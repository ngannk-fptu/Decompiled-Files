/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.functors;

import java.io.Serializable;
import org.apache.commons.collections.Predicate;

public final class NullPredicate
implements Predicate,
Serializable {
    private static final long serialVersionUID = 7533784454832764388L;
    public static final Predicate INSTANCE = new NullPredicate();

    public static Predicate getInstance() {
        return INSTANCE;
    }

    private NullPredicate() {
    }

    public boolean evaluate(Object object) {
        return object == null;
    }
}

