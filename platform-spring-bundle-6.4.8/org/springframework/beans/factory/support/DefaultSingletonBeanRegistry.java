/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans.factory.support;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanCreationNotAllowedException;
import org.springframework.beans.factory.BeanCurrentlyInCreationException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.SingletonBeanRegistry;
import org.springframework.core.SimpleAliasRegistry;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class DefaultSingletonBeanRegistry
extends SimpleAliasRegistry
implements SingletonBeanRegistry {
    private static final int SUPPRESSED_EXCEPTIONS_LIMIT = 100;
    private final Map<String, Object> singletonObjects = new ConcurrentHashMap<String, Object>(256);
    private final Map<String, ObjectFactory<?>> singletonFactories = new HashMap(16);
    private final Map<String, Object> earlySingletonObjects = new ConcurrentHashMap<String, Object>(16);
    private final Set<String> registeredSingletons = new LinkedHashSet<String>(256);
    private final Set<String> singletonsCurrentlyInCreation = Collections.newSetFromMap(new ConcurrentHashMap(16));
    private final Set<String> inCreationCheckExclusions = Collections.newSetFromMap(new ConcurrentHashMap(16));
    @Nullable
    private Set<Exception> suppressedExceptions;
    private boolean singletonsCurrentlyInDestruction = false;
    private final Map<String, DisposableBean> disposableBeans = new LinkedHashMap<String, DisposableBean>();
    private final Map<String, Set<String>> containedBeanMap = new ConcurrentHashMap<String, Set<String>>(16);
    private final Map<String, Set<String>> dependentBeanMap = new ConcurrentHashMap<String, Set<String>>(64);
    private final Map<String, Set<String>> dependenciesForBeanMap = new ConcurrentHashMap<String, Set<String>>(64);

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void registerSingleton(String beanName, Object singletonObject) throws IllegalStateException {
        Assert.notNull((Object)beanName, "Bean name must not be null");
        Assert.notNull(singletonObject, "Singleton object must not be null");
        Map<String, Object> map = this.singletonObjects;
        synchronized (map) {
            Object oldObject = this.singletonObjects.get(beanName);
            if (oldObject != null) {
                throw new IllegalStateException("Could not register object [" + singletonObject + "] under bean name '" + beanName + "': there is already object [" + oldObject + "] bound");
            }
            this.addSingleton(beanName, singletonObject);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void addSingleton(String beanName, Object singletonObject) {
        Map<String, Object> map = this.singletonObjects;
        synchronized (map) {
            this.singletonObjects.put(beanName, singletonObject);
            this.singletonFactories.remove(beanName);
            this.earlySingletonObjects.remove(beanName);
            this.registeredSingletons.add(beanName);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void addSingletonFactory(String beanName, ObjectFactory<?> singletonFactory) {
        Assert.notNull(singletonFactory, "Singleton factory must not be null");
        Map<String, Object> map = this.singletonObjects;
        synchronized (map) {
            if (!this.singletonObjects.containsKey(beanName)) {
                this.singletonFactories.put(beanName, singletonFactory);
                this.earlySingletonObjects.remove(beanName);
                this.registeredSingletons.add(beanName);
            }
        }
    }

    @Override
    @Nullable
    public Object getSingleton(String beanName) {
        return this.getSingleton(beanName, true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Nullable
    protected Object getSingleton(String beanName, boolean allowEarlyReference) {
        Object singletonObject = this.singletonObjects.get(beanName);
        if (singletonObject == null && this.isSingletonCurrentlyInCreation(beanName) && (singletonObject = this.earlySingletonObjects.get(beanName)) == null && allowEarlyReference) {
            Map<String, Object> map = this.singletonObjects;
            synchronized (map) {
                ObjectFactory<?> singletonFactory;
                singletonObject = this.singletonObjects.get(beanName);
                if (singletonObject == null && (singletonObject = this.earlySingletonObjects.get(beanName)) == null && (singletonFactory = this.singletonFactories.get(beanName)) != null) {
                    singletonObject = singletonFactory.getObject();
                    this.earlySingletonObjects.put(beanName, singletonObject);
                    this.singletonFactories.remove(beanName);
                }
            }
        }
        return singletonObject;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Object getSingleton(String beanName, ObjectFactory<?> singletonFactory) {
        Assert.notNull((Object)beanName, "Bean name must not be null");
        Map<String, Object> map = this.singletonObjects;
        synchronized (map) {
            Object singletonObject = this.singletonObjects.get(beanName);
            if (singletonObject == null) {
                boolean recordSuppressedExceptions;
                if (this.singletonsCurrentlyInDestruction) {
                    throw new BeanCreationNotAllowedException(beanName, "Singleton bean creation not allowed while singletons of this factory are in destruction (Do not request a bean from a BeanFactory in a destroy method implementation!)");
                }
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug((Object)("Creating shared instance of singleton bean '" + beanName + "'"));
                }
                this.beforeSingletonCreation(beanName);
                boolean newSingleton = false;
                boolean bl = recordSuppressedExceptions = this.suppressedExceptions == null;
                if (recordSuppressedExceptions) {
                    this.suppressedExceptions = new LinkedHashSet<Exception>();
                }
                try {
                    singletonObject = singletonFactory.getObject();
                    newSingleton = true;
                }
                catch (IllegalStateException ex) {
                    singletonObject = this.singletonObjects.get(beanName);
                    if (singletonObject == null) {
                        throw ex;
                    }
                }
                catch (BeanCreationException ex) {
                    if (recordSuppressedExceptions) {
                        for (Exception suppressedException : this.suppressedExceptions) {
                            ex.addRelatedCause(suppressedException);
                        }
                    }
                    throw ex;
                }
                finally {
                    if (recordSuppressedExceptions) {
                        this.suppressedExceptions = null;
                    }
                    this.afterSingletonCreation(beanName);
                }
                if (newSingleton) {
                    this.addSingleton(beanName, singletonObject);
                }
            }
            return singletonObject;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void onSuppressedException(Exception ex) {
        Map<String, Object> map = this.singletonObjects;
        synchronized (map) {
            if (this.suppressedExceptions != null && this.suppressedExceptions.size() < 100) {
                this.suppressedExceptions.add(ex);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void removeSingleton(String beanName) {
        Map<String, Object> map = this.singletonObjects;
        synchronized (map) {
            this.singletonObjects.remove(beanName);
            this.singletonFactories.remove(beanName);
            this.earlySingletonObjects.remove(beanName);
            this.registeredSingletons.remove(beanName);
        }
    }

    @Override
    public boolean containsSingleton(String beanName) {
        return this.singletonObjects.containsKey(beanName);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String[] getSingletonNames() {
        Map<String, Object> map = this.singletonObjects;
        synchronized (map) {
            return StringUtils.toStringArray(this.registeredSingletons);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int getSingletonCount() {
        Map<String, Object> map = this.singletonObjects;
        synchronized (map) {
            return this.registeredSingletons.size();
        }
    }

    public void setCurrentlyInCreation(String beanName, boolean inCreation) {
        Assert.notNull((Object)beanName, "Bean name must not be null");
        if (!inCreation) {
            this.inCreationCheckExclusions.add(beanName);
        } else {
            this.inCreationCheckExclusions.remove(beanName);
        }
    }

    public boolean isCurrentlyInCreation(String beanName) {
        Assert.notNull((Object)beanName, "Bean name must not be null");
        return !this.inCreationCheckExclusions.contains(beanName) && this.isActuallyInCreation(beanName);
    }

    protected boolean isActuallyInCreation(String beanName) {
        return this.isSingletonCurrentlyInCreation(beanName);
    }

    public boolean isSingletonCurrentlyInCreation(@Nullable String beanName) {
        return this.singletonsCurrentlyInCreation.contains(beanName);
    }

    protected void beforeSingletonCreation(String beanName) {
        if (!this.inCreationCheckExclusions.contains(beanName) && !this.singletonsCurrentlyInCreation.add(beanName)) {
            throw new BeanCurrentlyInCreationException(beanName);
        }
    }

    protected void afterSingletonCreation(String beanName) {
        if (!this.inCreationCheckExclusions.contains(beanName) && !this.singletonsCurrentlyInCreation.remove(beanName)) {
            throw new IllegalStateException("Singleton '" + beanName + "' isn't currently in creation");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void registerDisposableBean(String beanName, DisposableBean bean2) {
        Map<String, DisposableBean> map = this.disposableBeans;
        synchronized (map) {
            this.disposableBeans.put(beanName, bean2);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void registerContainedBean(String containedBeanName, String containingBeanName) {
        Map<String, Set<String>> map = this.containedBeanMap;
        synchronized (map) {
            Set containedBeans = this.containedBeanMap.computeIfAbsent(containingBeanName, k -> new LinkedHashSet(8));
            if (!containedBeans.add(containedBeanName)) {
                return;
            }
        }
        this.registerDependentBean(containedBeanName, containingBeanName);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void registerDependentBean(String beanName, String dependentBeanName) {
        String canonicalName = this.canonicalName(beanName);
        Map<String, Set<String>> map = this.dependentBeanMap;
        synchronized (map) {
            Set dependentBeans = this.dependentBeanMap.computeIfAbsent(canonicalName, k -> new LinkedHashSet(8));
            if (!dependentBeans.add(dependentBeanName)) {
                return;
            }
        }
        map = this.dependenciesForBeanMap;
        synchronized (map) {
            Set dependenciesForBean = this.dependenciesForBeanMap.computeIfAbsent(dependentBeanName, k -> new LinkedHashSet(8));
            dependenciesForBean.add(canonicalName);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected boolean isDependent(String beanName, String dependentBeanName) {
        Map<String, Set<String>> map = this.dependentBeanMap;
        synchronized (map) {
            return this.isDependent(beanName, dependentBeanName, null);
        }
    }

    private boolean isDependent(String beanName, String dependentBeanName, @Nullable Set<String> alreadySeen) {
        if (alreadySeen != null && alreadySeen.contains(beanName)) {
            return false;
        }
        String canonicalName = this.canonicalName(beanName);
        Set<String> dependentBeans = this.dependentBeanMap.get(canonicalName);
        if (dependentBeans == null || dependentBeans.isEmpty()) {
            return false;
        }
        if (dependentBeans.contains(dependentBeanName)) {
            return true;
        }
        if (alreadySeen == null) {
            alreadySeen = new HashSet<String>();
        }
        alreadySeen.add(beanName);
        for (String transitiveDependency : dependentBeans) {
            if (!this.isDependent(transitiveDependency, dependentBeanName, alreadySeen)) continue;
            return true;
        }
        return false;
    }

    protected boolean hasDependentBean(String beanName) {
        return this.dependentBeanMap.containsKey(beanName);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String[] getDependentBeans(String beanName) {
        Set<String> dependentBeans = this.dependentBeanMap.get(beanName);
        if (dependentBeans == null) {
            return new String[0];
        }
        Map<String, Set<String>> map = this.dependentBeanMap;
        synchronized (map) {
            return StringUtils.toStringArray(dependentBeans);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String[] getDependenciesForBean(String beanName) {
        Set<String> dependenciesForBean = this.dependenciesForBeanMap.get(beanName);
        if (dependenciesForBean == null) {
            return new String[0];
        }
        Map<String, Set<String>> map = this.dependenciesForBeanMap;
        synchronized (map) {
            return StringUtils.toStringArray(dependenciesForBean);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void destroySingletons() {
        String[] disposableBeanNames;
        if (this.logger.isTraceEnabled()) {
            this.logger.trace((Object)("Destroying singletons in " + this));
        }
        Map<String, Object> map = this.singletonObjects;
        synchronized (map) {
            this.singletonsCurrentlyInDestruction = true;
        }
        Map<String, DisposableBean> map2 = this.disposableBeans;
        synchronized (map2) {
            disposableBeanNames = StringUtils.toStringArray(this.disposableBeans.keySet());
        }
        for (int i2 = disposableBeanNames.length - 1; i2 >= 0; --i2) {
            this.destroySingleton(disposableBeanNames[i2]);
        }
        this.containedBeanMap.clear();
        this.dependentBeanMap.clear();
        this.dependenciesForBeanMap.clear();
        this.clearSingletonCache();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void clearSingletonCache() {
        Map<String, Object> map = this.singletonObjects;
        synchronized (map) {
            this.singletonObjects.clear();
            this.singletonFactories.clear();
            this.earlySingletonObjects.clear();
            this.registeredSingletons.clear();
            this.singletonsCurrentlyInDestruction = false;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void destroySingleton(String beanName) {
        DisposableBean disposableBean;
        this.removeSingleton(beanName);
        Map<String, DisposableBean> map = this.disposableBeans;
        synchronized (map) {
            disposableBean = this.disposableBeans.remove(beanName);
        }
        this.destroyBean(beanName, disposableBean);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void destroyBean(String beanName, @Nullable DisposableBean bean2) {
        Set<String> containedBeans;
        block18: {
            Set<String> dependencies;
            Map<String, Set<String>> map = this.dependentBeanMap;
            synchronized (map) {
                dependencies = this.dependentBeanMap.remove(beanName);
            }
            if (dependencies != null) {
                if (this.logger.isTraceEnabled()) {
                    this.logger.trace((Object)("Retrieved dependent beans for bean '" + beanName + "': " + dependencies));
                }
                for (String dependentBeanName : dependencies) {
                    this.destroySingleton(dependentBeanName);
                }
            }
            if (bean2 != null) {
                try {
                    bean2.destroy();
                }
                catch (Throwable ex) {
                    if (!this.logger.isWarnEnabled()) break block18;
                    this.logger.warn((Object)("Destruction of bean with name '" + beanName + "' threw an exception"), ex);
                }
            }
        }
        Map<String, Set<String>> map = this.containedBeanMap;
        synchronized (map) {
            containedBeans = this.containedBeanMap.remove(beanName);
        }
        if (containedBeans != null) {
            for (String containedBeanName : containedBeans) {
                this.destroySingleton(containedBeanName);
            }
        }
        map = this.dependentBeanMap;
        synchronized (map) {
            Iterator<Map.Entry<String, Set<String>>> it = this.dependentBeanMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, Set<String>> entry = it.next();
                Set<String> dependenciesToClean = entry.getValue();
                dependenciesToClean.remove(beanName);
                if (!dependenciesToClean.isEmpty()) continue;
                it.remove();
            }
        }
        this.dependenciesForBeanMap.remove(beanName);
    }

    @Override
    public final Object getSingletonMutex() {
        return this.singletonObjects;
    }
}

