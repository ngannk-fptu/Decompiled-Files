/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.status.service.systeminfo;

class ContentUsageInfo {
    private final int allContent;
    private final int currentContent;

    public ContentUsageInfo(int allContent, int currentContent) {
        this.allContent = allContent;
        this.currentContent = currentContent;
    }

    public int getAllContent() {
        return this.allContent;
    }

    public int getCurrentContent() {
        return this.currentContent;
    }
}

