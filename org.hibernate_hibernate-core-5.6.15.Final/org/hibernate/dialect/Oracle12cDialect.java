/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect;

import org.hibernate.boot.model.TypeContributions;
import org.hibernate.dialect.Oracle10gDialect;
import org.hibernate.dialect.identity.IdentityColumnSupport;
import org.hibernate.dialect.identity.Oracle12cIdentityColumnSupport;
import org.hibernate.dialect.pagination.AbstractLimitHandler;
import org.hibernate.dialect.pagination.LimitHandler;
import org.hibernate.dialect.pagination.Oracle12LimitHandler;
import org.hibernate.engine.config.spi.ConfigurationService;
import org.hibernate.engine.config.spi.StandardConverters;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.MaterializedBlobType;
import org.hibernate.type.WrappedMaterializedBlobType;

public class Oracle12cDialect
extends Oracle10gDialect {
    public static final String PREFER_LONG_RAW = "hibernate.dialect.oracle.prefer_long_raw";
    private static final AbstractLimitHandler LIMIT_HANDLER = Oracle12LimitHandler.INSTANCE;

    public Oracle12cDialect() {
        this.getDefaultProperties().setProperty("hibernate.jdbc.batch_versioned_data", "true");
    }

    @Override
    public void contributeTypes(TypeContributions typeContributions, ServiceRegistry serviceRegistry) {
        super.contributeTypes(typeContributions, serviceRegistry);
        boolean preferLong = serviceRegistry.getService(ConfigurationService.class).getSetting(PREFER_LONG_RAW, StandardConverters.BOOLEAN, Boolean.valueOf(false));
        if (!preferLong) {
            typeContributions.contributeType(MaterializedBlobType.INSTANCE, "byte[]", byte[].class.getName());
            typeContributions.contributeType(WrappedMaterializedBlobType.INSTANCE, "Byte[]", Byte[].class.getName());
        }
    }

    @Override
    protected void registerDefaultProperties() {
        super.registerDefaultProperties();
        this.getDefaultProperties().setProperty("hibernate.jdbc.use_get_generated_keys", "true");
    }

    @Override
    public String getNativeIdentifierGeneratorStrategy() {
        return "sequence";
    }

    @Override
    public IdentityColumnSupport getIdentityColumnSupport() {
        return new Oracle12cIdentityColumnSupport();
    }

    @Override
    public LimitHandler getLimitHandler() {
        return LIMIT_HANDLER;
    }
}

