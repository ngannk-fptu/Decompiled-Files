/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal.cglib.core;

import com.google.inject.internal.cglib.core.$Predicate;
import java.lang.reflect.Member;

public class $RejectModifierPredicate
implements $Predicate {
    private int rejectMask;

    public $RejectModifierPredicate(int rejectMask) {
        this.rejectMask = rejectMask;
    }

    public boolean evaluate(Object arg) {
        return (((Member)arg).getModifiers() & this.rejectMask) == 0;
    }
}

