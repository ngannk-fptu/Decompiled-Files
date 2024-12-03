/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.pages.ancestors;

import java.util.List;

class PageWithAncestors {
    private final Long pageId;
    private final List<Long> ancestors;

    PageWithAncestors(Long pageId, List<Long> ancestors) {
        this.pageId = pageId;
        this.ancestors = ancestors;
    }

    public Long getPageId() {
        return this.pageId;
    }

    public List<Long> getAncestors() {
        return this.ancestors;
    }
}

