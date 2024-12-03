/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.bytecode.internal.none;

import org.hibernate.HibernateException;
import org.hibernate.bytecode.enhance.spi.EnhancementContext;
import org.hibernate.bytecode.enhance.spi.Enhancer;
import org.hibernate.bytecode.internal.none.NoProxyFactoryFactory;
import org.hibernate.bytecode.spi.BytecodeProvider;
import org.hibernate.bytecode.spi.ProxyFactoryFactory;
import org.hibernate.bytecode.spi.ReflectionOptimizer;

public final class BytecodeProviderImpl
implements BytecodeProvider {
    @Override
    public ProxyFactoryFactory getProxyFactoryFactory() {
        return new NoProxyFactoryFactory();
    }

    @Override
    public ReflectionOptimizer getReflectionOptimizer(Class clazz, String[] getterNames, String[] setterNames, Class[] types) {
        throw new HibernateException("Using the ReflectionOptimizer is not possible when the configured BytecodeProvider is 'none'. Disable hibernate.bytecode.use_reflection_optimizer or use a different BytecodeProvider");
    }

    @Override
    public Enhancer getEnhancer(EnhancementContext enhancementContext) {
        return null;
    }
}

