/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal.cglib.core;

import com.google.inject.internal.asm.$Type;
import com.google.inject.internal.cglib.core.$ClassInfo;
import com.google.inject.internal.cglib.core.$Signature;

public abstract class $MethodInfo {
    protected $MethodInfo() {
    }

    public abstract $ClassInfo getClassInfo();

    public abstract int getModifiers();

    public abstract $Signature getSignature();

    public abstract $Type[] getExceptionTypes();

    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof $MethodInfo)) {
            return false;
        }
        return this.getSignature().equals((($MethodInfo)o).getSignature());
    }

    public int hashCode() {
        return this.getSignature().hashCode();
    }

    public String toString() {
        return this.getSignature().toString();
    }
}

