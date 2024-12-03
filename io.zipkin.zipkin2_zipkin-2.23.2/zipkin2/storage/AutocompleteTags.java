/*
 * Decompiled with CFR 0.152.
 */
package zipkin2.storage;

import java.util.List;
import zipkin2.Call;

public interface AutocompleteTags {
    public Call<List<String>> getKeys();

    public Call<List<String>> getValues(String var1);
}

