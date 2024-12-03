/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.hql.internal.ast.exec;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.QueryParameters;
import org.hibernate.engine.spi.SharedSessionContractImplementor;

public interface StatementExecutor {
    public String[] getSqlStatements();

    public int execute(QueryParameters var1, SharedSessionContractImplementor var2) throws HibernateException;
}

