/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.sourcemap;

import com.atlassian.sourcemap.ReadableSourceMap;
import com.atlassian.sourcemap.WritableSourceMap;
import com.atlassian.sourcemap.WritableSourceMapImpl;
import java.util.ArrayList;
import java.util.List;

public class SourceMapJoiner {
    List<ReadSourceMapWithOffset> sourceMaps = new ArrayList<ReadSourceMapWithOffset>();

    public void add(ReadableSourceMap sourceMap, int length) {
        this.add(sourceMap, length, 0);
    }

    public void add(ReadableSourceMap sourceMap, int length, int offset) {
        sourceMap.addOffset(offset);
        this.sourceMaps.add(new ReadSourceMapWithOffset(sourceMap, length));
    }

    public WritableSourceMap join() {
        WritableSourceMap joinedMap = new WritableSourceMapImpl.Builder().empty().build();
        int lineOffset = 0;
        for (ReadSourceMapWithOffset sourceMapWithOffset : this.sourceMaps) {
            int currentLineOffset = lineOffset;
            lineOffset += sourceMapWithOffset.linesCount;
            ReadableSourceMap sourceMap = sourceMapWithOffset.sourceMap;
            if (sourceMap == null) continue;
            joinedMap.addSourcesAndContents(sourceMap.getSources(), sourceMap.getSourcesContent());
            sourceMap.addOffset(currentLineOffset);
            sourceMap.eachMapping(joinedMap::addMapping);
        }
        return joinedMap;
    }

    static class ReadSourceMapWithOffset {
        ReadableSourceMap sourceMap;
        int linesCount;

        public ReadSourceMapWithOffset(ReadableSourceMap sourceMap, int linesCount) {
            this.sourceMap = sourceMap;
            this.linesCount = linesCount;
        }
    }
}

