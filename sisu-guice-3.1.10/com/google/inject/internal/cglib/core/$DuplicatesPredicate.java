/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal.cglib.core;

import com.google.inject.internal.cglib.core.$MethodWrapper;
import com.google.inject.internal.cglib.core.$Predicate;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

public class $DuplicatesPredicate
implements $Predicate {
    private Set unique = new HashSet();

    public boolean evaluate(Object arg) {
        return this.unique.add($MethodWrapper.create((Method)arg));
    }
}

