/*
 * Decompiled with CFR 0.152.
 */
package zipkin2.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import zipkin2.Call;
import zipkin2.Span;
import zipkin2.internal.AggregateCall;
import zipkin2.storage.SpanStore;
import zipkin2.storage.Traces;

public final class TracesAdapter
implements Traces {
    final SpanStore delegate;

    public TracesAdapter(SpanStore spanStore) {
        this.delegate = spanStore;
    }

    @Override
    public Call<List<Span>> getTrace(String traceId) {
        return this.delegate.getTrace(traceId);
    }

    @Override
    public Call<List<List<Span>>> getTraces(Iterable<String> traceIds) {
        if (traceIds == null) {
            throw new NullPointerException("traceIds == null");
        }
        ArrayList<Call<List<Span>>> calls = new ArrayList<Call<List<Span>>>();
        for (String traceId : traceIds) {
            calls.add(this.getTrace(Span.normalizeTraceId(traceId)));
        }
        if (calls.isEmpty()) {
            return Call.emptyList();
        }
        if (calls.size() == 1) {
            return ((Call)calls.get(0)).map(ToListOfTraces.INSTANCE);
        }
        return new ScatterGather((List<Call<List<Span>>>)calls);
    }

    public String toString() {
        return "TracesAdapter{" + this.delegate + "}";
    }

    static final class ScatterGather
    extends AggregateCall<List<Span>, List<List<Span>>> {
        ScatterGather(List<Call<List<Span>>> calls) {
            super(calls);
        }

        @Override
        protected List<List<Span>> newOutput() {
            return new ArrayList<List<Span>>();
        }

        @Override
        protected void append(List<Span> input, List<List<Span>> output) {
            if (!input.isEmpty()) {
                output.add(input);
            }
        }

        @Override
        protected boolean isEmpty(List<List<Span>> output) {
            return output.isEmpty();
        }

        @Override
        public ScatterGather clone() {
            return new ScatterGather(this.cloneCalls());
        }
    }

    static enum ToListOfTraces implements Call.Mapper<List<Span>, List<List<Span>>>
    {
        INSTANCE;


        @Override
        public List<List<Span>> map(List<Span> input) {
            return input.isEmpty() ? Collections.emptyList() : Collections.singletonList(input);
        }

        public String toString() {
            return "ToListOfTraces()";
        }
    }
}

