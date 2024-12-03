/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.bytecode.spi;

import org.hibernate.bytecode.enhance.spi.EnhancementContext;
import org.hibernate.bytecode.enhance.spi.Enhancer;
import org.hibernate.bytecode.spi.ProxyFactoryFactory;
import org.hibernate.bytecode.spi.ReflectionOptimizer;
import org.hibernate.service.Service;

public interface BytecodeProvider
extends Service {
    public ProxyFactoryFactory getProxyFactoryFactory();

    public ReflectionOptimizer getReflectionOptimizer(Class var1, String[] var2, String[] var3, Class[] var4);

    public Enhancer getEnhancer(EnhancementContext var1);

    default public void resetCaches() {
    }
}

