/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.resource.transaction.spi;

import org.hibernate.ConnectionAcquisitionMode;
import org.hibernate.ConnectionReleaseMode;
import org.hibernate.resource.jdbc.spi.PhysicalConnectionHandlingMode;
import org.hibernate.resource.transaction.backend.jdbc.internal.DdlTransactionIsolatorNonJtaImpl;
import org.hibernate.resource.transaction.backend.jta.internal.DdlTransactionIsolatorJtaImpl;
import org.hibernate.resource.transaction.spi.DdlTransactionIsolator;
import org.hibernate.resource.transaction.spi.TransactionCoordinator;
import org.hibernate.resource.transaction.spi.TransactionCoordinatorOwner;
import org.hibernate.service.Service;
import org.hibernate.tool.schema.internal.exec.JdbcContext;

public interface TransactionCoordinatorBuilder
extends Service {
    public TransactionCoordinator buildTransactionCoordinator(TransactionCoordinatorOwner var1, Options var2);

    public boolean isJta();

    public PhysicalConnectionHandlingMode getDefaultConnectionHandlingMode();

    @Deprecated
    default public ConnectionAcquisitionMode getDefaultConnectionAcquisitionMode() {
        return this.getDefaultConnectionHandlingMode().getAcquisitionMode();
    }

    @Deprecated
    default public ConnectionReleaseMode getDefaultConnectionReleaseMode() {
        return this.getDefaultConnectionHandlingMode().getReleaseMode();
    }

    default public DdlTransactionIsolator buildDdlTransactionIsolator(JdbcContext jdbcContext) {
        return this.isJta() ? new DdlTransactionIsolatorJtaImpl(jdbcContext) : new DdlTransactionIsolatorNonJtaImpl(jdbcContext);
    }

    public static interface Options {
        public boolean shouldAutoJoinTransaction();
    }
}

