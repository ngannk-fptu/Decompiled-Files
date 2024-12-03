/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  zipkin2.codec.BytesEncoder
 *  zipkin2.codec.SpanBytesEncoder
 *  zipkin2.reporter.AsyncReporter
 *  zipkin2.reporter.AsyncReporter$Builder
 *  zipkin2.reporter.Sender
 */
package zipkin2.reporter.beans;

import java.util.concurrent.TimeUnit;
import zipkin2.codec.BytesEncoder;
import zipkin2.codec.SpanBytesEncoder;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.Sender;
import zipkin2.reporter.beans.BaseAsyncFactoryBean;

public class AsyncReporterFactoryBean
extends BaseAsyncFactoryBean {
    SpanBytesEncoder encoder;

    public Class<? extends AsyncReporter> getObjectType() {
        return AsyncReporter.class;
    }

    protected AsyncReporter createInstance() {
        AsyncReporter.Builder builder = AsyncReporter.builder((Sender)this.sender);
        if (this.metrics != null) {
            builder.metrics(this.metrics);
        }
        if (this.messageMaxBytes != null) {
            builder.messageMaxBytes(this.messageMaxBytes.intValue());
        }
        if (this.messageTimeout != null) {
            builder.messageTimeout((long)this.messageTimeout.intValue(), TimeUnit.MILLISECONDS);
        }
        if (this.closeTimeout != null) {
            builder.closeTimeout((long)this.closeTimeout.intValue(), TimeUnit.MILLISECONDS);
        }
        if (this.queuedMaxSpans != null) {
            builder.queuedMaxSpans(this.queuedMaxSpans.intValue());
        }
        if (this.queuedMaxBytes != null) {
            builder.queuedMaxBytes(this.queuedMaxBytes.intValue());
        }
        return this.encoder != null ? builder.build((BytesEncoder)this.encoder) : builder.build();
    }

    protected void destroyInstance(Object instance) {
        ((AsyncReporter)instance).close();
    }

    public void setEncoder(SpanBytesEncoder encoder) {
        this.encoder = encoder;
    }
}

