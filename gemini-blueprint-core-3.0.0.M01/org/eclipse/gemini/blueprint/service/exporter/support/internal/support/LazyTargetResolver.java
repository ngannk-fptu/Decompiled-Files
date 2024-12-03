/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.BeanFactory
 */
package org.eclipse.gemini.blueprint.service.exporter.support.internal.support;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import org.eclipse.gemini.blueprint.service.exporter.support.internal.support.ListenerNotifier;
import org.eclipse.gemini.blueprint.service.exporter.support.internal.support.ServiceRegistrationDecorator;
import org.eclipse.gemini.blueprint.service.exporter.support.internal.support.UnregistrationNotifier;
import org.eclipse.gemini.blueprint.util.OsgiServiceReferenceUtils;
import org.springframework.beans.factory.BeanFactory;

public class LazyTargetResolver
implements UnregistrationNotifier {
    private final BeanFactory beanFactory;
    private final String beanName;
    private final boolean cacheService;
    private volatile Object target;
    private final Object lock = new Object();
    private final AtomicBoolean activated;
    private final ListenerNotifier notifier;
    private volatile ServiceRegistrationDecorator decorator;

    public LazyTargetResolver(Object target, BeanFactory beanFactory, String beanName, boolean cacheService, ListenerNotifier notifier, boolean lazyListeners) {
        this.target = target;
        this.beanFactory = beanFactory;
        this.beanName = beanName;
        this.cacheService = cacheService;
        this.notifier = notifier;
        this.activated = new AtomicBoolean(!lazyListeners);
    }

    public void activate() {
        if (this.activated.compareAndSet(false, true) && this.notifier != null) {
            if (this.decorator == null) {
                this.notifier.callUnregister(null, null);
            } else {
                Object target = this.getBeanIfAlreadyInstantiatedOrSingletonScoped().orElse(null);
                Map properties = (Map)((Object)OsgiServiceReferenceUtils.getServicePropertiesSnapshot(this.decorator.getReference()));
                this.notifier.callRegister(target, properties);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Object getBean() {
        if (this.target != null) {
            return this.target;
        }
        if (this.beanFactory.isSingleton(this.beanName) || !this.cacheService) {
            return this.beanFactory.getBean(this.beanName);
        }
        Object targetCandidate = this.beanFactory.getBean(this.beanName);
        if (this.target == null) {
            Object object = this.lock;
            synchronized (object) {
                if (this.target == null) {
                    this.target = targetCandidate;
                }
            }
        }
        return this.target;
    }

    public Class<?> getType() {
        if (this.target != null) {
            return this.target.getClass();
        }
        if (this.beanFactory.isSingleton(this.beanName)) {
            return this.beanFactory.getBean(this.beanName).getClass();
        }
        return this.beanFactory.getType(this.beanName);
    }

    @Override
    public void unregister(Map properties) {
        if (this.activated.get() && this.notifier != null) {
            Object target = this.getBeanIfAlreadyInstantiatedOrSingletonScoped().orElse(null);
            this.notifier.callUnregister(target, properties);
        }
    }

    public void setDecorator(ServiceRegistrationDecorator decorator) {
        this.decorator = decorator;
        if (decorator != null) {
            decorator.setNotifier(this);
        }
    }

    public void notifyIfPossible() {
        if (this.activated.get() && this.notifier != null) {
            Object target = this.getBeanIfAlreadyInstantiatedOrSingletonScoped().orElse(null);
            Map properties = (Map)((Object)OsgiServiceReferenceUtils.getServicePropertiesSnapshot(this.decorator.getReference()));
            this.notifier.callRegister(target, properties);
        }
    }

    public void startupUnregisterIfPossible() {
        if (this.activated.get() && this.notifier != null) {
            this.notifier.callUnregister(null, null);
        }
    }

    private Optional<Object> getBeanIfAlreadyInstantiatedOrSingletonScoped() {
        if (this.target != null) {
            return Optional.of(this.target);
        }
        if (this.cacheService || this.beanFactory.isSingleton(this.beanName)) {
            return Optional.of(this.getBean());
        }
        return Optional.empty();
    }
}

