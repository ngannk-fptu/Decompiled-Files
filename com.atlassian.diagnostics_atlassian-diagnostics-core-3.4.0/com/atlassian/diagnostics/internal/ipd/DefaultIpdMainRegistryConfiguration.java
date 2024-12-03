/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.ipd.internal.spi.IpdMetric
 */
package com.atlassian.diagnostics.internal.ipd;

import com.atlassian.diagnostics.internal.ipd.IpdMainRegistryConfiguration;
import com.atlassian.diagnostics.ipd.internal.spi.IpdMetric;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class DefaultIpdMainRegistryConfiguration
implements IpdMainRegistryConfiguration {
    private final AtomicReference<Supplier<Boolean>> ipdFfSupplier = new AtomicReference<Supplier<Boolean>>(() -> true);
    private final AtomicReference<Supplier<Boolean>> ipdWipFfSupplier = new AtomicReference<Supplier<Boolean>>(() -> false);
    private final AtomicReference<Consumer<IpdMetric>> logMetricOnDemand = new AtomicReference<Consumer<IpdMetric>>(metric -> {});
    private final String productPrefix;

    public DefaultIpdMainRegistryConfiguration(String productPrefix) {
        this.productPrefix = productPrefix;
    }

    public void setFFSupplier(Supplier<Boolean> fFSupplier) {
        this.ipdFfSupplier.set(fFSupplier);
    }

    public void setWipFFSupplier(Supplier<Boolean> wipFFSupplier) {
        this.ipdWipFfSupplier.set(wipFFSupplier);
    }

    public void setMetricLogOnDemandListener(Consumer<IpdMetric> logOnDemandListener) {
        this.logMetricOnDemand.set(logOnDemandListener);
    }

    @Override
    public String getProductPrefix() {
        return this.productPrefix;
    }

    @Override
    public boolean isIpdEnabled() {
        return this.ipdFfSupplier.get().get();
    }

    @Override
    public boolean isIpdWipEnabled() {
        return this.ipdWipFfSupplier.get().get();
    }

    @Override
    public void metricUpdated(IpdMetric metric) {
        this.logMetricOnDemand.get().accept(metric);
    }
}

