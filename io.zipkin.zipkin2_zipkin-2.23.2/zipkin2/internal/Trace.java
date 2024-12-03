/*
 * Decompiled with CFR 0.152.
 */
package zipkin2.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import zipkin2.Endpoint;
import zipkin2.Span;

public class Trace {
    static final Comparator<Span> CLEANUP_COMPARATOR = new Comparator<Span>(){

        @Override
        public int compare(Span left, Span right) {
            if (left.equals(right)) {
                return 0;
            }
            int bySpanId = left.id().compareTo(right.id());
            if (bySpanId != 0) {
                return bySpanId;
            }
            int byShared = Trace.compareShared(left, right);
            if (byShared != 0) {
                return byShared;
            }
            return Trace.compareEndpoint(left.localEndpoint(), right.localEndpoint());
        }
    };

    public static List<Span> merge(List<Span> spans) {
        int length = spans.size();
        if (length <= 1) {
            return spans;
        }
        ArrayList<Span> result = new ArrayList<Span>(spans);
        Collections.sort(result, CLEANUP_COMPARATOR);
        String traceId = ((Span)result.get(0)).traceId();
        for (int i = 1; i < length; ++i) {
            String nextTraceId = ((Span)result.get(i)).traceId();
            if (traceId.length() == 32) continue;
            traceId = nextTraceId;
        }
        Span last = null;
        for (int i = 0; i < length; ++i) {
            Span next;
            String nextId;
            Span span = (Span)result.get(i);
            boolean spanShared = Boolean.TRUE.equals(span.shared());
            Span.Builder replacement = null;
            if (span.traceId().length() != traceId.length()) {
                replacement = span.toBuilder().traceId(traceId);
            }
            EndpointTracker localEndpoint = null;
            while (i + 1 < length && (nextId = (next = (Span)result.get(i + 1)).id()).equals(span.id())) {
                boolean nextShared;
                if (localEndpoint == null) {
                    localEndpoint = new EndpointTracker();
                    localEndpoint.tryMerge(span.localEndpoint());
                }
                if (spanShared != (nextShared = Boolean.TRUE.equals(next.shared())) || !localEndpoint.tryMerge(next.localEndpoint())) break;
                if (replacement == null) {
                    replacement = span.toBuilder();
                }
                replacement.merge(next);
                --length;
                result.remove(i + 1);
            }
            if (last != null && last.id().equals(span.id())) {
                if (last.kind() == Span.Kind.CLIENT && span.kind() == Span.Kind.SERVER && !spanShared) {
                    spanShared = true;
                    if (replacement == null) {
                        replacement = span.toBuilder();
                    }
                    replacement.shared(true);
                }
                if (spanShared && span.parentId() == null && last.parentId() != null) {
                    if (replacement == null) {
                        replacement = span.toBuilder();
                    }
                    replacement.parentId(last.parentId());
                }
            }
            if (replacement != null) {
                span = replacement.build();
                result.set(i, span);
            }
            last = span;
        }
        return result;
    }

    static int compareShared(Span left, Span right) {
        boolean leftShared = Boolean.TRUE.equals(left.shared());
        boolean rightShared = Boolean.TRUE.equals(right.shared());
        if (leftShared && rightShared) {
            return 0;
        }
        if (leftShared) {
            return 1;
        }
        if (rightShared) {
            return -1;
        }
        boolean leftClient = Span.Kind.CLIENT.equals((Object)left.kind());
        boolean rightClient = Span.Kind.CLIENT.equals((Object)right.kind());
        if (leftClient && rightClient) {
            return 0;
        }
        if (leftClient) {
            return -1;
        }
        if (rightClient) {
            return 1;
        }
        return 0;
    }

    static int compareEndpoint(Endpoint left, Endpoint right) {
        if (left == null) {
            return right == null ? 0 : -1;
        }
        if (right == null) {
            return 1;
        }
        int byService = Trace.nullSafeCompareTo(left.serviceName(), right.serviceName(), false);
        if (byService != 0) {
            return byService;
        }
        int byIpV4 = Trace.nullSafeCompareTo(left.ipv4(), right.ipv4(), false);
        if (byIpV4 != 0) {
            return byIpV4;
        }
        return Trace.nullSafeCompareTo(left.ipv6(), right.ipv6(), false);
    }

    static <T extends Comparable<T>> int nullSafeCompareTo(T left, T right, boolean nullFirst) {
        if (left == null) {
            return right == null ? 0 : (nullFirst ? -1 : 1);
        }
        if (right == null) {
            return nullFirst ? 1 : -1;
        }
        return left.compareTo(right);
    }

    Trace() {
    }

    static final class EndpointTracker {
        String serviceName;
        String ipv4;
        String ipv6;
        int port;

        EndpointTracker() {
        }

        boolean tryMerge(Endpoint endpoint) {
            if (endpoint == null) {
                return true;
            }
            if (this.serviceName != null && endpoint.serviceName() != null && !this.serviceName.equals(endpoint.serviceName())) {
                return false;
            }
            if (this.ipv4 != null && endpoint.ipv4() != null && !this.ipv4.equals(endpoint.ipv4())) {
                return false;
            }
            if (this.ipv6 != null && endpoint.ipv6() != null && !this.ipv6.equals(endpoint.ipv6())) {
                return false;
            }
            if (this.port != 0 && endpoint.portAsInt() != 0 && this.port != endpoint.portAsInt()) {
                return false;
            }
            if (this.serviceName == null) {
                this.serviceName = endpoint.serviceName();
            }
            if (this.ipv4 == null) {
                this.ipv4 = endpoint.ipv4();
            }
            if (this.ipv6 == null) {
                this.ipv6 = endpoint.ipv6();
            }
            if (this.port == 0) {
                this.port = endpoint.portAsInt();
            }
            return true;
        }
    }
}

