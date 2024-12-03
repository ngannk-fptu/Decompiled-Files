/*
 * Decompiled with CFR 0.152.
 */
package zipkin2.storage;

import java.util.List;
import zipkin2.Call;
import zipkin2.Span;

public interface Traces {
    public Call<List<Span>> getTrace(String var1);

    public Call<List<List<Span>>> getTraces(Iterable<String> var1);
}

