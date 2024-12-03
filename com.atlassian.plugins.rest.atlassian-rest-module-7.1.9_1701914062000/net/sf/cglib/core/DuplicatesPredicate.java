/*
 * Decompiled with CFR 0.152.
 */
package net.sf.cglib.core;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import net.sf.cglib.core.MethodWrapper;
import net.sf.cglib.core.Predicate;

public class DuplicatesPredicate
implements Predicate {
    private Set unique = new HashSet();

    public boolean evaluate(Object arg) {
        return this.unique.add(MethodWrapper.create((Method)arg));
    }
}

