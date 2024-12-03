/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.webresource.api.UrlMode
 *  javax.annotation.Nonnull
 */
package com.atlassian.plugin.webresource.assembler.html;

import com.atlassian.plugin.webresource.PrefetchLinkWebResource;
import com.atlassian.plugin.webresource.assembler.ResourceUrls;
import com.atlassian.plugin.webresource.assembler.html.HtmlTagFormatter;
import com.atlassian.webresource.api.UrlMode;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;

public final class PrefetchHtmlFormatter
implements HtmlTagFormatter {
    private static final Map<UrlMode, PrefetchHtmlFormatter> FORMATTERS = new EnumMap<UrlMode, PrefetchHtmlFormatter>(UrlMode.class);
    private final UrlMode urlMode;

    private PrefetchHtmlFormatter(@Nonnull UrlMode urlMode) {
        this.urlMode = Objects.requireNonNull(urlMode, "The url mode is mandatory to build a prefetch formatter.");
    }

    @Nonnull
    public static PrefetchHtmlFormatter getInstance(@Nonnull UrlMode urlMode) {
        Objects.requireNonNull(urlMode, "The url mode is mandatory for the retriaval of the instance of PrefetchHtmlFormatter.");
        return FORMATTERS.get(urlMode);
    }

    @Override
    @Nonnull
    public String format(@Nonnull ResourceUrls resourceUrls) {
        Objects.requireNonNull(resourceUrls, "The resource urls are mandatory for the creation of the script tag");
        return PrefetchLinkWebResource.FORMATTER.formatResource(resourceUrls.getPluginUrlResource().getStaticUrl(this.urlMode), resourceUrls.getPluginUrlResource().getParams().all());
    }

    @Override
    public boolean matches(@Nonnull String resourceName) {
        Objects.requireNonNull(resourceName, "The resource name is mandatory for the comparison.");
        return PrefetchLinkWebResource.FORMATTER.matches(resourceName);
    }

    static {
        for (UrlMode urlMode : UrlMode.values()) {
            FORMATTERS.put(urlMode, new PrefetchHtmlFormatter(urlMode));
        }
    }
}

