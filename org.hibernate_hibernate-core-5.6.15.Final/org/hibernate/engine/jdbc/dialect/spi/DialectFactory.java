/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.jdbc.dialect.spi;

import java.util.Map;
import org.hibernate.HibernateException;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.dialect.spi.DialectResolutionInfoSource;
import org.hibernate.service.Service;

public interface DialectFactory
extends Service {
    public Dialect buildDialect(Map var1, DialectResolutionInfoSource var2) throws HibernateException;
}

