/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.config.AbstractFactoryBean
 *  zipkin2.codec.Encoding
 *  zipkin2.reporter.amqp.RabbitMQSender
 *  zipkin2.reporter.amqp.RabbitMQSender$Builder
 */
package zipkin2.reporter.beans;

import java.io.IOException;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import zipkin2.codec.Encoding;
import zipkin2.reporter.amqp.RabbitMQSender;

public class RabbitMQSenderFactoryBean
extends AbstractFactoryBean {
    String addresses;
    String queue;
    Encoding encoding;
    Integer connectionTimeout;
    String virtualHost;
    String username;
    String password;
    Integer messageMaxBytes;

    protected RabbitMQSender createInstance() {
        RabbitMQSender.Builder builder = RabbitMQSender.newBuilder();
        if (this.addresses != null) {
            builder.addresses(this.addresses);
        }
        if (this.encoding != null) {
            builder.encoding(this.encoding);
        }
        if (this.queue != null) {
            builder.queue(this.queue);
        }
        if (this.connectionTimeout != null) {
            builder.connectionTimeout(this.connectionTimeout.intValue());
        }
        if (this.virtualHost != null) {
            builder.virtualHost(this.virtualHost);
        }
        if (this.username != null) {
            builder.username(this.username);
        }
        if (this.password != null) {
            builder.password(this.password);
        }
        if (this.messageMaxBytes != null) {
            builder.messageMaxBytes(this.messageMaxBytes.intValue());
        }
        return builder.build();
    }

    public Class<? extends RabbitMQSender> getObjectType() {
        return RabbitMQSender.class;
    }

    public boolean isSingleton() {
        return true;
    }

    protected void destroyInstance(Object instance) throws IOException {
        ((RabbitMQSender)instance).close();
    }

    public void setAddresses(String addresses) {
        this.addresses = addresses;
    }

    public void setQueue(String queue) {
        this.queue = queue;
    }

    public void setEncoding(Encoding encoding) {
        this.encoding = encoding;
    }

    public void setConnectionTimeout(Integer connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public void setVirtualHost(String virtualHost) {
        this.virtualHost = virtualHost;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setMessageMaxBytes(Integer messageMaxBytes) {
        this.messageMaxBytes = messageMaxBytes;
    }
}

