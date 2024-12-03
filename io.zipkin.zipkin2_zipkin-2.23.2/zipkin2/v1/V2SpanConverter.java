/*
 * Decompiled with CFR 0.152.
 */
package zipkin2.v1;

import java.util.Map;
import zipkin2.Annotation;
import zipkin2.Endpoint;
import zipkin2.Span;
import zipkin2.v1.V1Span;

public final class V2SpanConverter {
    final V1Span.Builder result = V1Span.newBuilder();
    final V1SpanMetadata md = new V1SpanMetadata();

    public static V2SpanConverter create() {
        return new V2SpanConverter();
    }

    public V1Span convert(Span value) {
        boolean hasRemoteEndpoint;
        this.md.parse(value);
        this.result.clear().traceId(value.traceId()).parentId(value.parentId()).id(value.id()).name(value.name()).debug(value.debug());
        if (!Boolean.TRUE.equals(value.shared())) {
            this.result.timestamp(value.timestampAsLong());
            this.result.duration(value.durationAsLong());
        }
        boolean beginAnnotation = this.md.startTs != 0L && this.md.begin != null;
        boolean endAnnotation = this.md.endTs != 0L && this.md.end != null;
        Endpoint ep = value.localEndpoint();
        int annotationCount = value.annotations().size();
        if (beginAnnotation) {
            ++annotationCount;
            this.result.addAnnotation(this.md.startTs, this.md.begin, ep);
        }
        int length = value.annotations().size();
        for (int i = 0; i < length; ++i) {
            Annotation a = value.annotations().get(i);
            if (beginAnnotation && a.value().equals(this.md.begin) || endAnnotation && a.value().equals(this.md.end)) continue;
            this.result.addAnnotation(a.timestamp(), a.value(), ep);
        }
        if (endAnnotation) {
            ++annotationCount;
            this.result.addAnnotation(this.md.endTs, this.md.end, ep);
        }
        for (Map.Entry<String, String> b : value.tags().entrySet()) {
            this.result.addBinaryAnnotation(b.getKey(), b.getValue(), ep);
        }
        boolean writeLocalComponent = annotationCount == 0 && ep != null && value.tags().isEmpty();
        boolean bl = hasRemoteEndpoint = this.md.addr != null && value.remoteEndpoint() != null;
        if (writeLocalComponent) {
            this.result.addBinaryAnnotation("lc", "", ep);
        }
        if (hasRemoteEndpoint) {
            this.result.addBinaryAnnotation(this.md.addr, value.remoteEndpoint());
        }
        return this.result.build();
    }

    V2SpanConverter() {
    }

    static final class V1SpanMetadata {
        long startTs;
        long endTs;
        long msTs;
        long wsTs;
        long wrTs;
        long mrTs;
        String begin;
        String end;
        String addr;

        V1SpanMetadata() {
        }

        void parse(Span in) {
            this.mrTs = 0L;
            this.wrTs = 0L;
            this.wsTs = 0L;
            this.msTs = 0L;
            this.endTs = 0L;
            this.startTs = 0L;
            this.addr = null;
            this.end = null;
            this.begin = null;
            this.startTs = in.timestampAsLong();
            this.endTs = this.startTs != 0L && in.durationAsLong() != 0L ? this.startTs + in.durationAsLong() : 0L;
            Span.Kind kind = in.kind();
            int length = in.annotations().size();
            for (int i = 0; i < length; ++i) {
                Annotation a = in.annotations().get(i);
                String value = a.value();
                if (value.length() != 2) continue;
                if (value.equals("cs")) {
                    kind = Span.Kind.CLIENT;
                    if (a.timestamp() >= this.startTs) continue;
                    this.startTs = a.timestamp();
                    continue;
                }
                if (value.equals("sr")) {
                    kind = Span.Kind.SERVER;
                    if (a.timestamp() >= this.startTs) continue;
                    this.startTs = a.timestamp();
                    continue;
                }
                if (value.equals("ss")) {
                    kind = Span.Kind.SERVER;
                    if (a.timestamp() <= this.endTs) continue;
                    this.endTs = a.timestamp();
                    continue;
                }
                if (value.equals("cr")) {
                    kind = Span.Kind.CLIENT;
                    if (a.timestamp() <= this.endTs) continue;
                    this.endTs = a.timestamp();
                    continue;
                }
                if (value.equals("ms")) {
                    kind = Span.Kind.PRODUCER;
                    this.msTs = a.timestamp();
                    continue;
                }
                if (value.equals("mr")) {
                    kind = Span.Kind.CONSUMER;
                    this.mrTs = a.timestamp();
                    continue;
                }
                if (value.equals("ws")) {
                    this.wsTs = a.timestamp();
                    continue;
                }
                if (!value.equals("wr")) continue;
                this.wrTs = a.timestamp();
            }
            if (in.remoteEndpoint() != null) {
                this.addr = "sa";
            }
            if (kind == null) {
                return;
            }
            switch (kind) {
                case CLIENT: {
                    this.addr = "sa";
                    this.begin = "cs";
                    this.end = "cr";
                    break;
                }
                case SERVER: {
                    this.addr = "ca";
                    this.begin = "sr";
                    this.end = "ss";
                    break;
                }
                case PRODUCER: {
                    this.addr = "ma";
                    this.begin = "ms";
                    this.end = "ws";
                    if (this.startTs == 0L || this.msTs != 0L && this.msTs < this.startTs) {
                        this.startTs = this.msTs;
                    }
                    if (this.endTs != 0L && (this.wsTs == 0L || this.wsTs <= this.endTs)) break;
                    this.endTs = this.wsTs;
                    break;
                }
                case CONSUMER: {
                    this.addr = "ma";
                    if (this.startTs == 0L || this.wrTs != 0L && this.wrTs < this.startTs) {
                        this.startTs = this.wrTs;
                    }
                    if (this.endTs == 0L || this.mrTs != 0L && this.mrTs > this.endTs) {
                        this.endTs = this.mrTs;
                    }
                    if (this.endTs != 0L || this.wrTs != 0L) {
                        this.begin = "wr";
                        this.end = "mr";
                        break;
                    }
                    this.begin = "mr";
                    break;
                }
                default: {
                    throw new AssertionError((Object)"update kind mapping");
                }
            }
            if (in.remoteEndpoint() == null) {
                this.addr = null;
            }
        }
    }
}

