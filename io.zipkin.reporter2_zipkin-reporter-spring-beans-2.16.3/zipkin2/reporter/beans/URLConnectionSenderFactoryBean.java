/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.config.AbstractFactoryBean
 *  zipkin2.codec.Encoding
 *  zipkin2.reporter.urlconnection.URLConnectionSender
 *  zipkin2.reporter.urlconnection.URLConnectionSender$Builder
 */
package zipkin2.reporter.beans;

import org.springframework.beans.factory.config.AbstractFactoryBean;
import zipkin2.codec.Encoding;
import zipkin2.reporter.urlconnection.URLConnectionSender;

public class URLConnectionSenderFactoryBean
extends AbstractFactoryBean {
    String endpoint;
    Encoding encoding;
    Integer connectTimeout;
    Integer readTimeout;
    Boolean compressionEnabled;
    Integer messageMaxBytes;

    protected URLConnectionSender createInstance() {
        URLConnectionSender.Builder builder = URLConnectionSender.newBuilder();
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
        if (this.compressionEnabled != null) {
            builder.compressionEnabled(this.compressionEnabled.booleanValue());
        }
        if (this.messageMaxBytes != null) {
            builder.messageMaxBytes(this.messageMaxBytes.intValue());
        }
        return builder.build();
    }

    public Class<? extends URLConnectionSender> getObjectType() {
        return URLConnectionSender.class;
    }

    public boolean isSingleton() {
        return true;
    }

    protected void destroyInstance(Object instance) {
        ((URLConnectionSender)instance).close();
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public void setEncoding(Encoding encoding) {
        this.encoding = encoding;
    }

    public void setConnectTimeout(Integer connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public void setReadTimeout(Integer readTimeout) {
        this.readTimeout = readTimeout;
    }

    public void setCompressionEnabled(Boolean compressionEnabled) {
        this.compressionEnabled = compressionEnabled;
    }

    public void setMessageMaxBytes(Integer messageMaxBytes) {
        this.messageMaxBytes = messageMaxBytes;
    }
}

