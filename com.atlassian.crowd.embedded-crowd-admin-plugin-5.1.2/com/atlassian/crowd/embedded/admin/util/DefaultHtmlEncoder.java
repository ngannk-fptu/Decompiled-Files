/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringEscapeUtils
 */
package com.atlassian.crowd.embedded.admin.util;

import com.atlassian.crowd.embedded.admin.util.HtmlEncoder;
import org.apache.commons.lang3.StringEscapeUtils;

public class DefaultHtmlEncoder
implements HtmlEncoder {
    @Override
    public String encode(String encodeMe) {
        return StringEscapeUtils.escapeHtml4((String)encodeMe);
    }
}

