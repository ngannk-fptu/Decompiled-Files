/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringEscapeUtils
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.streams.spi;

import java.net.URI;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

@Deprecated
public final class StreamsLinks {
    private StreamsLinks() {
        throw new RuntimeException("StreamsLinks cannot be instantiated");
    }

    @Deprecated
    public static String nullSafeLink(URI uri, String label) {
        String escapedLabel = StringEscapeUtils.escapeHtml4((String)label);
        return uri != null && StringUtils.isNotBlank((CharSequence)uri.toASCIIString()) ? "<a href=\"" + uri.toASCIIString() + "\">" + escapedLabel + "</a>" : escapedLabel;
    }

    @Deprecated
    public static String nullSafeLink(URI uri, String label, String styleClass) {
        String escapedLabel = StringEscapeUtils.escapeHtml4((String)label);
        return uri != null && StringUtils.isNotBlank((CharSequence)uri.toASCIIString()) ? "<a href=\"" + uri.toASCIIString() + "\" class=\"" + styleClass + "\">" + escapedLabel + "</a>" : "<span class=\"" + styleClass + "\">" + escapedLabel + "</span>";
    }
}

