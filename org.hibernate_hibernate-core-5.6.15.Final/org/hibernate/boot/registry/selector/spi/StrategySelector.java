/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.registry.selector.spi;

import java.util.Collection;
import java.util.concurrent.Callable;
import org.hibernate.boot.registry.selector.spi.StrategyCreator;
import org.hibernate.service.Service;

public interface StrategySelector
extends Service {
    public <T> void registerStrategyImplementor(Class<T> var1, String var2, Class<? extends T> var3);

    public <T> void unRegisterStrategyImplementor(Class<T> var1, Class<? extends T> var2);

    public <T> Class<? extends T> selectStrategyImplementor(Class<T> var1, String var2);

    public <T> T resolveStrategy(Class<T> var1, Object var2);

    public <T> T resolveDefaultableStrategy(Class<T> var1, Object var2, T var3);

    public <T> T resolveDefaultableStrategy(Class<T> var1, Object var2, Callable<T> var3);

    public <T> T resolveStrategy(Class<T> var1, Object var2, Callable<T> var3, StrategyCreator<T> var4);

    public <T> T resolveStrategy(Class<T> var1, Object var2, T var3, StrategyCreator<T> var4);

    public <T> Collection<Class<? extends T>> getRegisteredStrategyImplementors(Class<T> var1);
}

