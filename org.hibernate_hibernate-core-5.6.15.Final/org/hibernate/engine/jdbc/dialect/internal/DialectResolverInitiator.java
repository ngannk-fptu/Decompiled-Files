/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.jdbc.dialect.internal;

import java.util.Map;
import org.hibernate.HibernateException;
import org.hibernate.boot.registry.StandardServiceInitiator;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.engine.jdbc.dialect.internal.DialectResolverSet;
import org.hibernate.engine.jdbc.dialect.internal.StandardDialectResolver;
import org.hibernate.engine.jdbc.dialect.spi.DialectResolver;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.service.spi.ServiceException;
import org.hibernate.service.spi.ServiceRegistryImplementor;

public class DialectResolverInitiator
implements StandardServiceInitiator<DialectResolver> {
    public static final DialectResolverInitiator INSTANCE = new DialectResolverInitiator();

    @Override
    public Class<DialectResolver> getServiceInitiated() {
        return DialectResolver.class;
    }

    @Override
    public DialectResolver initiateService(Map configurationValues, ServiceRegistryImplementor registry) {
        DialectResolverSet resolver = new DialectResolverSet();
        this.applyCustomerResolvers(resolver, registry, configurationValues);
        resolver.addResolver(new StandardDialectResolver());
        return resolver;
    }

    private void applyCustomerResolvers(DialectResolverSet resolver, ServiceRegistryImplementor registry, Map configurationValues) {
        String resolverImplNames = (String)configurationValues.get("hibernate.dialect_resolvers");
        if (StringHelper.isNotEmpty(resolverImplNames)) {
            ClassLoaderService classLoaderService = registry.getService(ClassLoaderService.class);
            for (String resolverImplName : StringHelper.split(", \n\r\f\t", resolverImplNames)) {
                try {
                    resolver.addResolver((DialectResolver)classLoaderService.classForName(resolverImplName).newInstance());
                }
                catch (HibernateException e) {
                    throw e;
                }
                catch (Exception e) {
                    throw new ServiceException("Unable to instantiate named dialect resolver [" + resolverImplName + "]", e);
                }
            }
        }
    }
}

