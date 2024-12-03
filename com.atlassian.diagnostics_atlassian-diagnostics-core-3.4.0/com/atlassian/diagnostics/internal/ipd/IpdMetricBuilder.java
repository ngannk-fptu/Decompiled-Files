/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.diagnostics.ipd.internal.spi.IpdMetric
 *  com.atlassian.diagnostics.ipd.internal.spi.MetricFactory
 *  com.atlassian.diagnostics.ipd.internal.spi.MetricOptions
 *  com.atlassian.util.profiling.MetricKey
 *  com.atlassian.util.profiling.MetricTag$RequiredMetricTag
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.diagnostics.internal.ipd;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.diagnostics.internal.ipd.IpdMetricTypeVerifier;
import com.atlassian.diagnostics.ipd.internal.spi.IpdMetric;
import com.atlassian.diagnostics.ipd.internal.spi.MetricFactory;
import com.atlassian.diagnostics.ipd.internal.spi.MetricOptions;
import com.atlassian.util.profiling.MetricKey;
import com.atlassian.util.profiling.MetricTag;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import org.apache.commons.lang3.StringUtils;

public class IpdMetricBuilder<T extends IpdMetric> {
    private final Set<MetricTag.RequiredMetricTag> tags = new HashSet<MetricTag.RequiredMetricTag>();
    private MetricFactory<T> metricFactory;
    private String metricName;
    private final IpdMetricTypeVerifier metricTypeVerifier;
    private boolean workInProgressMetric = false;
    private boolean logOnUpdate = false;

    public IpdMetricBuilder(String metricName, Collection<MetricTag.RequiredMetricTag> tags, MetricFactory<T> metricFactory, IpdMetricTypeVerifier metricTypeVerifier) {
        this.metricName = metricName;
        this.metricTypeVerifier = metricTypeVerifier;
        this.tags.addAll(tags);
        this.metricFactory = metricFactory;
    }

    @VisibleForTesting
    IpdMetricBuilder(String metricName, Collection<MetricTag.RequiredMetricTag> tags, MetricFactory<T> metricFactory) {
        this(metricName, tags, metricFactory, ipdMetric -> {});
    }

    public T buildMetric(String productPrefix, Supplier<Boolean> enabledCheck, Consumer<IpdMetric> loggingConsumer) {
        return (T)this.metricFactory.createMetric(new MetricOptions(this.getMetricKey(), productPrefix, enabledCheck, loggingConsumer, !this.logOnUpdate));
    }

    public IpdMetricBuilder<T> withPrefix(String prefix) {
        if (StringUtils.isEmpty((CharSequence)this.metricName)) {
            this.metricName = prefix;
        } else if (!StringUtils.isEmpty((CharSequence)prefix)) {
            this.metricName = prefix + "." + this.metricName;
        }
        return this;
    }

    public IpdMetricBuilder<T> withTags(MetricTag.RequiredMetricTag ... tags) {
        this.tags.addAll(Arrays.asList(tags));
        return this;
    }

    public IpdMetricBuilder<T> asWorkInProgress() {
        this.workInProgressMetric = true;
        return this;
    }

    public IpdMetricBuilder<T> logOnUpdate() {
        this.logOnUpdate = true;
        return this;
    }

    public IpdMetricBuilder<T> wrapMetricFactory(UnaryOperator<MetricFactory<T>> metricFactoryWrapper) {
        this.metricFactory = (MetricFactory)metricFactoryWrapper.apply(this.metricFactory);
        return this;
    }

    public MetricKey getMetricKey() {
        return MetricKey.metricKey((String)this.metricName, this.tags);
    }

    public String getMetricName() {
        return this.metricName;
    }

    public Set<MetricTag.RequiredMetricTag> getTags() {
        return this.tags;
    }

    public MetricFactory<T> getMetricFactory() {
        return this.metricFactory;
    }

    public boolean isWorkInProgressMetric() {
        return this.workInProgressMetric;
    }

    public boolean isLogOnUpdate() {
        return this.logOnUpdate;
    }

    public void verifyExpectedMetricType(IpdMetric ipdMetric) throws ClassCastException {
        this.metricTypeVerifier.verifyIpdMetricType(ipdMetric);
    }
}

