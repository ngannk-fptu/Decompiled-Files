/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.id;

import org.hibernate.HibernateException;
import org.hibernate.dialect.Dialect;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.id.PostInsertIdentityPersister;
import org.hibernate.id.insert.InsertGeneratedIdentifierDelegate;

public interface PostInsertIdentifierGenerator
extends IdentifierGenerator {
    public InsertGeneratedIdentifierDelegate getInsertGeneratedIdentifierDelegate(PostInsertIdentityPersister var1, Dialect var2, boolean var3) throws HibernateException;

    @Override
    default public boolean supportsJdbcBatchInserts() {
        return false;
    }
}

