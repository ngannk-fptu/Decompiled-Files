/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  brave.Tracing
 *  brave.messaging.MessagingRequest
 *  brave.messaging.MessagingTracing
 *  brave.messaging.MessagingTracing$Builder
 *  brave.messaging.MessagingTracingCustomizer
 *  brave.propagation.Propagation
 *  brave.sampler.SamplerFunction
 *  org.springframework.beans.factory.FactoryBean
 */
package brave.spring.beans;

import brave.Tracing;
import brave.messaging.MessagingRequest;
import brave.messaging.MessagingTracing;
import brave.messaging.MessagingTracingCustomizer;
import brave.propagation.Propagation;
import brave.sampler.SamplerFunction;
import java.util.List;
import org.springframework.beans.factory.FactoryBean;

public class MessagingTracingFactoryBean
implements FactoryBean {
    Tracing tracing;
    SamplerFunction<MessagingRequest> producerSampler;
    SamplerFunction<MessagingRequest> consumerSampler;
    Propagation<String> propagation;
    List<MessagingTracingCustomizer> customizers;

    public MessagingTracing getObject() {
        MessagingTracing.Builder builder = MessagingTracing.newBuilder((Tracing)this.tracing);
        if (this.producerSampler != null) {
            builder.producerSampler(this.producerSampler);
        }
        if (this.consumerSampler != null) {
            builder.consumerSampler(this.consumerSampler);
        }
        if (this.propagation != null) {
            builder.propagation(this.propagation);
        }
        if (this.customizers != null) {
            for (MessagingTracingCustomizer customizer : this.customizers) {
                customizer.customize(builder);
            }
        }
        return builder.build();
    }

    public Class<? extends MessagingTracing> getObjectType() {
        return MessagingTracing.class;
    }

    public boolean isSingleton() {
        return true;
    }

    public void setTracing(Tracing tracing) {
        this.tracing = tracing;
    }

    public void setProducerSampler(SamplerFunction<MessagingRequest> producerSampler) {
        this.producerSampler = producerSampler;
    }

    public void setConsumerSampler(SamplerFunction<MessagingRequest> consumerSampler) {
        this.consumerSampler = consumerSampler;
    }

    public void setPropagation(Propagation<String> propagation) {
        this.propagation = propagation;
    }

    public void setCustomizers(List<MessagingTracingCustomizer> customizers) {
        this.customizers = customizers;
    }
}

