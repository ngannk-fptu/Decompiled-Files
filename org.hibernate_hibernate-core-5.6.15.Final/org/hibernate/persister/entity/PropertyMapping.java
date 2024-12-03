/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.persister.entity;

import org.hibernate.QueryException;
import org.hibernate.type.Type;

public interface PropertyMapping {
    public Type toType(String var1) throws QueryException;

    public String[] toColumns(String var1, String var2) throws QueryException;

    public String[] toColumns(String var1) throws QueryException, UnsupportedOperationException;

    public Type getType();
}

