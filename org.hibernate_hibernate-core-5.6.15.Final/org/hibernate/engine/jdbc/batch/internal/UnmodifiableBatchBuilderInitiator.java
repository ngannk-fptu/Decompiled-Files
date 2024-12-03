/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.jdbc.batch.internal;

import java.util.Map;
import org.hibernate.boot.registry.StandardServiceInitiator;
import org.hibernate.engine.jdbc.batch.internal.UnmodifiableBatchBuilderImpl;
import org.hibernate.engine.jdbc.batch.spi.BatchBuilder;
import org.hibernate.internal.util.config.ConfigurationHelper;
import org.hibernate.service.spi.ServiceException;
import org.hibernate.service.spi.ServiceRegistryImplementor;

public final class UnmodifiableBatchBuilderInitiator
implements StandardServiceInitiator<BatchBuilder> {
    public static final UnmodifiableBatchBuilderInitiator INSTANCE = new UnmodifiableBatchBuilderInitiator();

    @Override
    public Class<BatchBuilder> getServiceInitiated() {
        return BatchBuilder.class;
    }

    @Override
    public BatchBuilder initiateService(Map configurationValues, ServiceRegistryImplementor registry) {
        Object builder = configurationValues.get("hibernate.jdbc.batch.builder");
        if (builder == null) {
            return new UnmodifiableBatchBuilderImpl(ConfigurationHelper.getInt("hibernate.jdbc.batch_size", configurationValues, 1));
        }
        throw new ServiceException("This Hibernate ORM serviceregistry has been configured explicitly to use " + this.getClass() + " to create BatchBuilder instances; the property '" + "hibernate.jdbc.batch.builder" + "' is not supported.");
    }
}

