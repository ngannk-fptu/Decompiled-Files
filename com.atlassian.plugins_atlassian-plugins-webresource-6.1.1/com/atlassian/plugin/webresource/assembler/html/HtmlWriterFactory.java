/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.webresource.api.UrlMode
 *  com.atlassian.webresource.api.assembler.resource.ResourcePhase
 *  javax.annotation.Nonnull
 */
package com.atlassian.plugin.webresource.assembler.html;

import com.atlassian.plugin.webresource.assembler.html.DefaultHtmlTagWriter;
import com.atlassian.plugin.webresource.assembler.html.DeferHtmlTagWriter;
import com.atlassian.plugin.webresource.assembler.html.HtmlTagWriter;
import com.atlassian.plugin.webresource.assembler.html.InlineHtmlTagWriter;
import com.atlassian.plugin.webresource.assembler.html.InteractiveHtmlTagWriter;
import com.atlassian.plugin.webresource.impl.RequestState;
import com.atlassian.plugin.webresource.impl.config.Config;
import com.atlassian.webresource.api.UrlMode;
import com.atlassian.webresource.api.assembler.resource.ResourcePhase;
import java.io.Writer;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import javax.annotation.Nonnull;

public class HtmlWriterFactory {
    private final Map<ResourcePhase, BiFunction<Writer, UrlMode, HtmlTagWriter>> htmlWriterBuilderByPhase;

    public HtmlWriterFactory(@Nonnull Config config, @Nonnull RequestState requestState) {
        Objects.requireNonNull(config, "The config is mandatory.");
        Objects.requireNonNull(requestState, "The requestState is mandatory.");
        this.htmlWriterBuilderByPhase = new EnumMap<ResourcePhase, BiFunction<Writer, UrlMode, HtmlTagWriter>>(ResourcePhase.class);
        this.htmlWriterBuilderByPhase.put(ResourcePhase.INLINE, (writer, urlMode) -> new InlineHtmlTagWriter(config, requestState, (Writer)writer, (UrlMode)urlMode));
        this.htmlWriterBuilderByPhase.put(ResourcePhase.REQUIRE, (writer, urlMode) -> new DefaultHtmlTagWriter(config, requestState, (Writer)writer, (UrlMode)urlMode));
        this.htmlWriterBuilderByPhase.put(ResourcePhase.DEFER, (writer, urlMode) -> new DeferHtmlTagWriter(requestState, (Writer)writer, (UrlMode)urlMode));
        this.htmlWriterBuilderByPhase.put(ResourcePhase.INTERACTION, (writer, urlMode) -> new InteractiveHtmlTagWriter(requestState, (Writer)writer, (UrlMode)urlMode));
    }

    @Nonnull
    public HtmlTagWriter get(@Nonnull ResourcePhase resourcePhase, @Nonnull Writer writer, @Nonnull UrlMode urlMode) {
        Objects.requireNonNull(resourcePhase, "The resource phase is mandatory.");
        Objects.requireNonNull(writer, "The writer is mandatory.");
        Objects.requireNonNull(urlMode, "The urlMode is mandatory.");
        BiFunction<Writer, UrlMode, HtmlTagWriter> defaultWriterBuilder = this.htmlWriterBuilderByPhase.get(ResourcePhase.defaultPhase());
        return this.htmlWriterBuilderByPhase.getOrDefault(resourcePhase, defaultWriterBuilder).apply(writer, urlMode);
    }
}

