/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.CybernekoHtmlToXmlConverter
 */
package com.atlassian.confluence.content.render.xhtml;

import com.atlassian.confluence.content.render.xhtml.CybernekoHtmlToXmlConverter;
import com.atlassian.confluence.content.render.xhtml.HtmlToXmlConverter;
import com.atlassian.confluence.content.render.xhtml.StaxUtils;

public class DefaultHtmlToXmlConverter
implements HtmlToXmlConverter {
    private final CybernekoHtmlToXmlConverter cybernekoConverter = new CybernekoHtmlToXmlConverter();

    @Override
    public String convert(String unclean) {
        return this.cybernekoConverter.convert(StaxUtils.stripIllegalControlChars(unclean).toString());
    }
}

