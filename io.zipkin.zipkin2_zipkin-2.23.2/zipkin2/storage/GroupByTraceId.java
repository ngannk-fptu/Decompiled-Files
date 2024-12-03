/*
 * Decompiled with CFR 0.152.
 */
package zipkin2.storage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import zipkin2.Call;
import zipkin2.Span;
import zipkin2.storage.StrictTraceId;

public final class GroupByTraceId
implements Call.Mapper<List<Span>, List<List<Span>>> {
    final boolean strictTraceId;

    public static Call.Mapper<List<Span>, List<List<Span>>> create(boolean strictTraceId) {
        return new GroupByTraceId(strictTraceId);
    }

    GroupByTraceId(boolean strictTraceId) {
        this.strictTraceId = strictTraceId;
    }

    @Override
    public List<List<Span>> map(List<Span> input) {
        if (input.isEmpty()) {
            return Collections.emptyList();
        }
        LinkedHashMap groupedByTraceId = new LinkedHashMap();
        for (Span span : input) {
            String traceId = span.traceId();
            if (!this.strictTraceId) {
                traceId = StrictTraceId.lowerTraceId(traceId);
            }
            if (!groupedByTraceId.containsKey(traceId)) {
                groupedByTraceId.put(traceId, new ArrayList());
            }
            ((List)groupedByTraceId.get(traceId)).add(span);
        }
        return new ArrayList<List<Span>>(groupedByTraceId.values());
    }

    public String toString() {
        return "GroupByTraceId{strictTraceId=" + this.strictTraceId + "}";
    }
}

