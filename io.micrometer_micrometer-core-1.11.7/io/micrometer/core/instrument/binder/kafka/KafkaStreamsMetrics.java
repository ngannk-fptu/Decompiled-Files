/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.lang.NonNullApi
 *  io.micrometer.common.lang.NonNullFields
 *  org.apache.kafka.streams.KafkaStreams
 */
package io.micrometer.core.instrument.binder.kafka;

import io.micrometer.common.lang.NonNullApi;
import io.micrometer.common.lang.NonNullFields;
import io.micrometer.core.annotation.Incubating;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.binder.kafka.KafkaMetrics;
import org.apache.kafka.streams.KafkaStreams;

@NonNullApi
@NonNullFields
@Incubating(since="1.4.0")
public class KafkaStreamsMetrics
extends KafkaMetrics {
    public KafkaStreamsMetrics(KafkaStreams kafkaStreams, Iterable<Tag> tags) {
        super(() -> ((KafkaStreams)kafkaStreams).metrics(), tags);
    }

    public KafkaStreamsMetrics(KafkaStreams kafkaStreams) {
        super(() -> ((KafkaStreams)kafkaStreams).metrics());
    }
}

