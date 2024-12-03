/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.lang.NonNullApi
 *  io.micrometer.common.lang.NonNullFields
 *  org.apache.kafka.clients.admin.AdminClient
 *  org.apache.kafka.clients.consumer.Consumer
 *  org.apache.kafka.clients.producer.Producer
 */
package io.micrometer.core.instrument.binder.kafka;

import io.micrometer.common.lang.NonNullApi;
import io.micrometer.common.lang.NonNullFields;
import io.micrometer.core.annotation.Incubating;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.binder.kafka.KafkaMetrics;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.producer.Producer;

@NonNullApi
@NonNullFields
@Incubating(since="1.4.0")
public class KafkaClientMetrics
extends KafkaMetrics {
    public KafkaClientMetrics(Producer<?, ?> kafkaProducer, Iterable<Tag> tags) {
        super(() -> kafkaProducer.metrics(), tags);
    }

    public KafkaClientMetrics(Producer<?, ?> kafkaProducer) {
        super(() -> kafkaProducer.metrics());
    }

    public KafkaClientMetrics(Consumer<?, ?> kafkaConsumer, Iterable<Tag> tags) {
        super(() -> kafkaConsumer.metrics(), tags);
    }

    public KafkaClientMetrics(Consumer<?, ?> kafkaConsumer) {
        super(() -> kafkaConsumer.metrics());
    }

    public KafkaClientMetrics(AdminClient adminClient, Iterable<Tag> tags) {
        super(() -> ((AdminClient)adminClient).metrics(), tags);
    }

    public KafkaClientMetrics(AdminClient adminClient) {
        super(() -> ((AdminClient)adminClient).metrics());
    }
}

