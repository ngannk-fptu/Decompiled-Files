/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.resource.transaction.backend.jta.internal;

import org.hibernate.engine.transaction.jta.platform.spi.JtaPlatform;
import org.hibernate.resource.jdbc.spi.PhysicalConnectionHandlingMode;
import org.hibernate.resource.transaction.backend.jta.internal.DdlTransactionIsolatorJtaImpl;
import org.hibernate.resource.transaction.backend.jta.internal.JtaTransactionCoordinatorImpl;
import org.hibernate.resource.transaction.spi.DdlTransactionIsolator;
import org.hibernate.resource.transaction.spi.TransactionCoordinator;
import org.hibernate.resource.transaction.spi.TransactionCoordinatorBuilder;
import org.hibernate.resource.transaction.spi.TransactionCoordinatorOwner;
import org.hibernate.service.spi.ServiceRegistryAwareService;
import org.hibernate.service.spi.ServiceRegistryImplementor;
import org.hibernate.tool.schema.internal.exec.JdbcContext;

public class JtaTransactionCoordinatorBuilderImpl
implements TransactionCoordinatorBuilder,
ServiceRegistryAwareService {
    public static final String SHORT_NAME = "jta";
    private JtaPlatform jtaPlatform;

    @Override
    public TransactionCoordinator buildTransactionCoordinator(TransactionCoordinatorOwner owner, TransactionCoordinatorBuilder.Options options) {
        return new JtaTransactionCoordinatorImpl(this, owner, options.shouldAutoJoinTransaction(), this.jtaPlatform);
    }

    @Override
    public boolean isJta() {
        return true;
    }

    @Override
    public PhysicalConnectionHandlingMode getDefaultConnectionHandlingMode() {
        return PhysicalConnectionHandlingMode.DELAYED_ACQUISITION_AND_RELEASE_AFTER_STATEMENT;
    }

    @Override
    public DdlTransactionIsolator buildDdlTransactionIsolator(JdbcContext jdbcContext) {
        return new DdlTransactionIsolatorJtaImpl(jdbcContext);
    }

    @Override
    public void injectServices(ServiceRegistryImplementor serviceRegistry) {
        this.jtaPlatform = serviceRegistry.getService(JtaPlatform.class);
    }
}

