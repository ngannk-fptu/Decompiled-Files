/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.sourcemap;

import com.atlassian.sourcemap.Mapping;
import com.atlassian.sourcemap.WritableSourceMap;
import com.atlassian.sourcemap.WritableSourceMapImpl;
import java.util.List;
import java.util.function.Consumer;

public interface ReadableSourceMap {
    public Mapping getMapping(int var1, int var2);

    public List<String> getSources();

    public List<String> getNames();

    public List<String> getSourcesContent();

    public int getOffset();

    public void addOffset(int var1);

    public void eachMapping(Consumer<Mapping> var1);

    public static WritableSourceMap toWritableSourceMap(ReadableSourceMap readableSourceMap) {
        if (readableSourceMap == null) {
            return null;
        }
        WritableSourceMap writableSourceMap = new WritableSourceMapImpl.Builder().withSourcesAndSourcesContent(readableSourceMap.getSources(), readableSourceMap.getSourcesContent()).build();
        readableSourceMap.eachMapping(writableSourceMap::addMapping);
        return writableSourceMap;
    }
}

