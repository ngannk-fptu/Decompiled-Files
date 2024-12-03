/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.description.modifier;

import net.bytebuddy.description.modifier.ModifierContributor;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public enum MethodStrictness implements ModifierContributor.ForMethod
{
    PLAIN(0),
    STRICT(2048);

    private final int mask;

    private MethodStrictness(int mask) {
        this.mask = mask;
    }

    @Override
    public int getMask() {
        return this.mask;
    }

    @Override
    public int getRange() {
        return 2048;
    }

    @Override
    public boolean isDefault() {
        return this == PLAIN;
    }

    public boolean isStrict() {
        return this == STRICT;
    }
}

