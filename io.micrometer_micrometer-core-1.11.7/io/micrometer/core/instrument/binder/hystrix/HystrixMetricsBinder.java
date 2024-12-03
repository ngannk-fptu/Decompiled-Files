/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.netflix.hystrix.strategy.HystrixPlugins
 *  com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy
 *  com.netflix.hystrix.strategy.eventnotifier.HystrixEventNotifier
 *  com.netflix.hystrix.strategy.executionhook.HystrixCommandExecutionHook
 *  com.netflix.hystrix.strategy.metrics.HystrixMetricsPublisher
 *  com.netflix.hystrix.strategy.properties.HystrixPropertiesStrategy
 *  io.micrometer.common.lang.NonNullApi
 *  io.micrometer.common.lang.NonNullFields
 */
package io.micrometer.core.instrument.binder.hystrix;

import com.netflix.hystrix.strategy.HystrixPlugins;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy;
import com.netflix.hystrix.strategy.eventnotifier.HystrixEventNotifier;
import com.netflix.hystrix.strategy.executionhook.HystrixCommandExecutionHook;
import com.netflix.hystrix.strategy.metrics.HystrixMetricsPublisher;
import com.netflix.hystrix.strategy.properties.HystrixPropertiesStrategy;
import io.micrometer.common.lang.NonNullApi;
import io.micrometer.common.lang.NonNullFields;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import io.micrometer.core.instrument.binder.hystrix.MicrometerMetricsPublisher;

@NonNullApi
@NonNullFields
public class HystrixMetricsBinder
implements MeterBinder {
    @Override
    public void bindTo(MeterRegistry registry) {
        HystrixEventNotifier eventNotifier = HystrixPlugins.getInstance().getEventNotifier();
        HystrixPropertiesStrategy propertiesStrategy = HystrixPlugins.getInstance().getPropertiesStrategy();
        HystrixCommandExecutionHook commandExecutionHook = HystrixPlugins.getInstance().getCommandExecutionHook();
        HystrixConcurrencyStrategy concurrencyStrategy = HystrixPlugins.getInstance().getConcurrencyStrategy();
        HystrixMetricsPublisher metricsPublisher = HystrixPlugins.getInstance().getMetricsPublisher();
        HystrixPlugins.reset();
        HystrixPlugins.getInstance().registerMetricsPublisher((HystrixMetricsPublisher)new MicrometerMetricsPublisher(registry, metricsPublisher));
        HystrixPlugins.getInstance().registerConcurrencyStrategy(concurrencyStrategy);
        HystrixPlugins.getInstance().registerEventNotifier(eventNotifier);
        HystrixPlugins.getInstance().registerPropertiesStrategy(propertiesStrategy);
        HystrixPlugins.getInstance().registerCommandExecutionHook(commandExecutionHook);
    }
}

