/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.description.modifier;

import net.bytebuddy.description.modifier.ModifierContributor;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public enum MethodManifestation implements ModifierContributor.ForMethod
{
    PLAIN(0),
    NATIVE(256),
    ABSTRACT(1024),
    FINAL(16),
    FINAL_NATIVE(272),
    BRIDGE(64),
    FINAL_BRIDGE(80);

    private final int mask;

    private MethodManifestation(int mask) {
        this.mask = mask;
    }

    @Override
    public int getMask() {
        return this.mask;
    }

    @Override
    public int getRange() {
        return 1360;
    }

    @Override
    public boolean isDefault() {
        return this == PLAIN;
    }

    public boolean isNative() {
        return (this.mask & 0x100) != 0;
    }

    public boolean isAbstract() {
        return (this.mask & 0x400) != 0;
    }

    public boolean isFinal() {
        return (this.mask & 0x10) != 0;
    }

    public boolean isBridge() {
        return (this.mask & 0x40) != 0;
    }
}

