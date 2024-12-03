/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.exec.spi;

import org.hibernate.loader.plan.exec.process.spi.ResultSetProcessor;

public interface LoadQueryDetails {
    public String getSqlStatement();

    public ResultSetProcessor getResultSetProcessor();
}

