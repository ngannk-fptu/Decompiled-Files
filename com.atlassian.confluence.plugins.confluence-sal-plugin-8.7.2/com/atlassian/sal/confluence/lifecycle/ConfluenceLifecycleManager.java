/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.bootstrap.AtlassianBootstrapManager
 *  com.atlassian.confluence.setup.SetupCompleteEvent
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventListenerRegistrar
 *  com.atlassian.plugin.event.PluginEventListener
 *  com.atlassian.plugin.event.events.PluginFrameworkStartedEvent
 *  com.atlassian.sal.api.lifecycle.LifecycleAware
 *  com.atlassian.sal.api.lifecycle.LifecycleManager
 *  com.atlassian.tenancy.api.event.TenantArrivedEvent
 *  com.atlassian.util.concurrent.Promise
 *  com.atlassian.util.concurrent.Promises
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Stopwatch
 *  io.atlassian.util.concurrent.ThreadFactories
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  org.osgi.framework.ServiceReference
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.sal.confluence.lifecycle;

import com.atlassian.config.bootstrap.AtlassianBootstrapManager;
import com.atlassian.confluence.setup.SetupCompleteEvent;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventListenerRegistrar;
import com.atlassian.plugin.event.PluginEventListener;
import com.atlassian.plugin.event.events.PluginFrameworkStartedEvent;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import com.atlassian.sal.api.lifecycle.LifecycleManager;
import com.atlassian.sal.confluence.lifecycle.ServiceExecutionStrategy;
import com.atlassian.tenancy.api.event.TenantArrivedEvent;
import com.atlassian.util.concurrent.Promise;
import com.atlassian.util.concurrent.Promises;
import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import io.atlassian.util.concurrent.ThreadFactories;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ConfluenceLifecycleManager
implements LifecycleManager {
    private static final Logger log = LoggerFactory.getLogger(ConfluenceLifecycleManager.class);
    private final AtomicReference<State> currentState = new AtomicReference<State>(State.BEFORE_INIT);
    private final Collection<ServiceReference<?>> lifecycleReferences = new CopyOnWriteArraySet();
    private final AtomicInteger tenantPositionMonitor = new AtomicInteger();
    private final AtlassianBootstrapManager bootstrapManager;
    private final EventListenerRegistrar eventListenerRegistrar;
    private final ExecutorService executorService = Executors.newCachedThreadPool(ThreadFactories.namedThreadFactory((String)this.getClass().getName()));

    public ConfluenceLifecycleManager(AtlassianBootstrapManager bootstrapManager, EventListenerRegistrar eventListenerRegistrar) {
        this.bootstrapManager = bootstrapManager;
        this.eventListenerRegistrar = eventListenerRegistrar;
    }

    @PostConstruct
    void init() {
        this.eventListenerRegistrar.register((Object)this);
    }

    @PreDestroy
    void dispose() {
        this.eventListenerRegistrar.unregister((Object)this);
        this.executorService.shutdownNow();
    }

    @PluginEventListener
    public void onPluginFrameworkStarted(PluginFrameworkStartedEvent ignored) {
        log.info("received PluginFrameworkStartedEvent, starting tenants... ");
        this.start();
    }

    @EventListener
    public void onSetupComplete(SetupCompleteEvent event) {
        log.info("received SetupCompleteEvent for tenant {}, current state {}", event.getSource(), (Object)this.currentState.get());
        this.start();
    }

    @EventListener
    public void onTenantArrived(TenantArrivedEvent event) {
        log.info("received TenantArrivedEvent received for tenant {}, current state {}", (Object)event.getTenant(), (Object)this.currentState.get());
        if (State.BEFORE_INIT != this.currentState.get()) {
            this.trigger().claim();
        }
    }

    public void onLifecycleExport(ServiceReference<?> lifecycleReference) {
        this.lifecycleReferences.add(lifecycleReference);
        if (State.BEFORE_INIT != this.currentState.get()) {
            this.trigger().claim();
        }
    }

    public boolean isApplicationSetUp() {
        return this.bootstrapManager.isSetupComplete();
    }

    public void start() {
        boolean applicationSetUp = this.isApplicationSetUp();
        if (!applicationSetUp) {
            log.info("start called, application not yet setup, defer tenant startup until later");
            return;
        }
        if (this.currentState.compareAndSet(State.BEFORE_INIT, State.INIT)) {
            log.info("start called, application setup, starting tenants (INIT)");
            this.trigger().claim();
            log.info("all tenants started (AFTER_INIT)");
            Preconditions.checkState((boolean)this.currentState.compareAndSet(State.INIT, State.AFTER_INIT));
        } else {
            log.info("start called, but existing state was {}, unable to start tenants", (Object)this.currentState.get());
        }
    }

    private Promise<?> trigger() {
        return Promises.when(List.of(Promises.forFuture(this.executorService.submit(new LifecycleExecution()))));
    }

    public void setLifecycleReferences(List<ServiceReference<?>> lifecycleReferences) {
        this.lifecycleReferences.addAll(lifecycleReferences);
    }

    protected abstract ServiceExecutionStrategy<LifecycleAware> createServiceExecutionStrategy();

    private class LifecycleExecution
    implements Runnable {
        private LifecycleExecution() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void run() {
            AtomicInteger atomicInteger = ConfluenceLifecycleManager.this.tenantPositionMonitor;
            synchronized (atomicInteger) {
                int tenantPosition = ConfluenceLifecycleManager.this.tenantPositionMonitor.get();
                if (tenantPosition < ConfluenceLifecycleManager.this.lifecycleReferences.size()) {
                    ServiceExecutionStrategy<LifecycleAware> serviceExecutionStrategy = ConfluenceLifecycleManager.this.createServiceExecutionStrategy();
                    tenantPosition = this.addLifecycles(tenantPosition, serviceExecutionStrategy);
                    serviceExecutionStrategy.trigger();
                }
                ConfluenceLifecycleManager.this.tenantPositionMonitor.set(tenantPosition);
            }
        }

        private Integer addLifecycles(Integer tenantPosition, ServiceExecutionStrategy<LifecycleAware> serviceExecutionStrategy) {
            ServiceReference[] lifeServiceReferences = ConfluenceLifecycleManager.this.lifecycleReferences.toArray(new ServiceReference[0]);
            while (tenantPosition < lifeServiceReferences.length) {
                ServiceReference lifecycleReference = lifeServiceReferences[tenantPosition];
                if (lifecycleReference.getBundle() != null) {
                    Preconditions.checkState((boolean)serviceExecutionStrategy.add(lifecycleReference, lifecycleAware -> {
                        this.triggerLifecycle((LifecycleAware)lifecycleAware);
                        return null;
                    }), (String)"Lifecycle reference %s could not be added. There should always be a catch all strategy configured.", (Object)lifecycleReference);
                }
                Integer n = tenantPosition;
                Integer n2 = tenantPosition = Integer.valueOf(tenantPosition + 1);
            }
            return tenantPosition;
        }

        private void triggerLifecycle(LifecycleAware lifecycleAware) {
            try {
                Stopwatch stopwatch = Stopwatch.createStarted();
                lifecycleAware.onStart();
                log.info("{}#onStart took {}ms", (Object)lifecycleAware.getClass().getName(), (Object)stopwatch.elapsed(TimeUnit.MILLISECONDS));
            }
            catch (RuntimeException e) {
                if (e.getCause() != null) {
                    log.error("Unable to start component: " + lifecycleAware.getClass().getName(), e.getCause());
                }
                log.error("Unable to start component: " + lifecycleAware.getClass().getName(), (Throwable)e);
            }
        }
    }

    private static enum State {
        BEFORE_INIT,
        INIT,
        AFTER_INIT;

    }
}

