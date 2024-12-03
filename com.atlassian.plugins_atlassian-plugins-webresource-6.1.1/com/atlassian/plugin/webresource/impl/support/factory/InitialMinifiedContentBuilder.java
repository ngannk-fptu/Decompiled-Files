/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sourcemap.ReadableSourceMap
 *  com.atlassian.webresource.spi.CompilerUtil
 *  com.atlassian.webresource.spi.ResourceCompiler
 *  javax.annotation.Nonnull
 */
package com.atlassian.plugin.webresource.impl.support.factory;

import com.atlassian.plugin.webresource.impl.Globals;
import com.atlassian.plugin.webresource.impl.config.Config;
import com.atlassian.plugin.webresource.impl.http.Router;
import com.atlassian.plugin.webresource.impl.snapshot.resource.Resource;
import com.atlassian.plugin.webresource.impl.support.InitialContent;
import com.atlassian.plugin.webresource.impl.support.Support;
import com.atlassian.plugin.webresource.impl.support.factory.InitialMinifiedContent;
import com.atlassian.plugin.webresource.impl.support.factory.InitialSourceContentBuilder;
import com.atlassian.sourcemap.ReadableSourceMap;
import com.atlassian.webresource.spi.CompilerUtil;
import com.atlassian.webresource.spi.ResourceCompiler;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;

class InitialMinifiedContentBuilder {
    private static final String ALTERNATE_MINIFIED_PATTERN_EXTENSION = ".min.%s";
    private static final String EMTPY = "";
    private static final String EXTENSION_PATTERN = ".%s";
    private static final String MINIFIED_PATTERN_EXTENSION = "-min.%s";
    private static final Collection<String> MINIFIABLE_EXTENSIONS = Arrays.asList("css", "js");
    private final Config config;
    private final Router router;
    private final InitialSourceContentBuilder initialSourceContentBuilder;
    private InputStream content;
    private String path;
    private ReadableSourceMap sourceMap;

    InitialMinifiedContentBuilder(@Nonnull Globals globals, @Nonnull InitialSourceContentBuilder initialSourceContentBuilder) {
        this.config = Objects.requireNonNull(globals.getConfig(), "The global configuration is mandatory for the creation of a minified content builder.");
        this.initialSourceContentBuilder = Objects.requireNonNull(initialSourceContentBuilder, "The initial content source builder is mandatory.");
        this.router = Objects.requireNonNull(globals.getRouter(), "The router information is mandatory for the creation of a minified content builder.");
    }

    private static String getAlternatePath(String path) {
        int lastDot = path.lastIndexOf(".");
        return path.substring(0, lastDot) + ".min" + path.substring(lastDot);
    }

    private static String getPath(String path) {
        int lastDot = path.lastIndexOf(".");
        return path.substring(0, lastDot) + "-min" + path.substring(lastDot);
    }

    @Nonnull
    InitialMinifiedContentBuilder withContent(@Nonnull Resource resource) {
        if (this.isMinificationEnabled(resource)) {
            String path;
            if (Objects.isNull(this.content)) {
                path = InitialMinifiedContentBuilder.getPath(resource.getPath());
                this.content = resource.getStreamFor(path);
            }
            if (Objects.isNull(this.content)) {
                path = InitialMinifiedContentBuilder.getAlternatePath(resource.getPath());
                this.content = resource.getStreamFor(path);
            }
            if (Objects.isNull(this.content) && this.config.isGlobalMinificationEnabled() && "js".equals(resource.getNameOrLocationType())) {
                this.content = CompilerUtil.toInputStream((ResourceCompiler)this.config.getResourceCompiler(), (String)resource.getPath());
            }
        }
        return this;
    }

    @Nonnull
    InitialMinifiedContentBuilder withPath(@Nonnull Resource resource) {
        if (this.isMinificationEnabled(resource)) {
            this.path = this.withContent(resource).build().getContent().map(minifiedStream -> InitialMinifiedContentBuilder.getPath(resource.getPath())).orElseGet(() -> InitialMinifiedContentBuilder.getAlternatePath(resource.getPath()));
        }
        return this;
    }

    @Nonnull
    InitialMinifiedContentBuilder withSourceMap(@Nonnull Resource resource) {
        String sourcePath = this.initialSourceContentBuilder.withContent(resource).withPath(this.router, resource).build().getPath().orElse(null);
        this.sourceMap = Support.getSourceMap(this.path, resource, sourcePath);
        return this;
    }

    @Nonnull
    InitialContent build() {
        return new InitialMinifiedContent(this.content, this.path, this.sourceMap);
    }

    private boolean isMinificationEnabled(Resource resource) {
        boolean isMinificationEnabled;
        boolean bl = isMinificationEnabled = resource.getParent().isMinificationEnabled() && this.config.isMinificationEnabled();
        if (isMinificationEnabled) {
            return MINIFIABLE_EXTENSIONS.stream().anyMatch(minifiableExtension -> {
                String path = Optional.ofNullable(resource.getPath()).map(String::toLowerCase).orElse(EMTPY);
                String extension = String.format(EXTENSION_PATTERN, minifiableExtension);
                String minifiedExtension = String.format(MINIFIED_PATTERN_EXTENSION, minifiableExtension);
                String alternateMinifiedExtension = String.format(ALTERNATE_MINIFIED_PATTERN_EXTENSION, minifiableExtension);
                return path.endsWith(extension) && !path.endsWith(minifiedExtension) && !path.endsWith(alternateMinifiedExtension);
            });
        }
        return isMinificationEnabled;
    }
}

