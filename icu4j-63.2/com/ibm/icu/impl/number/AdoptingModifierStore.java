/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.icu.impl.number;

import com.ibm.icu.impl.StandardPlural;
import com.ibm.icu.impl.number.Modifier;
import com.ibm.icu.impl.number.ModifierStore;

public class AdoptingModifierStore
implements ModifierStore {
    private final Modifier positive;
    private final Modifier zero;
    private final Modifier negative;
    final Modifier[] mods;
    boolean frozen;

    public AdoptingModifierStore(Modifier positive, Modifier zero, Modifier negative) {
        this.positive = positive;
        this.zero = zero;
        this.negative = negative;
        this.mods = null;
        this.frozen = true;
    }

    public AdoptingModifierStore() {
        this.positive = null;
        this.zero = null;
        this.negative = null;
        this.mods = new Modifier[3 * StandardPlural.COUNT];
        this.frozen = false;
    }

    public void setModifier(int signum, StandardPlural plural, Modifier mod) {
        assert (!this.frozen);
        this.mods[AdoptingModifierStore.getModIndex((int)signum, (StandardPlural)plural)] = mod;
    }

    public void freeze() {
        this.frozen = true;
    }

    public Modifier getModifierWithoutPlural(int signum) {
        assert (this.frozen);
        assert (this.mods == null);
        return signum == 0 ? this.zero : (signum < 0 ? this.negative : this.positive);
    }

    @Override
    public Modifier getModifier(int signum, StandardPlural plural) {
        assert (this.frozen);
        assert (this.positive == null);
        return this.mods[AdoptingModifierStore.getModIndex(signum, plural)];
    }

    private static int getModIndex(int signum, StandardPlural plural) {
        assert (signum >= -1 && signum <= 1);
        assert (plural != null);
        return plural.ordinal() * 3 + (signum + 1);
    }
}

