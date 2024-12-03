/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sourcemap.ReadableSourceMap
 *  javax.annotation.Nonnull
 */
package com.atlassian.plugin.webresource.impl.support.factory;

import com.atlassian.plugin.webresource.impl.http.Router;
import com.atlassian.plugin.webresource.impl.snapshot.resource.Resource;
import com.atlassian.plugin.webresource.impl.support.InitialContent;
import com.atlassian.plugin.webresource.impl.support.Support;
import com.atlassian.plugin.webresource.impl.support.factory.InitialSourceContent;
import com.atlassian.sourcemap.ReadableSourceMap;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import javax.annotation.Nonnull;

class InitialSourceContentBuilder {
    private InputStream content;
    private String path;
    private ReadableSourceMap sourceMap;

    InitialSourceContentBuilder() {
    }

    @Nonnull
    InitialSourceContentBuilder withContent(@Nonnull Resource resource) {
        if (Objects.nonNull(resource.getLocation())) {
            String prebuildSourcePath = Resource.getPrebuiltSourcePath(resource.getLocation());
            try (InputStream sourceStream = resource.getStreamFor(prebuildSourcePath);){
                this.content = sourceStream;
            }
            catch (IOException exception) {
                String message = String.format("Error while reading the source file for the resource with location %s.", resource.getLocation());
                Support.LOGGER.warn(message, (Throwable)exception);
            }
        }
        return this;
    }

    @Nonnull
    InitialSourceContentBuilder withPath(@Nonnull Router router, @Nonnull Resource resource) {
        if (Objects.isNull(this.content)) {
            this.withContent(resource);
        }
        this.path = Objects.isNull(this.content) ? router.sourceUrl(resource) : router.prebuildSourceUrl(resource);
        return this;
    }

    @Nonnull
    InitialSourceContentBuilder withSourceMap(@Nonnull Resource resource) {
        this.sourceMap = Support.getSourceMap(resource.getPath(), resource, this.path);
        return this;
    }

    @Nonnull
    InitialContent build() {
        return new InitialSourceContent(this.content, this.path, this.sourceMap);
    }
}

