/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.sourcemap;

import com.atlassian.sourcemap.Mapping;
import com.atlassian.sourcemap.ReadableSourceMap;
import com.atlassian.sourcemap.ReadableSourceMapImpl;
import java.util.List;

public interface WritableSourceMap {
    public void addSourcesAndContents(List<String> var1, List<String> var2);

    public void addMapping(int var1, int var2, int var3, int var4, String var5);

    public void addMapping(int var1, int var2, int var3, int var4, String var5, String var6);

    public void addMapping(Mapping var1);

    public String generate();

    public String generateForHumans();

    public static ReadableSourceMap toReadableSourceMap(WritableSourceMap writableSourceMap) {
        if (writableSourceMap == null) {
            return null;
        }
        return ReadableSourceMapImpl.fromSource(writableSourceMap.generate());
    }
}

