/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.cglib.core;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import org.springframework.cglib.core.MethodWrapper;
import org.springframework.cglib.core.Predicate;

public class DuplicatesPredicate
implements Predicate {
    private Set unique = new HashSet();

    public boolean evaluate(Object arg) {
        return this.unique.add(MethodWrapper.create((Method)arg));
    }
}

