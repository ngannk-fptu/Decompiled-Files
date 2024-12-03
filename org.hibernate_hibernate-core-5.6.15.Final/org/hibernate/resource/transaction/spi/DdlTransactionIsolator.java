/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.resource.transaction.spi;

import java.sql.Connection;
import org.hibernate.tool.schema.internal.exec.JdbcContext;

public interface DdlTransactionIsolator {
    public JdbcContext getJdbcContext();

    @Deprecated
    public void prepare();

    public Connection getIsolatedConnection();

    public void release();
}

