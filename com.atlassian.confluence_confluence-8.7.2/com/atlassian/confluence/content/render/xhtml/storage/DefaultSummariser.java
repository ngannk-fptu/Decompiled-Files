/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.util.RendererUtil
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.content.render.xhtml.storage;

import com.atlassian.confluence.content.render.xhtml.storage.Summariser;
import com.atlassian.confluence.util.HTMLSearchableTextUtil;
import com.atlassian.renderer.util.RendererUtil;
import org.apache.commons.lang3.StringUtils;
import org.xml.sax.SAXException;

public class DefaultSummariser
implements Summariser {
    @Override
    public String summarise(String xhtml) {
        String stripped = xhtml;
        try {
            stripped = HTMLSearchableTextUtil.stripTags(xhtml);
        }
        catch (SAXException sAXException) {
            // empty catch block
        }
        return RendererUtil.summariseWithoutStrippingWikiCharacters((String)stripped);
    }

    @Override
    public String summarise(String xhtml, int maxLength, boolean ellipses) {
        String stripped = xhtml;
        try {
            stripped = HTMLSearchableTextUtil.stripTags(xhtml);
        }
        catch (SAXException sAXException) {
            // empty catch block
        }
        if (ellipses) {
            return StringUtils.abbreviate((String)stripped, (int)maxLength);
        }
        return StringUtils.left((String)stripped, (int)maxLength);
    }
}

