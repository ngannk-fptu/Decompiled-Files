/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.resource.jdbc.spi;

import java.sql.Connection;

@Deprecated
public interface JdbcObserver {
    public void jdbcConnectionAcquisitionStart();

    public void jdbcConnectionAcquisitionEnd(Connection var1);

    public void jdbcConnectionReleaseStart();

    public void jdbcConnectionReleaseEnd();

    public void jdbcPrepareStatementStart();

    public void jdbcPrepareStatementEnd();

    public void jdbcExecuteStatementStart();

    public void jdbcExecuteStatementEnd();

    public void jdbcExecuteBatchStart();

    public void jdbcExecuteBatchEnd();

    default public void jdbcReleaseRegistryResourcesStart() {
    }

    default public void jdbcReleaseRegistryResourcesEnd() {
    }
}

