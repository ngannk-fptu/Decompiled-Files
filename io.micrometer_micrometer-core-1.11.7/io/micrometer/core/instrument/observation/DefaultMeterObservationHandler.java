/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.KeyValue
 *  io.micrometer.observation.Observation$Context
 *  io.micrometer.observation.Observation$Event
 */
package io.micrometer.core.instrument.observation;

import io.micrometer.common.KeyValue;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.LongTaskTimer;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.observation.MeterObservationHandler;
import io.micrometer.observation.Observation;
import java.util.ArrayList;
import java.util.List;

public class DefaultMeterObservationHandler
implements MeterObservationHandler<Observation.Context> {
    private final MeterRegistry meterRegistry;

    public DefaultMeterObservationHandler(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public void onStart(Observation.Context context) {
        LongTaskTimer.Sample longTaskSample = LongTaskTimer.builder(context.getName() + ".active").tags(this.createTags(context)).register(this.meterRegistry).start();
        context.put(LongTaskTimer.Sample.class, (Object)longTaskSample);
        Timer.Sample sample = Timer.start(this.meterRegistry);
        context.put(Timer.Sample.class, (Object)sample);
    }

    public void onStop(Observation.Context context) {
        List<Tag> tags = this.createTags(context);
        tags.add(Tag.of("error", this.getErrorValue(context)));
        Timer.Sample sample = (Timer.Sample)context.getRequired(Timer.Sample.class);
        sample.stop(((Timer.Builder)Timer.builder(context.getName()).tags(tags)).register(this.meterRegistry));
        LongTaskTimer.Sample longTaskSample = (LongTaskTimer.Sample)context.getRequired(LongTaskTimer.Sample.class);
        longTaskSample.stop();
    }

    public void onEvent(Observation.Event event, Observation.Context context) {
        Counter.builder(context.getName() + "." + event.getName()).tags(this.createTags(context)).register(this.meterRegistry).increment();
    }

    private String getErrorValue(Observation.Context context) {
        Throwable error = context.getError();
        return error != null ? error.getClass().getSimpleName() : "none";
    }

    private List<Tag> createTags(Observation.Context context) {
        ArrayList<Tag> tags = new ArrayList<Tag>();
        for (KeyValue keyValue : context.getLowCardinalityKeyValues()) {
            tags.add(Tag.of(keyValue.getKey(), keyValue.getValue()));
        }
        return tags;
    }
}

