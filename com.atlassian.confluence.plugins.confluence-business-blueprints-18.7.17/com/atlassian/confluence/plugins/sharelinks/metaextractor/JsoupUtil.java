/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jsoup.nodes.Document
 *  org.jsoup.nodes.Element
 *  org.jsoup.select.Elements
 */
package com.atlassian.confluence.plugins.sharelinks.metaextractor;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class JsoupUtil {
    public static String getMetaContent(Document doc, String elementQuery) {
        Elements metaElements = doc.select(elementQuery);
        if (metaElements.isEmpty()) {
            return null;
        }
        return ((Element)metaElements.get(0)).attr("content");
    }

    private JsoupUtil() {
    }
}

