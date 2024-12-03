/*
 * Decompiled with CFR 0.152.
 */
package zipkin2.v1;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import zipkin2.Endpoint;
import zipkin2.Span;
import zipkin2.internal.Nullable;
import zipkin2.v1.V1Annotation;
import zipkin2.v1.V1BinaryAnnotation;
import zipkin2.v1.V1Span;

public final class V1SpanConverter {
    final Span.Builder first = Span.newBuilder();
    final List<Span.Builder> spans = new ArrayList<Span.Builder>();
    V1Annotation cs;
    V1Annotation sr;
    V1Annotation ss;
    V1Annotation cr;
    V1Annotation ms;
    V1Annotation mr;
    V1Annotation ws;
    V1Annotation wr;

    public static V1SpanConverter create() {
        return new V1SpanConverter();
    }

    public List<Span> convert(V1Span source) {
        ArrayList<Span> out = new ArrayList<Span>();
        this.convert(source, out);
        return out;
    }

    public void convert(V1Span source, Collection<Span> sink) {
        this.start(source);
        this.processAnnotations(source);
        this.processBinaryAnnotations(source);
        this.finish(sink);
    }

    void start(V1Span source) {
        this.first.clear();
        this.spans.clear();
        this.wr = null;
        this.ws = null;
        this.mr = null;
        this.ms = null;
        this.cr = null;
        this.ss = null;
        this.sr = null;
        this.cs = null;
        V1SpanConverter.newBuilder(this.first, source);
    }

    void processAnnotations(V1Span source) {
        int length = source.annotations.size();
        for (int i = 0; i < length; ++i) {
            V1Annotation a = source.annotations.get(i);
            Span.Builder currentSpan = this.forEndpoint(source, a.endpoint);
            if (a.value.length() == 2 && a.endpoint != null) {
                if (a.value.equals("cs")) {
                    currentSpan.kind(Span.Kind.CLIENT);
                    this.cs = a;
                    continue;
                }
                if (a.value.equals("sr")) {
                    currentSpan.kind(Span.Kind.SERVER);
                    this.sr = a;
                    continue;
                }
                if (a.value.equals("ss")) {
                    currentSpan.kind(Span.Kind.SERVER);
                    this.ss = a;
                    continue;
                }
                if (a.value.equals("cr")) {
                    currentSpan.kind(Span.Kind.CLIENT);
                    this.cr = a;
                    continue;
                }
                if (a.value.equals("ms")) {
                    currentSpan.kind(Span.Kind.PRODUCER);
                    this.ms = a;
                    continue;
                }
                if (a.value.equals("mr")) {
                    currentSpan.kind(Span.Kind.CONSUMER);
                    this.mr = a;
                    continue;
                }
                if (a.value.equals("ws")) {
                    this.ws = a;
                    continue;
                }
                if (a.value.equals("wr")) {
                    this.wr = a;
                    continue;
                }
                currentSpan.addAnnotation(a.timestamp, a.value);
                continue;
            }
            currentSpan.addAnnotation(a.timestamp, a.value);
        }
        if (this.cs == null && V1SpanConverter.endTimestampReflectsSpanDuration(this.cr, source)) {
            this.cs = V1Annotation.create(source.timestamp, "cs", this.cr.endpoint);
        }
        if (this.sr == null && V1SpanConverter.endTimestampReflectsSpanDuration(this.ss, source)) {
            this.sr = V1Annotation.create(source.timestamp, "sr", this.ss.endpoint);
        }
        if (this.cs != null && this.sr != null) {
            Span.Builder server;
            this.maybeTimestampDuration(source, this.cs, this.cr);
            Span.Builder client = this.forEndpoint(source, this.cs.endpoint);
            if (V1SpanConverter.hasSameServiceName(this.cs.endpoint, this.sr.endpoint)) {
                client.kind(Span.Kind.CLIENT);
                server = this.newSpanBuilder(source, this.sr.endpoint).kind(Span.Kind.SERVER);
            } else {
                server = this.forEndpoint(source, this.sr.endpoint);
            }
            server.shared(true).timestamp(this.sr.timestamp);
            if (this.ss != null) {
                server.duration(this.ss.timestamp - this.sr.timestamp);
            }
            if (this.cr == null && source.duration == 0L) {
                client.duration(null);
            }
        } else if (this.cs != null && this.cr != null) {
            this.maybeTimestampDuration(source, this.cs, this.cr);
        } else if (this.sr != null && this.ss != null) {
            this.maybeTimestampDuration(source, this.sr, this.ss);
        } else {
            this.handleIncompleteRpc(source);
        }
        if (this.cs == null && this.sr != null && (source.timestamp == 0L || this.ss != null && source.duration == 0L)) {
            this.forEndpoint(source, this.sr.endpoint).shared(true);
        }
        if (this.ms != null && this.mr != null) {
            Span.Builder consumer;
            Span.Builder producer = this.forEndpoint(source, this.ms.endpoint);
            if (V1SpanConverter.hasSameServiceName(this.ms.endpoint, this.mr.endpoint)) {
                producer.kind(Span.Kind.PRODUCER);
                consumer = this.newSpanBuilder(source, this.mr.endpoint).kind(Span.Kind.CONSUMER);
            } else {
                consumer = this.forEndpoint(source, this.mr.endpoint);
            }
            consumer.shared(true);
            if (this.wr != null) {
                consumer.timestamp(this.wr.timestamp).duration(this.mr.timestamp - this.wr.timestamp);
            } else {
                consumer.timestamp(this.mr.timestamp);
            }
            producer.timestamp(this.ms.timestamp).duration(this.ws != null ? Long.valueOf(this.ws.timestamp - this.ms.timestamp) : null);
        } else if (this.ms != null) {
            this.maybeTimestampDuration(source, this.ms, this.ws);
        } else if (this.mr != null) {
            if (this.wr != null) {
                this.maybeTimestampDuration(source, this.wr, this.mr);
            } else {
                this.maybeTimestampDuration(source, this.mr, null);
            }
        } else {
            if (this.ws != null) {
                this.forEndpoint(source, this.ws.endpoint).addAnnotation(this.ws.timestamp, this.ws.value);
            }
            if (this.wr != null) {
                this.forEndpoint(source, this.wr.endpoint).addAnnotation(this.wr.timestamp, this.wr.value);
            }
        }
    }

    void handleIncompleteRpc(V1Span source) {
        this.handleIncompleteRpc(this.first);
        int length = this.spans.size();
        for (int i = 0; i < length; ++i) {
            this.handleIncompleteRpc(this.spans.get(i));
        }
        if (source.timestamp != 0L) {
            this.first.timestamp(source.timestamp).duration(source.duration);
        }
    }

    void handleIncompleteRpc(Span.Builder next) {
        if (Span.Kind.CLIENT.equals((Object)next.kind())) {
            if (this.cs != null) {
                next.timestamp(this.cs.timestamp);
            }
            if (this.cr != null) {
                next.addAnnotation(this.cr.timestamp, this.cr.value);
            }
        } else if (Span.Kind.SERVER.equals((Object)next.kind())) {
            if (this.sr != null) {
                next.timestamp(this.sr.timestamp);
            }
            if (this.ss != null) {
                next.addAnnotation(this.ss.timestamp, this.ss.value);
            }
        }
    }

    static boolean endTimestampReflectsSpanDuration(V1Annotation end, V1Span source) {
        return end != null && source.timestamp != 0L && source.duration != 0L && source.timestamp + source.duration == end.timestamp;
    }

    void maybeTimestampDuration(V1Span source, V1Annotation begin, @Nullable V1Annotation end) {
        Span.Builder span2 = this.forEndpoint(source, begin.endpoint);
        if (source.timestamp != 0L && source.duration != 0L) {
            span2.timestamp(source.timestamp).duration(source.duration);
        } else {
            span2.timestamp(begin.timestamp);
            if (end != null) {
                span2.duration(end.timestamp - begin.timestamp);
            }
        }
    }

    void processBinaryAnnotations(V1Span source) {
        V1Annotation server;
        boolean noCoreAnnotations;
        Endpoint ca = null;
        Endpoint sa = null;
        Endpoint ma = null;
        int length = source.binaryAnnotations.size();
        for (int i = 0; i < length; ++i) {
            V1BinaryAnnotation b = source.binaryAnnotations.get(i);
            if ("ca".equals(b.key)) {
                ca = b.endpoint;
                continue;
            }
            if ("sa".equals(b.key)) {
                sa = b.endpoint;
                continue;
            }
            if ("ma".equals(b.key)) {
                ma = b.endpoint;
                continue;
            }
            Span.Builder currentSpan = this.forEndpoint(source, b.endpoint);
            if ("lc".equals(b.key) && b.stringValue.isEmpty()) continue;
            currentSpan.putTag(b.key, b.stringValue);
        }
        boolean bl = noCoreAnnotations = this.cs == null && this.cr == null && this.ss == null && this.sr == null;
        if (noCoreAnnotations && (ca != null || sa != null)) {
            if (ca != null && sa != null) {
                this.forEndpoint(source, ca).remoteEndpoint(sa);
            } else if (sa != null) {
                this.forEndpoint(source, null).remoteEndpoint(sa);
            } else {
                this.forEndpoint(source, null).kind(Span.Kind.SERVER).remoteEndpoint(ca);
            }
            return;
        }
        V1Annotation v1Annotation = server = this.sr != null ? this.sr : this.ss;
        if (ca != null && server != null && !ca.equals(server.endpoint)) {
            if (V1SpanConverter.hasSameServiceName(ca, server.endpoint)) {
                ca = ca.toBuilder().serviceName(null).build();
            }
            this.forEndpoint(source, server.endpoint).remoteEndpoint(ca);
        }
        if (sa != null) {
            if (this.cs != null) {
                this.forEndpoint(source, this.cs.endpoint).remoteEndpoint(sa);
            } else if (this.cr != null) {
                this.forEndpoint(source, this.cr.endpoint).remoteEndpoint(sa);
            }
        }
        if (ma != null) {
            if (this.ms != null) {
                this.forEndpoint(source, this.ms.endpoint).remoteEndpoint(ma);
            }
            if (this.mr != null) {
                this.forEndpoint(source, this.mr.endpoint).remoteEndpoint(ma);
            }
        }
    }

    Span.Builder forEndpoint(V1Span source, @Nullable Endpoint e) {
        if (e == null) {
            return this.first;
        }
        if (V1SpanConverter.closeEnoughEndpoint(this.first, e)) {
            return this.first;
        }
        int length = this.spans.size();
        for (int i = 0; i < length; ++i) {
            Span.Builder next = this.spans.get(i);
            if (!V1SpanConverter.closeEnoughEndpoint(next, e)) continue;
            return next;
        }
        return this.newSpanBuilder(source, e);
    }

    static boolean closeEnoughEndpoint(Span.Builder builder, Endpoint e) {
        Endpoint localEndpoint = builder.localEndpoint();
        if (localEndpoint == null) {
            builder.localEndpoint(e);
            return true;
        }
        return V1SpanConverter.hasSameServiceName(localEndpoint, e);
    }

    Span.Builder newSpanBuilder(V1Span source, Endpoint e) {
        Span.Builder result = V1SpanConverter.newBuilder(Span.newBuilder(), source).localEndpoint(e);
        this.spans.add(result);
        return result;
    }

    void finish(Collection<Span> sink) {
        sink.add(this.first.build());
        int length = this.spans.size();
        for (int i = 0; i < length; ++i) {
            sink.add(this.spans.get(i).build());
        }
    }

    static boolean hasSameServiceName(Endpoint left, @Nullable Endpoint right) {
        return V1SpanConverter.equal(left.serviceName(), right.serviceName());
    }

    static boolean equal(Object a, Object b) {
        return a == b || a != null && a.equals(b);
    }

    static Span.Builder newBuilder(Span.Builder builder, V1Span source) {
        return builder.traceId(source.traceIdHigh, source.traceId).parentId(source.parentId).id(source.id).name(source.name).debug(source.debug);
    }

    V1SpanConverter() {
    }
}

