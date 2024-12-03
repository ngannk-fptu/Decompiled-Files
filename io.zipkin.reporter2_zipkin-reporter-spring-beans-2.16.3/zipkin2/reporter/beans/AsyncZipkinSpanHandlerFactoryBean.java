/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  brave.Tag
 *  zipkin2.reporter.Sender
 *  zipkin2.reporter.brave.AsyncZipkinSpanHandler
 *  zipkin2.reporter.brave.AsyncZipkinSpanHandler$Builder
 */
package zipkin2.reporter.beans;

import brave.Tag;
import java.util.concurrent.TimeUnit;
import zipkin2.reporter.Sender;
import zipkin2.reporter.beans.BaseAsyncFactoryBean;
import zipkin2.reporter.brave.AsyncZipkinSpanHandler;

public class AsyncZipkinSpanHandlerFactoryBean
extends BaseAsyncFactoryBean {
    Tag<Throwable> errorTag;
    Boolean alwaysReportSpans;

    public Class<? extends AsyncZipkinSpanHandler> getObjectType() {
        return AsyncZipkinSpanHandler.class;
    }

    protected AsyncZipkinSpanHandler createInstance() {
        AsyncZipkinSpanHandler.Builder builder = AsyncZipkinSpanHandler.newBuilder((Sender)this.sender);
        if (this.errorTag != null) {
            builder.errorTag(this.errorTag);
        }
        if (this.alwaysReportSpans != null) {
            builder.alwaysReportSpans(this.alwaysReportSpans.booleanValue());
        }
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
        return builder.build();
    }

    protected void destroyInstance(Object instance) {
        ((AsyncZipkinSpanHandler)instance).close();
    }

    public void setErrorTag(Tag<Throwable> errorTag) {
        this.errorTag = errorTag;
    }

    public void setAlwaysReportSpans(Boolean alwaysReportSpans) {
        this.alwaysReportSpans = alwaysReportSpans;
    }
}

