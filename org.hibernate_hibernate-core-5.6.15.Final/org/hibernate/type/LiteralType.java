/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type;

import org.hibernate.dialect.Dialect;

public interface LiteralType<T> {
    public String objectToSQLString(T var1, Dialect var2) throws Exception;
}

