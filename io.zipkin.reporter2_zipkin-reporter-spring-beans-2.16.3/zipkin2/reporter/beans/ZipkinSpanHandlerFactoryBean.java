/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  brave.Tag
 *  brave.handler.SpanHandler
 *  org.springframework.beans.factory.config.AbstractFactoryBean
 *  zipkin2.Span
 *  zipkin2.reporter.Reporter
 *  zipkin2.reporter.brave.ZipkinSpanHandler
 *  zipkin2.reporter.brave.ZipkinSpanHandler$Builder
 */
package zipkin2.reporter.beans;

import brave.Tag;
import brave.handler.SpanHandler;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import zipkin2.Span;
import zipkin2.reporter.Reporter;
import zipkin2.reporter.brave.ZipkinSpanHandler;

public class ZipkinSpanHandlerFactoryBean
extends AbstractFactoryBean {
    Reporter<Span> spanReporter;
    Tag<Throwable> errorTag;
    Boolean alwaysReportSpans;

    protected SpanHandler createInstance() {
        ZipkinSpanHandler.Builder builder = ZipkinSpanHandler.newBuilder(this.spanReporter);
        if (this.errorTag != null) {
            builder.errorTag(this.errorTag);
        }
        if (this.alwaysReportSpans != null) {
            builder.alwaysReportSpans(this.alwaysReportSpans.booleanValue());
        }
        return builder.build();
    }

    public Class<? extends SpanHandler> getObjectType() {
        return SpanHandler.class;
    }

    public boolean isSingleton() {
        return true;
    }

    public void setSpanReporter(Reporter<Span> spanReporter) {
        this.spanReporter = spanReporter;
    }

    public void setErrorTag(Tag<Throwable> errorTag) {
        this.errorTag = errorTag;
    }

    public void setAlwaysReportSpans(Boolean alwaysReportSpans) {
        this.alwaysReportSpans = alwaysReportSpans;
    }
}

