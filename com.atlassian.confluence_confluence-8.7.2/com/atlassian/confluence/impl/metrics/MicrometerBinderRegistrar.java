/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventListenerRegistrar
 *  com.atlassian.tenancy.api.event.TenantArrivedEvent
 *  io.micrometer.core.instrument.MeterRegistry
 *  io.micrometer.core.instrument.binder.MeterBinder
 *  io.micrometer.core.instrument.binder.jvm.JvmGcMetrics
 *  io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics
 *  io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics
 *  io.micrometer.core.instrument.binder.system.ProcessorMetrics
 *  io.micrometer.core.instrument.binder.system.UptimeMetrics
 *  io.micrometer.core.instrument.binder.tomcat.TomcatMetrics
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  org.hibernate.engine.spi.SessionFactoryImplementor
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.factory.ListableBeanFactory
 *  org.springframework.context.ApplicationContext
 *  org.springframework.context.ApplicationContextAware
 */
package com.atlassian.confluence.impl.metrics;

import com.atlassian.confluence.impl.hibernate.metrics.ConfluenceHibernateMetrics;
import com.atlassian.confluence.impl.metrics.ConfluenceMicrometer;
import com.atlassian.confluence.impl.metrics.IndexTaskQueueMetricsBinder;
import com.atlassian.confluence.search.IndexTaskQueue;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventListenerRegistrar;
import com.atlassian.tenancy.api.event.TenantArrivedEvent;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import io.micrometer.core.instrument.binder.system.UptimeMetrics;
import io.micrometer.core.instrument.binder.tomcat.TomcatMetrics;
import java.util.Arrays;
import java.util.Collections;
import java.util.NoSuchElementException;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.management.MBeanServer;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public abstract class MicrometerBinderRegistrar
implements ApplicationContextAware {
    private static final Logger log = LoggerFactory.getLogger(MicrometerBinderRegistrar.class);
    private final MeterRegistry meterRegistry;
    private final MBeanServer mBeanServer;
    private final EventListenerRegistrar eventListenerRegistrar;
    private ListableBeanFactory beanFactory;

    public MicrometerBinderRegistrar(MeterRegistry meterRegistry, MBeanServer mBeanServer, EventListenerRegistrar eventListenerRegistrar) {
        this.meterRegistry = meterRegistry;
        this.mBeanServer = mBeanServer;
        this.eventListenerRegistrar = eventListenerRegistrar;
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.beanFactory = applicationContext;
    }

    @PostConstruct
    public void initSystemMetrics() {
        this.bind(new MeterBinder[]{new ProcessorMetrics(), new UptimeMetrics(), new JvmMemoryMetrics(), new JvmGcMetrics(), new JvmThreadMetrics()});
    }

    @PostConstruct
    public void initTomcatMetrics() throws ReflectiveOperationException {
        this.bind(new MeterBinder[]{new TomcatMetricsCreator().instantiateTomcatMetrics()});
    }

    @PostConstruct
    public void registerEventListeners() {
        this.eventListenerRegistrar.register((Object)this);
    }

    @PreDestroy
    public void unregisterEventListeners() {
        this.eventListenerRegistrar.unregister((Object)this);
    }

    private void initIndexTaskQueueMetrics() {
        this.beanFactory.getBeansOfType(IndexTaskQueue.class, false, false).forEach((name, queue) -> this.bind(new IndexTaskQueueMetricsBinder((IndexTaskQueue<?>)queue, (String)name)));
    }

    @EventListener
    public void onTenantArrived(TenantArrivedEvent event) {
        this.initIndexTaskQueueMetrics();
        this.bind(new ConfluenceHibernateMetrics(this.getTenantedSessionFactory()));
    }

    private void bind(MeterBinder ... binders) {
        if (ConfluenceMicrometer.isMicrometerEnabled()) {
            Arrays.stream(binders).forEach(binder -> {
                log.info("Binding metrics from {}", (Object)binder.getClass().getTypeName());
                try {
                    binder.bindTo(this.meterRegistry);
                }
                catch (RuntimeException ex) {
                    log.error("Error binding metrics from {}", (Object)binder.getClass().getTypeName(), (Object)ex);
                }
            });
        }
    }

    protected abstract SessionFactoryImplementor getTenantedSessionFactory();

    class TomcatMetricsCreator {
        TomcatMetricsCreator() {
        }

        private TomcatMetrics instantiateTomcatMetrics() throws ReflectiveOperationException {
            return this.instantiate(TomcatMetrics.class, null, Collections.emptySet(), MicrometerBinderRegistrar.this.mBeanServer);
        }

        private <T> T instantiate(Class<T> clazz, Object ... args) throws ReflectiveOperationException {
            Object instance = Arrays.stream(clazz.getConstructors()).filter(ctor -> ctor.getParameterTypes().length == args.length).findFirst().orElseThrow(() -> new NoSuchElementException("No constructor found on " + clazz + " which takes " + args.length + " arguments")).newInstance(args);
            return clazz.cast(instance);
        }
    }
}

