/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal.cglib.core;

import com.google.inject.internal.asm.$Type;
import com.google.inject.internal.cglib.core.$Predicate;
import com.google.inject.internal.cglib.core.$TypeUtils;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;

public class $VisibilityPredicate
implements .Predicate {
    private boolean protectedOk;
    private String pkg;

    public $VisibilityPredicate(Class source, boolean protectedOk) {
        this.protectedOk = protectedOk;
        this.pkg = $TypeUtils.getPackageName($Type.getType(source));
    }

    public boolean evaluate(Object arg) {
        int mod;
        int n = mod = arg instanceof Member ? ((Member)arg).getModifiers() : ((Integer)arg).intValue();
        if (Modifier.isPrivate(mod)) {
            return false;
        }
        if (Modifier.isPublic(mod)) {
            return true;
        }
        if (Modifier.isProtected(mod)) {
            return this.protectedOk;
        }
        return this.pkg.equals($TypeUtils.getPackageName($Type.getType(((Member)arg).getDeclaringClass())));
    }
}

