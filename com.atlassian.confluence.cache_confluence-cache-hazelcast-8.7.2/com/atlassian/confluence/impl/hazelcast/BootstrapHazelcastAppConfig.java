/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.ApplicationConfig
 *  com.atlassian.confluence.cluster.ClusterManager
 *  com.atlassian.confluence.core.SynchronizationManager
 *  com.atlassian.hazelcast.serialization.OsgiClassLoaderRegistry
 *  com.atlassian.hazelcast.serialization.OsgiSafeStreamSerializer
 *  com.atlassian.plugin.spring.AvailableToPlugins
 *  io.micrometer.core.instrument.MeterRegistry
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.confluence.impl.hazelcast;

import com.atlassian.config.ApplicationConfig;
import com.atlassian.confluence.cluster.ClusterManager;
import com.atlassian.confluence.cluster.hazelcast.ConfluenceOutOfMemoryHandler;
import com.atlassian.confluence.cluster.hazelcast.HazelcastClusterManager;
import com.atlassian.confluence.core.SynchronizationManager;
import com.atlassian.hazelcast.serialization.OsgiClassLoaderRegistry;
import com.atlassian.hazelcast.serialization.OsgiSafeStreamSerializer;
import com.atlassian.plugin.spring.AvailableToPlugins;
import io.micrometer.core.instrument.MeterRegistry;
import javax.management.MBeanServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BootstrapHazelcastAppConfig {
    @Bean
    @AvailableToPlugins(interfaces={ClusterManager.class})
    protected ClusterManager clusterManager(ApplicationConfig applicationConfig, ClassLoader uberClassLoader, SynchronizationManager synchronizationManager, MeterRegistry micrometerMeterRegistry, MBeanServer mBeanServer) {
        return new HazelcastClusterManager(applicationConfig, uberClassLoader, "confluence-hazelcast-config.xml", synchronizationManager, this.osgiSafeStreamSerializer(), new ConfluenceOutOfMemoryHandler(), micrometerMeterRegistry, mBeanServer);
    }

    @Bean
    protected OsgiSafeStreamSerializer osgiSafeStreamSerializer() {
        return new OsgiSafeStreamSerializer(this.osgiClassLoaderRegistry());
    }

    @Bean
    protected OsgiClassLoaderRegistry osgiClassLoaderRegistry() {
        return new OsgiClassLoaderRegistry();
    }
}

