/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.webresource.api.UrlMode
 *  javax.annotation.Nonnull
 */
package com.atlassian.plugin.webresource.assembler.html;

import com.atlassian.plugin.webresource.assembler.ResourceUrls;
import com.atlassian.plugin.webresource.assembler.html.CssTagFormatter;
import com.atlassian.plugin.webresource.assembler.html.HtmlTagFormatter;
import com.atlassian.plugin.webresource.assembler.html.HtmlTagWriter;
import com.atlassian.plugin.webresource.assembler.html.JavaScriptTagFormatter;
import com.atlassian.plugin.webresource.impl.RequestState;
import com.atlassian.webresource.api.UrlMode;
import java.io.Writer;
import java.util.Arrays;
import java.util.Objects;
import javax.annotation.Nonnull;

final class DeferHtmlTagWriter
extends HtmlTagWriter {
    DeferHtmlTagWriter(@Nonnull RequestState requestState, @Nonnull Writer writer, @Nonnull UrlMode urlMode) {
        super(requestState, writer, Arrays.asList(new CssTagFormatter(urlMode), new JavaScriptTagFormatter(urlMode, true)));
    }

    @Override
    @Nonnull
    String generateHtmlTag(@Nonnull ResourceUrls resourceUrls, @Nonnull HtmlTagFormatter formatter) {
        Objects.requireNonNull(resourceUrls, "The resource urls are mandatory for the creation of the script tag.");
        Objects.requireNonNull(formatter, "The formatter is mandatory for generating the tags.");
        return formatter.format(resourceUrls);
    }
}

