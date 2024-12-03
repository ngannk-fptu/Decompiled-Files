/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.id;

import org.hibernate.HibernateException;

public enum SequenceMismatchStrategy {
    LOG,
    EXCEPTION,
    FIX,
    NONE;


    public static SequenceMismatchStrategy interpret(Object sequenceMismatchStrategy) {
        if (sequenceMismatchStrategy == null) {
            return EXCEPTION;
        }
        if (sequenceMismatchStrategy instanceof SequenceMismatchStrategy) {
            return (SequenceMismatchStrategy)((Object)sequenceMismatchStrategy);
        }
        if (sequenceMismatchStrategy instanceof String) {
            String sequenceMismatchStrategyString = (String)sequenceMismatchStrategy;
            for (SequenceMismatchStrategy value : SequenceMismatchStrategy.values()) {
                if (!value.name().equalsIgnoreCase(sequenceMismatchStrategyString)) continue;
                return value;
            }
        }
        throw new HibernateException("Unrecognized sequence.increment_size_mismatch_strategy value : [" + sequenceMismatchStrategy + "].  Supported values include [log], [exception], and [fix].");
    }
}

