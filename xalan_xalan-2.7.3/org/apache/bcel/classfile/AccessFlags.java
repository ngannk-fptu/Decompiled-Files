/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.classfile;

public abstract class AccessFlags {
    @Deprecated
    protected int access_flags;

    public AccessFlags() {
    }

    public AccessFlags(int a) {
        this.access_flags = a;
    }

    public final int getAccessFlags() {
        return this.access_flags;
    }

    public final int getModifiers() {
        return this.access_flags;
    }

    public final boolean isAbstract() {
        return (this.access_flags & 0x400) != 0;
    }

    public final void isAbstract(boolean flag) {
        this.setFlag(1024, flag);
    }

    public final boolean isAnnotation() {
        return (this.access_flags & 0x2000) != 0;
    }

    public final void isAnnotation(boolean flag) {
        this.setFlag(8192, flag);
    }

    public final boolean isEnum() {
        return (this.access_flags & 0x4000) != 0;
    }

    public final void isEnum(boolean flag) {
        this.setFlag(16384, flag);
    }

    public final boolean isFinal() {
        return (this.access_flags & 0x10) != 0;
    }

    public final void isFinal(boolean flag) {
        this.setFlag(16, flag);
    }

    public final boolean isInterface() {
        return (this.access_flags & 0x200) != 0;
    }

    public final void isInterface(boolean flag) {
        this.setFlag(512, flag);
    }

    public final boolean isNative() {
        return (this.access_flags & 0x100) != 0;
    }

    public final void isNative(boolean flag) {
        this.setFlag(256, flag);
    }

    public final boolean isPrivate() {
        return (this.access_flags & 2) != 0;
    }

    public final void isPrivate(boolean flag) {
        this.setFlag(2, flag);
    }

    public final boolean isProtected() {
        return (this.access_flags & 4) != 0;
    }

    public final void isProtected(boolean flag) {
        this.setFlag(4, flag);
    }

    public final boolean isPublic() {
        return (this.access_flags & 1) != 0;
    }

    public final void isPublic(boolean flag) {
        this.setFlag(1, flag);
    }

    public final boolean isStatic() {
        return (this.access_flags & 8) != 0;
    }

    public final void isStatic(boolean flag) {
        this.setFlag(8, flag);
    }

    public final boolean isStrictfp() {
        return (this.access_flags & 0x800) != 0;
    }

    public final void isStrictfp(boolean flag) {
        this.setFlag(2048, flag);
    }

    public final boolean isSynchronized() {
        return (this.access_flags & 0x20) != 0;
    }

    public final void isSynchronized(boolean flag) {
        this.setFlag(32, flag);
    }

    public final boolean isSynthetic() {
        return (this.access_flags & 0x1000) != 0;
    }

    public final void isSynthetic(boolean flag) {
        this.setFlag(4096, flag);
    }

    public final boolean isTransient() {
        return (this.access_flags & 0x80) != 0;
    }

    public final void isTransient(boolean flag) {
        this.setFlag(128, flag);
    }

    public final boolean isVarArgs() {
        return (this.access_flags & 0x80) != 0;
    }

    public final void isVarArgs(boolean flag) {
        this.setFlag(128, flag);
    }

    public final boolean isVolatile() {
        return (this.access_flags & 0x40) != 0;
    }

    public final void isVolatile(boolean flag) {
        this.setFlag(64, flag);
    }

    public final void setAccessFlags(int accessFlags) {
        this.access_flags = accessFlags;
    }

    private void setFlag(int flag, boolean set) {
        if ((this.access_flags & flag) != 0) {
            if (!set) {
                this.access_flags ^= flag;
            }
        } else if (set) {
            this.access_flags |= flag;
        }
    }

    public final void setModifiers(int accessFlags) {
        this.setAccessFlags(accessFlags);
    }
}

