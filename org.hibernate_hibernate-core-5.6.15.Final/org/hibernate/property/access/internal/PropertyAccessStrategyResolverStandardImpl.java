/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.property.access.internal;

import org.hibernate.EntityMode;
import org.hibernate.HibernateException;
import org.hibernate.boot.registry.selector.spi.StrategySelector;
import org.hibernate.engine.internal.ManagedTypeHelper;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.property.access.internal.PropertyAccessStrategyEnhancedImpl;
import org.hibernate.property.access.spi.BuiltInPropertyAccessStrategies;
import org.hibernate.property.access.spi.PropertyAccessStrategy;
import org.hibernate.property.access.spi.PropertyAccessStrategyResolver;
import org.hibernate.service.ServiceRegistry;

public class PropertyAccessStrategyResolverStandardImpl
implements PropertyAccessStrategyResolver {
    private final ServiceRegistry serviceRegistry;
    private StrategySelector strategySelectorService;

    public PropertyAccessStrategyResolverStandardImpl(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    public PropertyAccessStrategy resolvePropertyAccessStrategy(Class containerClass, String explicitAccessStrategyName, EntityMode entityMode) {
        if ((BuiltInPropertyAccessStrategies.BASIC.getExternalName().equals(explicitAccessStrategyName) || BuiltInPropertyAccessStrategies.FIELD.getExternalName().equals(explicitAccessStrategyName) || BuiltInPropertyAccessStrategies.MIXED.getExternalName().equals(explicitAccessStrategyName)) && ManagedTypeHelper.isManagedType(containerClass)) {
            return PropertyAccessStrategyEnhancedImpl.INSTANCE;
        }
        if (StringHelper.isNotEmpty(explicitAccessStrategyName)) {
            return this.resolveExplicitlyNamedPropertyAccessStrategy(explicitAccessStrategyName);
        }
        if (entityMode == EntityMode.MAP) {
            return BuiltInPropertyAccessStrategies.MAP.getStrategy();
        }
        return BuiltInPropertyAccessStrategies.BASIC.getStrategy();
    }

    protected PropertyAccessStrategy resolveExplicitlyNamedPropertyAccessStrategy(String explicitAccessStrategyName) {
        BuiltInPropertyAccessStrategies builtInStrategyEnum = BuiltInPropertyAccessStrategies.interpret(explicitAccessStrategyName);
        if (builtInStrategyEnum != null) {
            return builtInStrategyEnum.getStrategy();
        }
        return this.strategySelectorService().resolveStrategy(PropertyAccessStrategy.class, explicitAccessStrategyName);
    }

    protected StrategySelector strategySelectorService() {
        if (this.strategySelectorService == null) {
            if (this.serviceRegistry == null) {
                throw new HibernateException("ServiceRegistry not yet injected; PropertyAccessStrategyResolver not ready for use.");
            }
            this.strategySelectorService = this.serviceRegistry.getService(StrategySelector.class);
        }
        return this.strategySelectorService;
    }
}

