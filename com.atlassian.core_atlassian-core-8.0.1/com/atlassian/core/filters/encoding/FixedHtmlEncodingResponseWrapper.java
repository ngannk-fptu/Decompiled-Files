/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.http.HttpServletResponseWrapper
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.core.filters.encoding;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import org.apache.commons.lang3.StringUtils;

public final class FixedHtmlEncodingResponseWrapper
extends HttpServletResponseWrapper {
    public FixedHtmlEncodingResponseWrapper(HttpServletResponse response) {
        super(response);
    }

    public final void setContentType(String contentType) {
        if (StringUtils.startsWith((CharSequence)contentType, (CharSequence)"text/html") && contentType.length() > "text/html".length()) {
            return;
        }
        if (StringUtils.trimToEmpty((String)contentType).equals("text/html")) {
            super.setContentType(contentType + ";charset=" + this.getResponse().getCharacterEncoding());
            return;
        }
        super.setContentType(contentType);
    }
}

