/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.apache.commons.collections.CollectionUtils
 *  org.apache.commons.lang3.StringEscapeUtils
 */
package com.atlassian.plugin.webresource;

import com.atlassian.plugin.webresource.AbstractWebResourceFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringEscapeUtils;

public class CssWebResource
extends AbstractWebResourceFormatter {
    private static final String ATTRIBUTE_JOINER = " ";
    private static final String ATTRIBUTE_MEDIAL_ALL = " media=\"all\"";
    private static final List<String> STYLESHEET_PARAMETERS_HANDLED = Arrays.asList("title", "media", "charset", "data-wrm-key", "data-wrm-batch-type");
    private static final String STYLESHEET_TAG_BEGIN = "<link rel=\"stylesheet\" href=\"";
    private static final String STYLESHEET_TAG_EMPTY = "";
    private static final String STYLESHEET_TAG_END = ">\n";

    @Override
    public boolean matches(@Nullable String name) {
        return Optional.ofNullable(name).filter(value -> name.endsWith(".css")).isPresent();
    }

    @Override
    @Nonnull
    public String formatResource(String url, Map<String, String> attributes) {
        if (this.isValid(attributes)) {
            StringBuilder buffer = new StringBuilder().append(STYLESHEET_TAG_BEGIN).append(StringEscapeUtils.escapeHtml4((String)url)).append('\"');
            List<String> tokens = this.getParametersAsAttributes(attributes);
            if (CollectionUtils.isNotEmpty(tokens)) {
                buffer.append(ATTRIBUTE_JOINER).append(String.join((CharSequence)ATTRIBUTE_JOINER, tokens));
            }
            String mediaAttribute = Optional.ofNullable(attributes.get("media")).map(attribute -> STYLESHEET_TAG_EMPTY).orElse(ATTRIBUTE_MEDIAL_ALL);
            return buffer.append(mediaAttribute).append(STYLESHEET_TAG_END).toString();
        }
        return STYLESHEET_TAG_EMPTY;
    }

    @Override
    protected List<String> getAttributeParameters() {
        return STYLESHEET_PARAMETERS_HANDLED;
    }
}

