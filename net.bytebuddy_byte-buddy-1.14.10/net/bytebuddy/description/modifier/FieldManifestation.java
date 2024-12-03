/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.description.modifier;

import net.bytebuddy.description.modifier.ModifierContributor;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public enum FieldManifestation implements ModifierContributor.ForField
{
    PLAIN(0),
    FINAL(16),
    VOLATILE(64);

    private final int mask;

    private FieldManifestation(int mask) {
        this.mask = mask;
    }

    @Override
    public int getMask() {
        return this.mask;
    }

    @Override
    public int getRange() {
        return 80;
    }

    @Override
    public boolean isDefault() {
        return this == PLAIN;
    }

    public boolean isFinal() {
        return (this.mask & 0x10) != 0;
    }

    public boolean isVolatile() {
        return (this.mask & 0x40) != 0;
    }

    public boolean isPlain() {
        return !this.isFinal() && !this.isVolatile();
    }
}

