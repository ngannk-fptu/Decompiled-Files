/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.sourcemap;

import com.atlassian.sourcemap.InternalUtil;
import com.atlassian.sourcemap.Mapping;
import com.atlassian.sourcemap.ReadableSourceMap;
import com.atlassian.sourcemap.SourceMapConsumer;
import java.io.InputStream;
import java.io.Reader;
import java.util.List;
import java.util.function.Consumer;

public class ReadableSourceMapImpl
implements ReadableSourceMap {
    private final SourceMapConsumer sourcemapConsumer;
    private int offset;

    private ReadableSourceMapImpl(String sourceMap, int offset) {
        this.offset = offset;
        this.sourcemapConsumer = new SourceMapConsumer(sourceMap);
    }

    public static ReadableSourceMap fromSourceWithOffset(String sourceMap, int offset) {
        return new ReadableSourceMapImpl(sourceMap, offset);
    }

    public static ReadableSourceMap fromSource(String sourceMap) {
        return new ReadableSourceMapImpl(sourceMap, 0);
    }

    public static ReadableSourceMap fromSource(InputStream sourceMap) {
        return new ReadableSourceMapImpl(InternalUtil.toString(sourceMap), 0);
    }

    public static ReadableSourceMap fromSource(Reader sourceMap) {
        return new ReadableSourceMapImpl(InternalUtil.toString(sourceMap), 0);
    }

    @Override
    public List<String> getSources() {
        return this.sourcemapConsumer.getSourceFileNames();
    }

    @Override
    public List<String> getSourcesContent() {
        return this.sourcemapConsumer.getSourcesContent();
    }

    @Override
    public int getOffset() {
        return this.offset;
    }

    @Override
    public void addOffset(int offset) {
        this.offset += offset;
    }

    @Override
    public List<String> getNames() {
        return this.sourcemapConsumer.getSourceSymbolNames();
    }

    private void ensureOffset() {
        if (this.offset > 0) {
            this.sourcemapConsumer.recalculateWithOffset(this.offset);
        }
        this.offset = 0;
    }

    @Override
    public Mapping getMapping(int lineNumber, int column) {
        this.ensureOffset();
        return this.sourcemapConsumer.getMapping(lineNumber - this.offset, column);
    }

    @Override
    public void eachMapping(Consumer<Mapping> callback) {
        this.ensureOffset();
        this.sourcemapConsumer.eachMapping(callback);
    }
}

