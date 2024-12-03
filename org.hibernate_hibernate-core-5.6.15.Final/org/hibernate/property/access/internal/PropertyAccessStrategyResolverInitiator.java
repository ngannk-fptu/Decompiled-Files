/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.property.access.internal;

import java.util.Map;
import org.hibernate.boot.registry.StandardServiceInitiator;
import org.hibernate.property.access.internal.PropertyAccessStrategyResolverStandardImpl;
import org.hibernate.property.access.spi.PropertyAccessStrategyResolver;
import org.hibernate.service.spi.ServiceRegistryImplementor;

public class PropertyAccessStrategyResolverInitiator
implements StandardServiceInitiator<PropertyAccessStrategyResolver> {
    public static final PropertyAccessStrategyResolverInitiator INSTANCE = new PropertyAccessStrategyResolverInitiator();

    @Override
    public Class<PropertyAccessStrategyResolver> getServiceInitiated() {
        return PropertyAccessStrategyResolver.class;
    }

    @Override
    public PropertyAccessStrategyResolver initiateService(Map configurationValues, ServiceRegistryImplementor registry) {
        return new PropertyAccessStrategyResolverStandardImpl(registry);
    }
}

