/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.jdbc.internal;

import java.sql.ResultSet;
import org.hibernate.engine.jdbc.ColumnNameCache;
import org.hibernate.engine.jdbc.ResultSetWrapperProxy;
import org.hibernate.engine.jdbc.spi.ResultSetWrapper;
import org.hibernate.service.ServiceRegistry;

@Deprecated
public class ResultSetWrapperImpl
implements ResultSetWrapper {
    private final ServiceRegistry serviceRegistry;

    public ResultSetWrapperImpl(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    public ResultSet wrap(ResultSet resultSet, ColumnNameCache columnNameCache) {
        return ResultSetWrapperProxy.generateProxy(resultSet, columnNameCache, this.serviceRegistry);
    }
}

