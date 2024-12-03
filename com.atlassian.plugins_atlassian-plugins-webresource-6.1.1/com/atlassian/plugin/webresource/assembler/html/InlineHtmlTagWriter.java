/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.webresource.api.UrlMode
 *  javax.annotation.Nonnull
 */
package com.atlassian.plugin.webresource.assembler.html;

import com.atlassian.plugin.webresource.assembler.ResourceUrls;
import com.atlassian.plugin.webresource.assembler.html.HtmlTagFormatter;
import com.atlassian.plugin.webresource.assembler.html.HtmlTagWriter;
import com.atlassian.plugin.webresource.assembler.html.JavaScriptSyncResourceWriter;
import com.atlassian.plugin.webresource.impl.RequestState;
import com.atlassian.plugin.webresource.impl.config.Config;
import com.atlassian.webresource.api.UrlMode;
import java.io.Writer;
import java.util.Collection;
import java.util.Collections;
import javax.annotation.Nonnull;

public class InlineHtmlTagWriter
extends HtmlTagWriter {
    private final JavaScriptSyncResourceWriter jsWriter;

    public InlineHtmlTagWriter(Config config, RequestState requestState, Writer writer, UrlMode urlMode) {
        super(requestState, writer, Collections.emptyList());
        this.jsWriter = new JavaScriptSyncResourceWriter(requestState, writer);
    }

    @Override
    public void writeHtmlTag(@Nonnull Collection<ResourceUrls> resources) {
        this.jsWriter.write(resources);
    }

    @Override
    @Nonnull
    String generateHtmlTag(@Nonnull ResourceUrls resource, @Nonnull HtmlTagFormatter formatter) {
        throw new RuntimeException("This should never get called.");
    }
}

