/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.description.modifier;

import net.bytebuddy.description.modifier.ModifierContributor;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public enum FieldPersistence implements ModifierContributor.ForField
{
    PLAIN(0),
    TRANSIENT(128);

    private final int mask;

    private FieldPersistence(int mask) {
        this.mask = mask;
    }

    @Override
    public int getMask() {
        return this.mask;
    }

    @Override
    public int getRange() {
        return 128;
    }

    @Override
    public boolean isDefault() {
        return this == PLAIN;
    }

    public boolean isTransient() {
        return (this.mask & 0x80) != 0;
    }
}

