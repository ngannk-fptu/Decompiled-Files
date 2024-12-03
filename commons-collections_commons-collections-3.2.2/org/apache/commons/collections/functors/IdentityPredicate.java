/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.functors;

import java.io.Serializable;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.functors.NullPredicate;

public final class IdentityPredicate
implements Predicate,
Serializable {
    private static final long serialVersionUID = -89901658494523293L;
    private final Object iValue;

    public static Predicate getInstance(Object object) {
        if (object == null) {
            return NullPredicate.INSTANCE;
        }
        return new IdentityPredicate(object);
    }

    public IdentityPredicate(Object object) {
        this.iValue = object;
    }

    public boolean evaluate(Object object) {
        return this.iValue == object;
    }

    public Object getValue() {
        return this.iValue;
    }
}

