/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.description.modifier;

import net.bytebuddy.description.modifier.ModifierContributor;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public enum SyntheticState implements ModifierContributor.ForType,
ModifierContributor.ForMethod,
ModifierContributor.ForField,
ModifierContributor.ForParameter
{
    PLAIN(0),
    SYNTHETIC(4096);

    private final int mask;

    private SyntheticState(int mask) {
        this.mask = mask;
    }

    @Override
    public int getMask() {
        return this.mask;
    }

    @Override
    public int getRange() {
        return 4096;
    }

    @Override
    public boolean isDefault() {
        return this == PLAIN;
    }

    public boolean isSynthetic() {
        return this == SYNTHETIC;
    }
}

