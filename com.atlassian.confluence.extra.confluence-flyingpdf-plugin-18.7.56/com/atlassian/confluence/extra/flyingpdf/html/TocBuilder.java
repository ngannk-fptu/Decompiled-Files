/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.flyingpdf.html;

import com.atlassian.confluence.extra.flyingpdf.util.UrlUtils;
import java.util.ArrayList;
import java.util.List;

public class TocBuilder {
    private final String baseUrl;
    private final String spaceKey;
    private final List<TocEntry> entries = new ArrayList<TocEntry>();

    public TocBuilder() {
        this(null, null);
    }

    public TocBuilder(String baseUrl, String spaceKey) {
        this.baseUrl = baseUrl;
        this.spaceKey = spaceKey;
    }

    public void addEntry(int level, String pageTitle) {
        this.entries.add(new TocEntry(level, pageTitle));
    }

    public void addEntry(int level, String pageTitle, int pageLocation) {
        this.entries.add(new TocEntry(level, pageTitle, pageLocation));
    }

    public List<TocEntry> getEntries() {
        return this.entries;
    }

    public class TocEntry {
        private final int level;
        private final String title;
        private final int pageLocation;

        TocEntry(int level, String title) {
            this(level, title, 0);
        }

        TocEntry(int level, String title, int pageLocation) {
            this.level = level;
            this.title = title;
            this.pageLocation = pageLocation;
        }

        public int getLevel() {
            return this.level;
        }

        public String getTitle() {
            return this.title;
        }

        public String getFullUrl() {
            return TocBuilder.this.baseUrl + "/display/" + TocBuilder.this.spaceKey + "/" + UrlUtils.encodeTitle(this.title);
        }

        public int getPageLocation() {
            return this.pageLocation;
        }
    }
}

