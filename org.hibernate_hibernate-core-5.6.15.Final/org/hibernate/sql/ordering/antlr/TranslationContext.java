/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.sql.ordering.antlr;

import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.function.SQLFunctionRegistry;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.sql.ordering.antlr.ColumnMapper;

public interface TranslationContext {
    public SessionFactoryImplementor getSessionFactory();

    public Dialect getDialect();

    public SQLFunctionRegistry getSqlFunctionRegistry();

    public ColumnMapper getColumnMapper();
}

