/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.description.modifier;

import net.bytebuddy.description.modifier.ModifierContributor;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public enum MethodArguments implements ModifierContributor.ForMethod
{
    PLAIN(0),
    VARARGS(128);

    private final int mask;

    private MethodArguments(int mask) {
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

    public boolean isVarArgs() {
        return this == VARARGS;
    }
}

