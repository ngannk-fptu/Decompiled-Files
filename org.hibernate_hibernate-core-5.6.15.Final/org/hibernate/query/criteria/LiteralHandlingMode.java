/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.query.criteria;

import org.hibernate.HibernateException;

public enum LiteralHandlingMode {
    AUTO,
    BIND,
    INLINE;


    public static LiteralHandlingMode interpret(Object literalHandlingMode) {
        if (literalHandlingMode == null) {
            return AUTO;
        }
        if (literalHandlingMode instanceof LiteralHandlingMode) {
            return (LiteralHandlingMode)((Object)literalHandlingMode);
        }
        if (literalHandlingMode instanceof String) {
            for (LiteralHandlingMode value : LiteralHandlingMode.values()) {
                if (!value.name().equalsIgnoreCase((String)literalHandlingMode)) continue;
                return value;
            }
        }
        throw new HibernateException("Unrecognized literal_handling_mode value : " + literalHandlingMode + ".  Supported values include 'auto', 'inline', and 'bind'.");
    }
}

