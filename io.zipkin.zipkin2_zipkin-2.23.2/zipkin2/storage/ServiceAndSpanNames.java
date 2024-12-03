/*
 * Decompiled with CFR 0.152.
 */
package zipkin2.storage;

import java.util.List;
import zipkin2.Call;

public interface ServiceAndSpanNames {
    public Call<List<String>> getServiceNames();

    public Call<List<String>> getRemoteServiceNames(String var1);

    public Call<List<String>> getSpanNames(String var1);
}

