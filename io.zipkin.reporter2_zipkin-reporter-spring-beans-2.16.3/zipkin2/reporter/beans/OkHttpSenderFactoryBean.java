/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.config.AbstractFactoryBean
 *  zipkin2.codec.Encoding
 *  zipkin2.reporter.okhttp3.OkHttpSender
 *  zipkin2.reporter.okhttp3.OkHttpSender$Builder
 */
package zipkin2.reporter.beans;

import org.springframework.beans.factory.config.AbstractFactoryBean;
import zipkin2.codec.Encoding;
import zipkin2.reporter.okhttp3.OkHttpSender;

public class OkHttpSenderFactoryBean
extends AbstractFactoryBean {
    String endpoint;
    Encoding encoding;
    Integer maxRequests;
    Integer connectTimeout;
    Integer readTimeout;
    Integer writeTimeout;
    Boolean compressionEnabled;
    Integer messageMaxBytes;

    protected OkHttpSender createInstance() {
        OkHttpSender.Builder builder = OkHttpSender.newBuilder();
        if (this.endpoint != null) {
            builder.endpoint(this.endpoint);
        }
        if (this.encoding != null) {
            builder.encoding(this.encoding);
        }
        if (this.connectTimeout != null) {
            builder.connectTimeout(this.connectTimeout.intValue());
        }
        if (this.readTimeout != null) {
            builder.readTimeout(this.readTimeout.intValue());
        }
        if (this.writeTimeout != null) {
            builder.writeTimeout(this.writeTimeout.intValue());
        }
        if (this.maxRequests != null) {
            builder.maxRequests(this.maxRequests.intValue());
        }
        if (this.compressionEnabled != null) {
            builder.compressionEnabled(this.compressionEnabled.booleanValue());
        }
        if (this.messageMaxBytes != null) {
            builder.messageMaxBytes(this.messageMaxBytes.intValue());
        }
        return builder.build();
    }

    public Class<? extends OkHttpSender> getObjectType() {
        return OkHttpSender.class;
    }

    public boolean isSingleton() {
        return true;
    }

    protected void destroyInstance(Object instance) {
        ((OkHttpSender)instance).close();
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public void setEncoding(Encoding encoding) {
        this.encoding = encoding;
    }

    public void setMaxRequests(Integer maxRequests) {
        this.maxRequests = maxRequests;
    }

    public void setConnectTimeout(Integer connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public void setReadTimeout(Integer readTimeout) {
        this.readTimeout = readTimeout;
    }

    public void setWriteTimeout(Integer writeTimeout) {
        this.writeTimeout = writeTimeout;
    }

    public void setCompressionEnabled(Boolean compressionEnabled) {
        this.compressionEnabled = compressionEnabled;
    }

    public void setMessageMaxBytes(Integer messageMaxBytes) {
        this.messageMaxBytes = messageMaxBytes;
    }
}

