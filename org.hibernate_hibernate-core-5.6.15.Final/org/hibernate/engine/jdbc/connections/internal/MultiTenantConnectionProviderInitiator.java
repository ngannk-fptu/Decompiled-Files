/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.engine.jdbc.connections.internal;

import java.util.Map;
import org.hibernate.MultiTenancyStrategy;
import org.hibernate.boot.registry.StandardServiceInitiator;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.boot.registry.classloading.spi.ClassLoadingException;
import org.hibernate.engine.jdbc.connections.spi.DataSourceBasedMultiTenantConnectionProviderImpl;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.hibernate.service.spi.ServiceException;
import org.hibernate.service.spi.ServiceRegistryImplementor;
import org.jboss.logging.Logger;

public class MultiTenantConnectionProviderInitiator
implements StandardServiceInitiator<MultiTenantConnectionProvider> {
    private static final Logger log = Logger.getLogger(MultiTenantConnectionProviderInitiator.class);
    public static final MultiTenantConnectionProviderInitiator INSTANCE = new MultiTenantConnectionProviderInitiator();

    @Override
    public Class<MultiTenantConnectionProvider> getServiceInitiated() {
        return MultiTenantConnectionProvider.class;
    }

    @Override
    public MultiTenantConnectionProvider initiateService(Map configurationValues, ServiceRegistryImplementor registry) {
        Class implClass;
        MultiTenancyStrategy strategy = MultiTenancyStrategy.determineMultiTenancyStrategy(configurationValues);
        if (!strategy.requiresMultiTenantConnectionProvider()) {
            return null;
        }
        Object configValue = configurationValues.get("hibernate.multi_tenant_connection_provider");
        if (configValue == null) {
            Object dataSourceConfigValue = configurationValues.get("hibernate.connection.datasource");
            if (dataSourceConfigValue != null && String.class.isInstance(dataSourceConfigValue)) {
                return new DataSourceBasedMultiTenantConnectionProviderImpl();
            }
            return null;
        }
        if (MultiTenantConnectionProvider.class.isInstance(configValue)) {
            return (MultiTenantConnectionProvider)configValue;
        }
        if (Class.class.isInstance(configValue)) {
            implClass = (Class)configValue;
        } else {
            String className = configValue.toString();
            ClassLoaderService classLoaderService = registry.getService(ClassLoaderService.class);
            try {
                implClass = classLoaderService.classForName(className);
            }
            catch (ClassLoadingException cle) {
                log.warn((Object)("Unable to locate specified class [" + className + "]"), (Throwable)((Object)cle));
                throw new ServiceException("Unable to locate specified multi-tenant connection provider [" + className + "]");
            }
        }
        try {
            return (MultiTenantConnectionProvider)implClass.newInstance();
        }
        catch (Exception e) {
            log.warn((Object)("Unable to instantiate specified class [" + implClass.getName() + "]"), (Throwable)e);
            throw new ServiceException("Unable to instantiate specified multi-tenant connection provider [" + implClass.getName() + "]");
        }
    }
}

