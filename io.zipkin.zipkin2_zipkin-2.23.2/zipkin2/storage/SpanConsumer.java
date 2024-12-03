/*
 * Decompiled with CFR 0.152.
 */
package zipkin2.storage;

import java.util.List;
import zipkin2.Call;
import zipkin2.Span;

public interface SpanConsumer {
    public Call<Void> accept(List<Span> var1);
}

