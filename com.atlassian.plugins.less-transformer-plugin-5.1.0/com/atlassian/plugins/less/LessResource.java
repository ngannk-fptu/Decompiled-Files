/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.lesscss.LessCompiler
 *  com.atlassian.lesscss.Loader
 *  com.atlassian.lesscss.spi.LessCssCompilationEvent
 *  com.atlassian.plugin.servlet.DownloadableResource
 *  com.atlassian.plugin.webresource.transformer.CharSequenceDownloadableResource
 */
package com.atlassian.plugins.less;

import com.atlassian.event.api.EventPublisher;
import com.atlassian.lesscss.LessCompiler;
import com.atlassian.lesscss.Loader;
import com.atlassian.lesscss.spi.LessCssCompilationEvent;
import com.atlassian.plugin.servlet.DownloadableResource;
import com.atlassian.plugin.webresource.transformer.CharSequenceDownloadableResource;
import java.net.URI;

class LessResource
extends CharSequenceDownloadableResource {
    private static final String ATLASSIAN_WEBRESOURCE_DISABLE_MINIFICATION = "atlassian.webresource.disable.minification";
    private final LessCompiler compiler;
    private final Loader loader;
    private final URI resourceUri;
    private final EventPublisher eventPublisher;

    public LessResource(DownloadableResource originalResource, LessCompiler compiler, Loader loader, URI resourceUri, EventPublisher eventPublisher) {
        super(originalResource);
        this.compiler = compiler;
        this.loader = loader;
        this.resourceUri = resourceUri;
        this.eventPublisher = eventPublisher;
    }

    public String getContentType() {
        return "text/css";
    }

    protected CharSequence transform(CharSequence original) {
        this.eventPublisher.publish((Object)new LessCssCompilationEvent(this.resourceUri));
        boolean compress = !LessResource.isMinificationDisabled();
        return this.compiler.compile(this.loader, this.resourceUri, original, compress);
    }

    private static boolean isMinificationDisabled() {
        return Boolean.getBoolean(ATLASSIAN_WEBRESOURCE_DISABLE_MINIFICATION) || Boolean.getBoolean("atlassian.dev.mode");
    }
}

