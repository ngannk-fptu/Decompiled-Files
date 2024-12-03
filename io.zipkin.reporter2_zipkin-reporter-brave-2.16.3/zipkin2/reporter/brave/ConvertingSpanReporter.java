/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  brave.Span$Kind
 *  brave.Tag
 *  brave.handler.MutableSpan
 *  brave.handler.MutableSpan$AnnotationConsumer
 *  brave.handler.MutableSpan$TagConsumer
 *  zipkin2.Endpoint
 *  zipkin2.Span
 *  zipkin2.Span$Builder
 *  zipkin2.Span$Kind
 *  zipkin2.reporter.Reporter
 */
package zipkin2.reporter.brave;

import brave.Span;
import brave.Tag;
import brave.handler.MutableSpan;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;
import zipkin2.Endpoint;
import zipkin2.Span;
import zipkin2.reporter.Reporter;

final class ConvertingSpanReporter
implements Reporter<MutableSpan> {
    static final Logger logger = Logger.getLogger(ConvertingSpanReporter.class.getName());
    static final Map<Span.Kind, Span.Kind> BRAVE_TO_ZIPKIN_KIND = ConvertingSpanReporter.generateKindMap();
    final Reporter<Span> delegate;
    final Tag<Throwable> errorTag;

    ConvertingSpanReporter(Reporter<Span> delegate, Tag<Throwable> errorTag) {
        this.delegate = delegate;
        this.errorTag = errorTag;
    }

    public void report(MutableSpan span) {
        this.maybeAddErrorTag(span);
        Span converted = ConvertingSpanReporter.convert(span);
        this.delegate.report((Object)converted);
    }

    static Span convert(MutableSpan span) {
        Span.Kind kind;
        Span.Builder result = Span.newBuilder().traceId(span.traceId()).parentId(span.parentId()).id(span.id()).name(span.name());
        long start = span.startTimestamp();
        long finish = span.finishTimestamp();
        result.timestamp(start);
        if (start != 0L && finish != 0L) {
            result.duration(Math.max(finish - start, 1L));
        }
        if ((kind = span.kind()) != null) {
            result.kind(BRAVE_TO_ZIPKIN_KIND.get(kind));
        }
        String localServiceName = span.localServiceName();
        String localIp = span.localIp();
        if (localServiceName != null || localIp != null) {
            result.localEndpoint(Endpoint.newBuilder().serviceName(localServiceName).ip(localIp).port(span.localPort()).build());
        }
        String remoteServiceName = span.remoteServiceName();
        String remoteIp = span.remoteIp();
        if (remoteServiceName != null || remoteIp != null) {
            result.remoteEndpoint(Endpoint.newBuilder().serviceName(remoteServiceName).ip(remoteIp).port(span.remotePort()).build());
        }
        span.forEachTag((MutableSpan.TagConsumer)Consumer.INSTANCE, (Object)result);
        span.forEachAnnotation((MutableSpan.AnnotationConsumer)Consumer.INSTANCE, (Object)result);
        if (span.shared()) {
            result.shared(true);
        }
        if (span.debug()) {
            result.debug(true);
        }
        return result.build();
    }

    void maybeAddErrorTag(MutableSpan span) {
        if (span.error() == null) {
            return;
        }
        if (span.tag("error") == null) {
            this.errorTag.tag((Object)span.error(), null, span);
        }
    }

    public String toString() {
        return this.delegate.toString();
    }

    public final boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ConvertingSpanReporter)) {
            return false;
        }
        return this.delegate.equals(((ConvertingSpanReporter)o).delegate);
    }

    public final int hashCode() {
        return this.delegate.hashCode();
    }

    static Map<Span.Kind, Span.Kind> generateKindMap() {
        LinkedHashMap<Span.Kind, Span.Kind> result = new LinkedHashMap<Span.Kind, Span.Kind>();
        for (Span.Kind kind : Span.Kind.values()) {
            try {
                result.put(kind, Span.Kind.valueOf((String)kind.name()));
            }
            catch (RuntimeException e) {
                logger.warning("Could not map Brave kind " + kind + " to Zipkin");
            }
        }
        return result;
    }

    static enum Consumer implements MutableSpan.TagConsumer<Span.Builder>,
    MutableSpan.AnnotationConsumer<Span.Builder>
    {
        INSTANCE;


        public void accept(Span.Builder target, String key, String value) {
            target.putTag(key, value);
        }

        public void accept(Span.Builder target, long timestamp, String value) {
            target.addAnnotation(timestamp, value);
        }
    }
}

