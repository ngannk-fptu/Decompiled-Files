/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringEscapeUtils
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.plugin.webresource;

import com.atlassian.plugin.webresource.AbstractWebResourceFormatter;
import com.atlassian.plugin.webresource.WebResourceFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

public class PrefetchLinkWebResource
extends AbstractWebResourceFormatter {
    public static final WebResourceFormatter FORMATTER = new PrefetchLinkWebResource();
    private static final List<String> HANDLED_PARAMETERS = Arrays.asList("title", "media", "crossorigin");

    @Override
    public boolean matches(String name) {
        return name != null && (name.endsWith(".js") || name.endsWith(".css") || name.endsWith(".less") || name.endsWith(".soy"));
    }

    @Override
    public String formatResource(String url, Map<String, String> attributes) {
        if (!this.isValid(attributes)) {
            return "";
        }
        StringBuilder buffer = new StringBuilder("<link rel=\"prefetch\" href=\"");
        buffer.append(StringEscapeUtils.escapeHtml4((String)url)).append("\"");
        List<String> attributeTokens = this.getParametersAsAttributes(attributes);
        if (attributes.size() > 0) {
            buffer.append(" ").append(StringUtils.join(attributeTokens.iterator(), (String)" "));
        }
        buffer.append(">\n");
        return buffer.toString();
    }

    @Override
    protected List<String> getAttributeParameters() {
        return HANDLED_PARAMETERS;
    }
}

