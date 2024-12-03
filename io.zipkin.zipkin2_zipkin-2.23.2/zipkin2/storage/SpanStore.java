/*
 * Decompiled with CFR 0.152.
 */
package zipkin2.storage;

import java.util.List;
import zipkin2.Call;
import zipkin2.DependencyLink;
import zipkin2.Span;
import zipkin2.storage.QueryRequest;

public interface SpanStore {
    public Call<List<List<Span>>> getTraces(QueryRequest var1);

    @Deprecated
    public Call<List<Span>> getTrace(String var1);

    @Deprecated
    public Call<List<String>> getServiceNames();

    @Deprecated
    public Call<List<String>> getSpanNames(String var1);

    public Call<List<DependencyLink>> getDependencies(long var1, long var3);
}

