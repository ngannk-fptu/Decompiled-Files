/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.query;

import org.hibernate.HibernateException;

public enum ImmutableEntityUpdateQueryHandlingMode {
    WARNING,
    EXCEPTION;


    public static ImmutableEntityUpdateQueryHandlingMode interpret(Object mode) {
        if (mode == null) {
            return WARNING;
        }
        if (mode instanceof ImmutableEntityUpdateQueryHandlingMode) {
            return (ImmutableEntityUpdateQueryHandlingMode)((Object)mode);
        }
        if (mode instanceof String) {
            for (ImmutableEntityUpdateQueryHandlingMode value : ImmutableEntityUpdateQueryHandlingMode.values()) {
                if (!value.name().equalsIgnoreCase((String)mode)) continue;
                return value;
            }
        }
        throw new HibernateException("Unrecognized immutable_entity_update_query_handling_mode value : " + mode + ".  Supported values include 'warning' and 'exception''.");
    }
}

