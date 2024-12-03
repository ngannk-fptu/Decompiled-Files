/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.lang.Nullable
 *  io.micrometer.observation.Observation
 *  io.micrometer.observation.Observation$Context
 *  io.micrometer.observation.ObservationConvention
 *  io.micrometer.observation.ObservationRegistry
 *  io.micrometer.observation.transport.ResponseContext
 */
package io.micrometer.core.instrument.observation;

import io.micrometer.common.lang.Nullable;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Timer;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationConvention;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.transport.ResponseContext;
import java.util.function.Supplier;

public class ObservationOrTimerCompatibleInstrumentation<T extends Observation.Context> {
    private final MeterRegistry meterRegistry;
    private final ObservationRegistry observationRegistry;
    @Nullable
    private final ObservationConvention<T> convention;
    private final ObservationConvention<T> defaultConvention;
    @Nullable
    private Timer.Sample timerSample;
    @Nullable
    private Observation observation;
    @Nullable
    private T context;
    @Nullable
    private Throwable throwable;

    public static <T extends Observation.Context> ObservationOrTimerCompatibleInstrumentation<T> start(MeterRegistry meterRegistry, @Nullable ObservationRegistry observationRegistry, Supplier<T> context, @Nullable ObservationConvention<T> convention, ObservationConvention<T> defaultConvention) {
        ObservationOrTimerCompatibleInstrumentation<T> observationOrTimer = new ObservationOrTimerCompatibleInstrumentation<T>(meterRegistry, observationRegistry, convention, defaultConvention);
        super.start(context);
        return observationOrTimer;
    }

    private ObservationOrTimerCompatibleInstrumentation(MeterRegistry meterRegistry, @Nullable ObservationRegistry observationRegistry, @Nullable ObservationConvention<T> convention, ObservationConvention<T> defaultConvention) {
        this.meterRegistry = meterRegistry;
        this.observationRegistry = observationRegistry == null ? ObservationRegistry.NOOP : observationRegistry;
        this.convention = convention;
        this.defaultConvention = defaultConvention;
    }

    private void start(Supplier<T> contextSupplier) {
        if (this.observationRegistry.isNoop()) {
            this.timerSample = Timer.start(this.meterRegistry);
        } else {
            this.observation = Observation.start(this.convention, this.defaultConvention, contextSupplier, (ObservationRegistry)this.observationRegistry);
            this.context = this.observation.getContext();
        }
    }

    public <RES> void setResponse(RES response) {
        if (this.observationRegistry.isNoop() || !(this.context instanceof ResponseContext)) {
            return;
        }
        ResponseContext responseContext = (ResponseContext)this.context;
        responseContext.setResponse(response);
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    public void stop(String timerName, @Nullable String timerDescription, Supplier<Iterable<Tag>> tagsSupplier) {
        if (this.observationRegistry.isNoop() && this.timerSample != null) {
            this.timerSample.stop(((Timer.Builder)Timer.builder(timerName).description(timerDescription).tags((Iterable)tagsSupplier.get())).register(this.meterRegistry));
        } else if (this.observation != null) {
            if (this.throwable != null) {
                this.observation.error(this.throwable);
            }
            this.observation.stop();
        }
    }
}

