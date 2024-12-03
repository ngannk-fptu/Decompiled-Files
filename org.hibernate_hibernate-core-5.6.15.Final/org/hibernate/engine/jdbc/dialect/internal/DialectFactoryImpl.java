/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.jdbc.dialect.internal;

import java.util.Map;
import org.hibernate.HibernateException;
import org.hibernate.boot.registry.selector.spi.StrategySelector;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.dialect.spi.DialectFactory;
import org.hibernate.engine.jdbc.dialect.spi.DialectResolutionInfo;
import org.hibernate.engine.jdbc.dialect.spi.DialectResolutionInfoSource;
import org.hibernate.engine.jdbc.dialect.spi.DialectResolver;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.service.spi.ServiceRegistryAwareService;
import org.hibernate.service.spi.ServiceRegistryImplementor;

public class DialectFactoryImpl
implements DialectFactory,
ServiceRegistryAwareService {
    private StrategySelector strategySelector;
    private DialectResolver dialectResolver;

    @Override
    public void injectServices(ServiceRegistryImplementor serviceRegistry) {
        this.strategySelector = serviceRegistry.getService(StrategySelector.class);
        this.dialectResolver = serviceRegistry.getService(DialectResolver.class);
    }

    public void setDialectResolver(DialectResolver dialectResolver) {
        this.dialectResolver = dialectResolver;
    }

    @Override
    public Dialect buildDialect(Map configValues, DialectResolutionInfoSource resolutionInfoSource) throws HibernateException {
        Object dialectReference = configValues.get("hibernate.dialect");
        if (!this.isEmpty(dialectReference)) {
            return this.constructDialect(dialectReference);
        }
        return this.determineDialect(resolutionInfoSource);
    }

    private boolean isEmpty(Object dialectReference) {
        if (dialectReference != null) {
            if (dialectReference instanceof String) {
                return StringHelper.isEmpty((String)dialectReference);
            }
            return false;
        }
        return true;
    }

    private Dialect constructDialect(Object dialectReference) {
        try {
            Dialect dialect = this.strategySelector.resolveStrategy(Dialect.class, dialectReference);
            if (dialect == null) {
                throw new HibernateException("Unable to construct requested dialect [" + dialectReference + "]");
            }
            return dialect;
        }
        catch (HibernateException e) {
            throw e;
        }
        catch (Exception e) {
            throw new HibernateException("Unable to construct requested dialect [" + dialectReference + "]", e);
        }
    }

    private Dialect determineDialect(DialectResolutionInfoSource resolutionInfoSource) {
        if (resolutionInfoSource == null) {
            throw new HibernateException("Access to DialectResolutionInfo cannot be null when 'hibernate.dialect' not set");
        }
        DialectResolutionInfo info = resolutionInfoSource.getDialectResolutionInfo();
        Dialect dialect = this.dialectResolver.resolveDialect(info);
        if (dialect == null) {
            throw new HibernateException("Unable to determine Dialect to use [name=" + info.getDatabaseName() + ", majorVersion=" + info.getDatabaseMajorVersion() + "]; user must register resolver or explicitly set 'hibernate.dialect'");
        }
        return dialect;
    }
}

