/*
 * Decompiled with CFR 0.152.
 */
package org.radeox.filter;

import org.radeox.filter.LinkTestFilter;

public class WikiLinkFilter
extends LinkTestFilter {
    protected String getLocaleKey() {
        return "filter.wikilink";
    }

    protected String getWikiView(String name) {
        return name;
    }
}

