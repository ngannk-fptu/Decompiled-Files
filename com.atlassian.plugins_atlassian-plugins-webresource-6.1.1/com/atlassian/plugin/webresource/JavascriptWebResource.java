/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.StringEscapeUtils
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.plugin.webresource;

import com.atlassian.plugin.webresource.AbstractWebResourceFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

public class JavascriptWebResource
extends AbstractWebResourceFormatter {
    private static final String ATTRIBUTE_JOINER = " ";
    private static final List<String> HANDLED_PARAMETERS = Arrays.asList("charset", "data-wrm-key", "data-wrm-batch-type", "data-initially-rendered", "async", "defer");
    private static final String JAVASCRIPT_TAG_BEGIN = "<script src=\"";
    private static final String JAVASCRIPT_TAG_EMPTY = "";
    private static final String JAVASCRIPT_TAG_END = "></script>\n";

    @Override
    public boolean matches(@Nullable String name) {
        return Optional.ofNullable(name).filter(value -> name.endsWith(".js")).isPresent();
    }

    @Override
    public String formatResource(String url, Map<String, String> attributes) {
        if (this.isValid(attributes)) {
            StringBuilder scriptTagBuilder = new StringBuilder(JAVASCRIPT_TAG_BEGIN).append(StringEscapeUtils.escapeHtml4((String)url)).append("\" ");
            return scriptTagBuilder.append(StringUtils.join(this.getParametersAsAttributes(attributes).iterator(), (String)ATTRIBUTE_JOINER)).append(JAVASCRIPT_TAG_END).toString();
        }
        return JAVASCRIPT_TAG_EMPTY;
    }

    @Override
    protected List<String> getAttributeParameters() {
        return HANDLED_PARAMETERS;
    }

    @Override
    boolean isValid(Map<String, String> attributes) {
        return super.isValid(attributes);
    }
}

