/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.functors;

import java.io.Serializable;
import org.apache.commons.collections.Predicate;

public final class TruePredicate
implements Predicate,
Serializable {
    private static final long serialVersionUID = 3374767158756189740L;
    public static final Predicate INSTANCE = new TruePredicate();

    public static Predicate getInstance() {
        return INSTANCE;
    }

    private TruePredicate() {
    }

    public boolean evaluate(Object object) {
        return true;
    }
}

