/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.jdbc.batch.internal;

import java.util.Map;
import org.hibernate.boot.registry.StandardServiceInitiator;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.engine.jdbc.batch.internal.BatchBuilderImpl;
import org.hibernate.engine.jdbc.batch.spi.BatchBuilder;
import org.hibernate.internal.util.config.ConfigurationHelper;
import org.hibernate.service.spi.ServiceException;
import org.hibernate.service.spi.ServiceRegistryImplementor;

public class BatchBuilderInitiator
implements StandardServiceInitiator<BatchBuilder> {
    public static final BatchBuilderInitiator INSTANCE = new BatchBuilderInitiator();
    public static final String BUILDER = "hibernate.jdbc.batch.builder";

    @Override
    public Class<BatchBuilder> getServiceInitiated() {
        return BatchBuilder.class;
    }

    @Override
    public BatchBuilder initiateService(Map configurationValues, ServiceRegistryImplementor registry) {
        Object builder = configurationValues.get(BUILDER);
        if (builder == null) {
            return new BatchBuilderImpl(ConfigurationHelper.getInt("hibernate.jdbc.batch_size", configurationValues, 1));
        }
        if (BatchBuilder.class.isInstance(builder)) {
            return (BatchBuilder)builder;
        }
        String builderClassName = builder.toString();
        try {
            return (BatchBuilder)registry.getService(ClassLoaderService.class).classForName(builderClassName).newInstance();
        }
        catch (Exception e) {
            throw new ServiceException("Could not build explicit BatchBuilder [" + builderClassName + "]", e);
        }
    }
}

