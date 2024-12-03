/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.config.AbstractFactoryBean
 *  zipkin2.codec.Encoding
 *  zipkin2.reporter.kafka.KafkaSender
 *  zipkin2.reporter.kafka.KafkaSender$Builder
 */
package zipkin2.reporter.beans;

import org.springframework.beans.factory.config.AbstractFactoryBean;
import zipkin2.codec.Encoding;
import zipkin2.reporter.kafka.KafkaSender;

public class KafkaSenderFactoryBean
extends AbstractFactoryBean {
    String bootstrapServers;
    String topic;
    Encoding encoding;
    Integer messageMaxBytes;

    protected KafkaSender createInstance() {
        KafkaSender.Builder builder = KafkaSender.newBuilder();
        if (this.bootstrapServers != null) {
            builder.bootstrapServers(this.bootstrapServers);
        }
        if (this.encoding != null) {
            builder.encoding(this.encoding);
        }
        if (this.topic != null) {
            builder.topic(this.topic);
        }
        if (this.messageMaxBytes != null) {
            builder.messageMaxBytes(this.messageMaxBytes.intValue());
        }
        return builder.build();
    }

    public Class<? extends KafkaSender> getObjectType() {
        return KafkaSender.class;
    }

    public boolean isSingleton() {
        return true;
    }

    protected void destroyInstance(Object instance) {
        ((KafkaSender)instance).close();
    }

    public void setBootstrapServers(String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setEncoding(Encoding encoding) {
        this.encoding = encoding;
    }

    public void setMessageMaxBytes(Integer messageMaxBytes) {
        this.messageMaxBytes = messageMaxBytes;
    }
}

