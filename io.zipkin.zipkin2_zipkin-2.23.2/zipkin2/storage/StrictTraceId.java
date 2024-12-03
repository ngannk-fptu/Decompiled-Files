/*
 * Decompiled with CFR 0.152.
 */
package zipkin2.storage;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import zipkin2.Call;
import zipkin2.Span;
import zipkin2.internal.FilterTraces;
import zipkin2.storage.QueryRequest;

public final class StrictTraceId {
    public static Call.Mapper<List<Span>, List<Span>> filterSpans(String traceId) {
        return new FilterSpans(traceId);
    }

    public static Call.Mapper<List<List<Span>>, List<List<Span>>> filterTraces(QueryRequest request) {
        return new FilterTracesIfClashOnLowerTraceId(request);
    }

    static boolean hasClashOnLowerTraceId(List<List<Span>> input) {
        int traceCount = input.size();
        if (traceCount <= 1) {
            return false;
        }
        LinkedHashSet<String> traceIdLows = new LinkedHashSet<String>();
        boolean clash = false;
        for (int i = 0; i < traceCount; ++i) {
            String traceId = StrictTraceId.lowerTraceId(input.get(i).get(0).traceId());
            if (traceIdLows.add(traceId)) continue;
            clash = true;
            break;
        }
        return clash;
    }

    static String lowerTraceId(String traceId) {
        return traceId.length() == 16 ? traceId : traceId.substring(16);
    }

    public static Call.Mapper<List<List<Span>>, List<List<Span>>> filterTraces(Iterable<String> traceIds) {
        return new FilterTracesByIds(traceIds);
    }

    StrictTraceId() {
    }

    static final class FilterTracesByIds
    implements Call.Mapper<List<List<Span>>, List<List<Span>>> {
        final Set<String> traceIds = new LinkedHashSet<String>();

        FilterTracesByIds(Iterable<String> sanitizedIds) {
            for (String traceId : sanitizedIds) {
                this.traceIds.add(traceId);
            }
        }

        @Override
        public List<List<Span>> map(List<List<Span>> input) {
            Iterator<List<Span>> i = input.iterator();
            while (i.hasNext()) {
                List<Span> next = i.next();
                if (this.traceIds.contains(next.get(0).traceId())) continue;
                i.remove();
            }
            return input;
        }

        public String toString() {
            return "FilterTracesByIds{traceIds=" + this.traceIds + "}";
        }
    }

    static final class FilterSpans
    implements Call.Mapper<List<Span>, List<Span>> {
        final String traceId;

        FilterSpans(String traceId) {
            this.traceId = traceId;
        }

        @Override
        public List<Span> map(List<Span> input) {
            Iterator<Span> i = input.iterator();
            while (i.hasNext()) {
                Span next = i.next();
                if (next.traceId().equals(this.traceId)) continue;
                i.remove();
            }
            return input;
        }

        public String toString() {
            return "FilterSpans{traceId=" + this.traceId + "}";
        }
    }

    static final class FilterTracesIfClashOnLowerTraceId
    implements Call.Mapper<List<List<Span>>, List<List<Span>>> {
        final QueryRequest request;

        FilterTracesIfClashOnLowerTraceId(QueryRequest request) {
            this.request = request;
        }

        @Override
        public List<List<Span>> map(List<List<Span>> input) {
            if (StrictTraceId.hasClashOnLowerTraceId(input)) {
                return FilterTraces.create(this.request).map(input);
            }
            return input;
        }

        public String toString() {
            return "FilterTracesIfClashOnLowerTraceId{request=" + this.request + "}";
        }
    }
}

