/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.util.profiling.micrometer.util.QualifiedCompatibleHierarchicalNameMapper
 *  com.atlassian.util.profiling.micrometer.util.UnescapedObjectNameFactory
 *  com.codahale.metrics.MetricRegistry
 *  com.codahale.metrics.jmx.JmxReporter
 *  com.codahale.metrics.jmx.ObjectNameFactory
 *  io.micrometer.core.instrument.Clock
 *  io.micrometer.core.instrument.LongTaskTimer
 *  io.micrometer.core.instrument.MeterRegistry
 *  io.micrometer.core.instrument.Statistic
 *  io.micrometer.core.instrument.Tags
 *  io.micrometer.core.instrument.composite.CompositeMeterRegistry
 *  io.micrometer.core.instrument.config.MeterFilter
 *  io.micrometer.core.instrument.config.NamingConvention
 *  io.micrometer.core.instrument.util.HierarchicalNameMapper
 *  io.micrometer.core.lang.NonNull
 *  io.micrometer.influx.InfluxConfig
 *  io.micrometer.influx.InfluxMeterRegistry
 *  io.micrometer.jmx.JmxConfig
 *  io.micrometer.jmx.JmxMeterRegistry
 *  org.apache.commons.lang3.StringUtils
 *  org.springframework.beans.factory.config.AbstractFactoryBean
 */
package com.atlassian.confluence.impl.metrics;

import com.atlassian.confluence.impl.metrics.ConfluenceJmxConfig;
import com.atlassian.util.profiling.micrometer.util.QualifiedCompatibleHierarchicalNameMapper;
import com.atlassian.util.profiling.micrometer.util.UnescapedObjectNameFactory;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.jmx.JmxReporter;
import com.codahale.metrics.jmx.ObjectNameFactory;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.LongTaskTimer;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Statistic;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import io.micrometer.core.instrument.config.MeterFilter;
import io.micrometer.core.instrument.config.NamingConvention;
import io.micrometer.core.instrument.util.HierarchicalNameMapper;
import io.micrometer.core.lang.NonNull;
import io.micrometer.influx.InfluxConfig;
import io.micrometer.influx.InfluxMeterRegistry;
import io.micrometer.jmx.JmxConfig;
import io.micrometer.jmx.JmxMeterRegistry;
import java.util.Arrays;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.AbstractFactoryBean;

class MicrometerFactoryBean
extends AbstractFactoryBean<MeterRegistry> {
    public static final MeterFilter IGNORE_ANALYTIC_TAGS = MeterFilter.ignoreTags((String[])new String[]{"atl-analytics"});
    private final ConfluenceJmxConfig confluenceJmxConfig;

    MicrometerFactoryBean(ConfluenceJmxConfig confluenceJmxConfig) {
        this.confluenceJmxConfig = confluenceJmxConfig;
    }

    public Class<?> getObjectType() {
        return MeterRegistry.class;
    }

    @NonNull
    protected MeterRegistry createInstance() {
        CompositeMeterRegistry registries = new CompositeMeterRegistry();
        if (this.confluenceJmxConfig.isJmxEnabled()) {
            registries.add((MeterRegistry)MicrometerFactoryBean.createJmxRegistry(this.confluenceJmxConfig));
        }
        if (ConfluenceInfluxConfig.INSTANCE.enabled()) {
            registries.add((MeterRegistry)this.createInfluxRegistry());
        }
        return registries;
    }

    private InfluxMeterRegistry createInfluxRegistry() {
        InfluxMeterRegistry influxMeterRegistry = new InfluxMeterRegistry((InfluxConfig)ConfluenceInfluxConfig.INSTANCE, Clock.SYSTEM);
        influxMeterRegistry.config().meterFilter(IGNORE_ANALYTIC_TAGS);
        return influxMeterRegistry;
    }

    static JmxMeterRegistry createJmxRegistry(ConfluenceJmxConfig confluenceJmxConfig) {
        MetricRegistry metricRegistry = new MetricRegistry();
        JmxReporter jmxReporter = JmxReporter.forRegistry((MetricRegistry)metricRegistry).inDomain("com.atlassian.confluence").createsObjectNamesWith((ObjectNameFactory)new UnescapedObjectNameFactory()).build();
        QualifiedCompatibleHierarchicalNameMapper nameMapper = new QualifiedCompatibleHierarchicalNameMapper();
        JmxMeterRegistry micrometerRegistry = new JmxMeterRegistry((JmxConfig)confluenceJmxConfig, Clock.SYSTEM, (HierarchicalNameMapper)nameMapper, metricRegistry, jmxReporter);
        micrometerRegistry.config().namingConvention(NamingConvention.dot).meterFilter(IGNORE_ANALYTIC_TAGS).onMeterRemoved(m -> {
            if (m instanceof LongTaskTimer) {
                for (Statistic statistic : Statistic.values()) {
                    metricRegistry.remove(nameMapper.toHierarchicalName(m.getId().withTag(statistic), micrometerRegistry.config().namingConvention()));
                }
            }
        });
        return micrometerRegistry;
    }

    static class ConfluenceInfluxConfig
    implements InfluxConfig {
        static final ConfluenceInfluxConfig INSTANCE = new ConfluenceInfluxConfig();

        ConfluenceInfluxConfig() {
        }

        public String prefix() {
            return "confluence.micrometer.influx";
        }

        public String get(String key) {
            return System.getProperty(key);
        }

        public boolean enabled() {
            return Boolean.parseBoolean(this.get(this.prefix() + ".enabled"));
        }

        Tags commonTags() {
            String str = StringUtils.trimToEmpty((String)this.get(this.prefix() + ".commonTags"));
            String[] items = (String[])Arrays.stream(StringUtils.split((String)str, (String)",")).flatMap(pair -> Arrays.stream(StringUtils.split((String)pair, (String)"="))).toArray(String[]::new);
            return Tags.of((String[])items);
        }
    }
}

