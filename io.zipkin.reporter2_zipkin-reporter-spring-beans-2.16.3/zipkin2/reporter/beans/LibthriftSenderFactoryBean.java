/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.config.AbstractFactoryBean
 *  zipkin2.reporter.libthrift.LibthriftSender
 *  zipkin2.reporter.libthrift.LibthriftSender$Builder
 */
package zipkin2.reporter.beans;

import org.springframework.beans.factory.config.AbstractFactoryBean;
import zipkin2.reporter.libthrift.LibthriftSender;

public class LibthriftSenderFactoryBean
extends AbstractFactoryBean {
    String host;
    Integer connectTimeout;
    Integer socketTimeout;
    Integer port;
    Integer messageMaxBytes;

    protected LibthriftSender createInstance() {
        LibthriftSender.Builder builder = LibthriftSender.newBuilder();
        if (this.host != null) {
            builder.host(this.host);
        }
        if (this.port != null) {
            builder.port(this.port.intValue());
        }
        if (this.socketTimeout != null) {
            builder.socketTimeout(this.socketTimeout.intValue());
        }
        if (this.connectTimeout != null) {
            builder.connectTimeout(this.connectTimeout.intValue());
        }
        if (this.messageMaxBytes != null) {
            builder.messageMaxBytes(this.messageMaxBytes.intValue());
        }
        return builder.build();
    }

    public Class<? extends LibthriftSender> getObjectType() {
        return LibthriftSender.class;
    }

    public boolean isSingleton() {
        return true;
    }

    protected void destroyInstance(Object instance) {
        ((LibthriftSender)instance).close();
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public void setSocketTimeout(Integer socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    public void setConnectTimeout(Integer connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public void setMessageMaxBytes(Integer messageMaxBytes) {
        this.messageMaxBytes = messageMaxBytes;
    }
}

