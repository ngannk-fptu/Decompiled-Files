/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.description.modifier;

import net.bytebuddy.description.modifier.ModifierContributor;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public enum TypeManifestation implements ModifierContributor.ForType
{
    PLAIN(0),
    FINAL(16),
    ABSTRACT(1024),
    INTERFACE(1536),
    ANNOTATION(9728);

    private final int mask;

    private TypeManifestation(int mask) {
        this.mask = mask;
    }

    @Override
    public int getMask() {
        return this.mask;
    }

    @Override
    public int getRange() {
        return 9744;
    }

    @Override
    public boolean isDefault() {
        return this == PLAIN;
    }

    public boolean isFinal() {
        return (this.mask & 0x10) != 0;
    }

    public boolean isAbstract() {
        return (this.mask & 0x400) != 0 && !this.isInterface();
    }

    public boolean isInterface() {
        return (this.mask & 0x200) != 0;
    }

    public boolean isAnnotation() {
        return (this.mask & 0x2000) != 0;
    }
}

