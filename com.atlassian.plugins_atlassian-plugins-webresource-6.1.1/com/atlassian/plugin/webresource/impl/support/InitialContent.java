/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sourcemap.ReadableSourceMap
 *  javax.annotation.Nullable
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.plugin.webresource.impl.support;

import com.atlassian.plugin.webresource.impl.support.Content;
import com.atlassian.sourcemap.ReadableSourceMap;
import java.io.InputStream;
import java.util.Optional;
import javax.annotation.Nullable;
import org.checkerframework.checker.nullness.qual.NonNull;

public abstract class InitialContent {
    private final InputStream content;
    private final String path;
    private final ReadableSourceMap sourceMap;

    public InitialContent(@Nullable InputStream content, @Nullable String path, @Nullable ReadableSourceMap sourceMap) {
        this.content = content;
        this.path = path;
        this.sourceMap = sourceMap;
    }

    public @NonNull Optional<InputStream> getContent() {
        return Optional.ofNullable(this.content);
    }

    public @NonNull Optional<String> getPath() {
        return Optional.ofNullable(this.path);
    }

    public @NonNull Optional<ReadableSourceMap> getSourceMap() {
        return Optional.ofNullable(this.sourceMap);
    }

    public abstract @NonNull Content toContent(@NonNull Content var1);
}

