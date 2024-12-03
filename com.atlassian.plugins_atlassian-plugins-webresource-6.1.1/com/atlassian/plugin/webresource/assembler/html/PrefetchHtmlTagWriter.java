/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.webresource.api.UrlMode
 *  javax.annotation.Nonnull
 */
package com.atlassian.plugin.webresource.assembler.html;

import com.atlassian.plugin.webresource.BatchResourceContentsWebFormatter;
import com.atlassian.plugin.webresource.assembler.ResourceUrls;
import com.atlassian.plugin.webresource.assembler.html.HtmlTagFormatter;
import com.atlassian.plugin.webresource.assembler.html.HtmlTagWriter;
import com.atlassian.plugin.webresource.assembler.html.PrefetchHtmlFormatter;
import com.atlassian.plugin.webresource.impl.RequestState;
import com.atlassian.plugin.webresource.impl.config.Config;
import com.atlassian.webresource.api.UrlMode;
import java.io.Writer;
import java.util.Collections;
import java.util.Objects;
import javax.annotation.Nonnull;

public class PrefetchHtmlTagWriter
extends HtmlTagWriter {
    private final Config configuration;

    public PrefetchHtmlTagWriter(@Nonnull Config configuration, @Nonnull RequestState requestState, @Nonnull Writer writer, @Nonnull UrlMode urlMode) {
        super(requestState, writer, Collections.singletonList(PrefetchHtmlFormatter.getInstance(urlMode)));
        this.configuration = Objects.requireNonNull(configuration, "The configuration is mandatory for the creation of PrefetchHtmlTagWriter.");
    }

    @Override
    @Nonnull
    String generateHtmlTag(@Nonnull ResourceUrls resource, @Nonnull HtmlTagFormatter formatter) {
        if (this.configuration.isBatchContentTrackingEnabled()) {
            String formattedResource = formatter.format(resource);
            return BatchResourceContentsWebFormatter.insertBatchResourceContents(resource, formattedResource);
        }
        return formatter.format(resource);
    }
}

