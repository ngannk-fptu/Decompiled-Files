/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 *  org.apache.commons.lang3.BooleanUtils
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.plugin.webresource;

import com.atlassian.plugin.webresource.WebResourceFormatter;
import com.google.common.base.Strings;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

abstract class AbstractWebResourceFormatter
implements WebResourceFormatter {
    AbstractWebResourceFormatter() {
    }

    protected abstract List<String> getAttributeParameters();

    protected List<String> getParametersAsAttributes(Map params) {
        ArrayList<String> attributes = new ArrayList<String>();
        Iterator iterator = params.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry param;
            Map.Entry entry = param = iterator.next();
            String key = (String)entry.getKey();
            String value = (String)entry.getValue();
            if (!StringUtils.isNotBlank((CharSequence)key) || !this.getAttributeParameters().contains(key.toLowerCase())) continue;
            String attribute = key + (value.equals("") ? "" : "=\"" + value + "\"");
            attributes.add(attribute);
        }
        return attributes;
    }

    boolean isValid(Map<String, String> attributes) {
        String ieOnly = attributes.getOrDefault("ieonly", "false");
        String conditionalComment = attributes.getOrDefault("conditionalComment", "");
        boolean isNotForIeOnly = !BooleanUtils.toBoolean((String)ieOnly);
        boolean isNotConditionallyForIeOnly = Strings.isNullOrEmpty((String)conditionalComment);
        return isNotForIeOnly && isNotConditionallyForIeOnly;
    }
}

