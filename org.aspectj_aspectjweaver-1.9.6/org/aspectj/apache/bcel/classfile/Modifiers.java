/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.apache.bcel.classfile;

public abstract class Modifiers {
    protected int modifiers;

    public Modifiers() {
    }

    public Modifiers(int a) {
        this.modifiers = a;
    }

    public final int getModifiers() {
        return this.modifiers;
    }

    public final void setModifiers(int modifiers) {
        this.modifiers = modifiers;
    }

    public final boolean isPublic() {
        return (this.modifiers & 1) != 0;
    }

    public final boolean isPrivate() {
        return (this.modifiers & 2) != 0;
    }

    public final boolean isProtected() {
        return (this.modifiers & 4) != 0;
    }

    public final boolean isStatic() {
        return (this.modifiers & 8) != 0;
    }

    public final boolean isFinal() {
        return (this.modifiers & 0x10) != 0;
    }

    public final boolean isSynchronized() {
        return (this.modifiers & 0x20) != 0;
    }

    public final boolean isVolatile() {
        return (this.modifiers & 0x40) != 0;
    }

    public final boolean isTransient() {
        return (this.modifiers & 0x80) != 0;
    }

    public final boolean isNative() {
        return (this.modifiers & 0x100) != 0;
    }

    public final boolean isInterface() {
        return (this.modifiers & 0x200) != 0;
    }

    public final boolean isAbstract() {
        return (this.modifiers & 0x400) != 0;
    }

    public final boolean isStrictfp() {
        return (this.modifiers & 0x800) != 0;
    }

    public final boolean isVarargs() {
        return (this.modifiers & 0x80) != 0;
    }

    public final boolean isBridge() {
        return (this.modifiers & 0x40) != 0;
    }
}

