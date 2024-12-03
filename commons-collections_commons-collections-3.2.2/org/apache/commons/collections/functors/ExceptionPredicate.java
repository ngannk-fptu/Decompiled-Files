/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.functors;

import java.io.Serializable;
import org.apache.commons.collections.FunctorException;
import org.apache.commons.collections.Predicate;

public final class ExceptionPredicate
implements Predicate,
Serializable {
    private static final long serialVersionUID = 7179106032121985545L;
    public static final Predicate INSTANCE = new ExceptionPredicate();

    public static Predicate getInstance() {
        return INSTANCE;
    }

    private ExceptionPredicate() {
    }

    public boolean evaluate(Object object) {
        throw new FunctorException("ExceptionPredicate invoked");
    }
}

