/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.activemq.ActiveMQConnectionFactory
 *  org.springframework.beans.factory.config.AbstractFactoryBean
 *  zipkin2.codec.Encoding
 *  zipkin2.reporter.activemq.ActiveMQSender
 *  zipkin2.reporter.activemq.ActiveMQSender$Builder
 */
package zipkin2.reporter.beans;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import zipkin2.codec.Encoding;
import zipkin2.reporter.activemq.ActiveMQSender;

public class ActiveMQSenderFactoryBean
extends AbstractFactoryBean {
    String url;
    String queue;
    String username;
    String password;
    String clientIdPrefix = "zipkin";
    String connectionIdPrefix = "zipkin";
    Encoding encoding;
    Integer messageMaxBytes;

    protected ActiveMQSender createInstance() {
        ActiveMQSender.Builder builder = ActiveMQSender.newBuilder();
        if (this.url == null) {
            throw new IllegalArgumentException("url is required");
        }
        if (this.queue != null) {
            builder.queue(this.queue);
        }
        ActiveMQConnectionFactory connectionFactory = this.username != null ? new ActiveMQConnectionFactory(this.username, this.password, this.url) : new ActiveMQConnectionFactory(this.url);
        connectionFactory.setClientIDPrefix(this.clientIdPrefix);
        connectionFactory.setConnectionIDPrefix(this.connectionIdPrefix);
        builder.connectionFactory(connectionFactory);
        if (this.encoding != null) {
            builder.encoding(this.encoding);
        }
        if (this.messageMaxBytes != null) {
            builder.messageMaxBytes(this.messageMaxBytes.intValue());
        }
        return builder.build();
    }

    public Class<? extends ActiveMQSender> getObjectType() {
        return ActiveMQSender.class;
    }

    public boolean isSingleton() {
        return true;
    }

    protected void destroyInstance(Object instance) {
        ((ActiveMQSender)instance).close();
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setQueue(String queue) {
        this.queue = queue;
    }

    public void setClientIdPrefix(String clientIdPrefix) {
        this.clientIdPrefix = clientIdPrefix;
    }

    public String getConnectionIdPrefix() {
        return this.connectionIdPrefix;
    }

    public void setConnectionIdPrefix(String connectionIdPrefix) {
        this.connectionIdPrefix = connectionIdPrefix;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEncoding(Encoding encoding) {
        this.encoding = encoding;
    }

    public void setMessageMaxBytes(Integer messageMaxBytes) {
        this.messageMaxBytes = messageMaxBytes;
    }
}

