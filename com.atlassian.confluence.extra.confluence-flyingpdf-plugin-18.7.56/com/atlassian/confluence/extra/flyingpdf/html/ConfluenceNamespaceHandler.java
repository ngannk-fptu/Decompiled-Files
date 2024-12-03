/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.extra.flyingpdf.html;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;
import org.xhtmlrenderer.simple.extend.XhtmlNamespaceHandler;

public class ConfluenceNamespaceHandler
extends XhtmlNamespaceHandler {
    private String baseUrl;

    public ConfluenceNamespaceHandler(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public String getLinkUri(Element e) {
        Object uri = super.getLinkUri(e);
        if (StringUtils.isNotBlank((CharSequence)uri) && ((String)uri).charAt(0) == '/') {
            uri = this.baseUrl + (String)uri;
        }
        return uri;
    }
}

