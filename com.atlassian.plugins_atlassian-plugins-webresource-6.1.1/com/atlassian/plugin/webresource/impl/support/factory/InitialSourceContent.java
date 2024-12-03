/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sourcemap.ReadableSourceMap
 *  javax.annotation.Nullable
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.plugin.webresource.impl.support.factory;

import com.atlassian.plugin.webresource.impl.support.Content;
import com.atlassian.plugin.webresource.impl.support.InitialContent;
import com.atlassian.plugin.webresource.impl.support.factory.InitialSourceContentBuilder;
import com.atlassian.sourcemap.ReadableSourceMap;
import java.io.InputStream;
import java.io.OutputStream;
import javax.annotation.Nullable;
import org.checkerframework.checker.nullness.qual.NonNull;

class InitialSourceContent
extends InitialContent {
    InitialSourceContent(@Nullable InputStream content, @Nullable String path, @Nullable ReadableSourceMap sourceMap) {
        super(content, path, sourceMap);
    }

    static @NonNull InitialSourceContentBuilder builder() {
        return new InitialSourceContentBuilder();
    }

    @Override
    public @NonNull Content toContent(final @NonNull Content originalContent) {
        return new Content(){

            @Override
            public ReadableSourceMap writeTo(OutputStream originalContentStream, boolean isSourceMapEnabled) {
                originalContent.writeTo(originalContentStream, isSourceMapEnabled);
                return InitialSourceContent.this.getSourceMap().filter(sourceMap -> isSourceMapEnabled).orElse(null);
            }

            @Override
            public String getContentType() {
                return originalContent.getContentType();
            }

            @Override
            public boolean isTransformed() {
                return false;
            }
        };
    }
}

