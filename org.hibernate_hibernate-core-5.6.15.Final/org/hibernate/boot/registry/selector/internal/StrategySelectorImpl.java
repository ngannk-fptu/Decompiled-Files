/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.boot.registry.selector.internal;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import org.hibernate.HibernateException;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.boot.registry.classloading.spi.ClassLoadingException;
import org.hibernate.boot.registry.selector.internal.LazyServiceResolver;
import org.hibernate.boot.registry.selector.spi.StrategyCreator;
import org.hibernate.boot.registry.selector.spi.StrategySelectionException;
import org.hibernate.boot.registry.selector.spi.StrategySelector;
import org.jboss.logging.Logger;

public class StrategySelectorImpl
implements StrategySelector {
    private static final Logger log = Logger.getLogger(StrategySelectorImpl.class);
    private static final StrategyCreator STANDARD_STRATEGY_CREATOR = strategyClass -> {
        try {
            return strategyClass.newInstance();
        }
        catch (Exception e) {
            throw new StrategySelectionException(String.format("Could not instantiate named strategy class [%s]", strategyClass.getName()), e);
        }
    };
    private final Map<Class, Map<String, Class>> namedStrategyImplementorByStrategyMap = new ConcurrentHashMap<Class, Map<String, Class>>();
    private final Map<Class, LazyServiceResolver> lazyStrategyImplementorByStrategyMap = new ConcurrentHashMap<Class, LazyServiceResolver>();
    private final ClassLoaderService classLoaderService;

    public StrategySelectorImpl(ClassLoaderService classLoaderService) {
        this.classLoaderService = classLoaderService;
    }

    public <T> void registerStrategyLazily(Class<T> strategy, LazyServiceResolver<T> resolver) {
        LazyServiceResolver<T> previous = this.lazyStrategyImplementorByStrategyMap.put(strategy, resolver);
        if (previous != null) {
            throw new HibernateException("Detected a second LazyServiceResolver replacing an existing LazyServiceResolver implementation for strategy " + strategy.getName());
        }
    }

    @Override
    public <T> void registerStrategyImplementor(Class<T> strategy, String name, Class<? extends T> implementation) {
        Class<T> old;
        Map<String, Class> namedStrategyImplementorMap = this.namedStrategyImplementorByStrategyMap.get(strategy);
        if (namedStrategyImplementorMap == null) {
            namedStrategyImplementorMap = new ConcurrentHashMap<String, Class>();
            this.namedStrategyImplementorByStrategyMap.put(strategy, namedStrategyImplementorMap);
        }
        if ((old = namedStrategyImplementorMap.put(name, implementation)) == null) {
            if (log.isTraceEnabled()) {
                log.trace((Object)String.format("Registering named strategy selector [%s] : [%s] -> [%s]", strategy.getName(), name, implementation.getName()));
            }
        } else if (log.isDebugEnabled()) {
            log.debug((Object)String.format("Registering named strategy selector [%s] : [%s] -> [%s] (replacing [%s])", strategy.getName(), name, implementation.getName(), old.getName()));
        }
    }

    @Override
    public <T> void unRegisterStrategyImplementor(Class<T> strategy, Class<? extends T> implementation) {
        Map<String, Class> namedStrategyImplementorMap = this.namedStrategyImplementorByStrategyMap.get(strategy);
        if (namedStrategyImplementorMap == null) {
            log.debug((Object)"Named strategy map did not exist on call to un-register");
            return;
        }
        Iterator<Class> itr = namedStrategyImplementorMap.values().iterator();
        while (itr.hasNext()) {
            Class registered = itr.next();
            if (!registered.equals(implementation)) continue;
            itr.remove();
        }
        if (namedStrategyImplementorMap.isEmpty()) {
            this.namedStrategyImplementorByStrategyMap.remove(strategy);
        }
    }

    @Override
    public <T> Class<? extends T> selectStrategyImplementor(Class<T> strategy, String name) {
        Class resolve;
        Class registered;
        Map<String, Class> namedStrategyImplementorMap = this.namedStrategyImplementorByStrategyMap.get(strategy);
        if (namedStrategyImplementorMap != null && (registered = namedStrategyImplementorMap.get(name)) != null) {
            return registered;
        }
        LazyServiceResolver lazyServiceResolver = this.lazyStrategyImplementorByStrategyMap.get(strategy);
        if (lazyServiceResolver != null && (resolve = lazyServiceResolver.resolve(name)) != null) {
            return resolve;
        }
        try {
            return this.classLoaderService.classForName(name);
        }
        catch (ClassLoadingException e) {
            throw new StrategySelectionException("Unable to resolve name [" + name + "] as strategy [" + strategy.getName() + "]", (Throwable)((Object)e));
        }
    }

    @Override
    public <T> T resolveStrategy(Class<T> strategy, Object strategyReference) {
        return this.resolveDefaultableStrategy(strategy, strategyReference, (T)null);
    }

    @Override
    public <T> T resolveDefaultableStrategy(Class<T> strategy, Object strategyReference, T defaultValue) {
        return (T)this.resolveDefaultableStrategy(strategy, strategyReference, () -> defaultValue);
    }

    @Override
    public <T> T resolveDefaultableStrategy(Class<T> strategy, Object strategyReference, Callable<T> defaultResolver) {
        return this.resolveStrategy(strategy, strategyReference, defaultResolver, STANDARD_STRATEGY_CREATOR);
    }

    @Override
    public <T> T resolveStrategy(Class<T> strategy, Object strategyReference, T defaultValue, StrategyCreator<T> creator) {
        return (T)this.resolveStrategy(strategy, strategyReference, () -> defaultValue, creator);
    }

    public Collection getRegisteredStrategyImplementors(Class strategy) {
        LazyServiceResolver lazyServiceResolver = this.lazyStrategyImplementorByStrategyMap.get(strategy);
        if (lazyServiceResolver != null) {
            throw new StrategySelectionException("Can't use this method on for strategy types which are embedded in the core library");
        }
        Map<String, Class> registrations = this.namedStrategyImplementorByStrategyMap.get(strategy);
        if (registrations == null) {
            return Collections.emptySet();
        }
        return new HashSet<Class>(registrations.values());
    }

    @Override
    public <T> T resolveStrategy(Class<T> strategy, Object strategyReference, Callable<T> defaultResolver, StrategyCreator<T> creator) {
        if (strategyReference == null) {
            try {
                return defaultResolver.call();
            }
            catch (Exception e) {
                throw new StrategySelectionException("Default-resolver threw exception", e);
            }
        }
        if (strategy.isInstance(strategyReference)) {
            return strategy.cast(strategyReference);
        }
        Class<T> implementationClass = Class.class.isInstance(strategyReference) ? (Class<T>)strategyReference : this.selectStrategyImplementor(strategy, strategyReference.toString());
        try {
            return creator.create(implementationClass);
        }
        catch (Exception e) {
            throw new StrategySelectionException(String.format("Could not instantiate named strategy class [%s]", implementationClass.getName()), e);
        }
    }
}

