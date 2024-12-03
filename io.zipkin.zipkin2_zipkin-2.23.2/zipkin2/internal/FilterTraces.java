/*
 * Decompiled with CFR 0.152.
 */
package zipkin2.internal;

import java.util.ArrayList;
import java.util.List;
import zipkin2.Call;
import zipkin2.Span;
import zipkin2.storage.QueryRequest;

public final class FilterTraces
implements Call.Mapper<List<List<Span>>, List<List<Span>>> {
    final QueryRequest request;

    public static Call.Mapper<List<List<Span>>, List<List<Span>>> create(QueryRequest request) {
        return new FilterTraces(request);
    }

    FilterTraces(QueryRequest request) {
        this.request = request;
    }

    @Override
    public List<List<Span>> map(List<List<Span>> input) {
        int length = input.size();
        if (length == 0) {
            return input;
        }
        ArrayList<List<Span>> result = new ArrayList<List<Span>>(length);
        for (int i = 0; i < length; ++i) {
            List<Span> next = input.get(i);
            if (!this.request.test(next)) continue;
            result.add(next);
        }
        return result;
    }

    public String toString() {
        return "FilterTraces{request=" + this.request + "}";
    }
}

