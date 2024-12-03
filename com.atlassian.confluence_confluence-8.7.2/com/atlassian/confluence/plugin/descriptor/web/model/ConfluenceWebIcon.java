/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.web.model.WebIcon
 */
package com.atlassian.confluence.plugin.descriptor.web.model;

import com.atlassian.confluence.plugin.descriptor.web.model.ConfluenceWebLink;
import com.atlassian.plugin.web.model.WebIcon;

public class ConfluenceWebIcon
implements WebIcon {
    WebIcon webIcon;

    public ConfluenceWebIcon(WebIcon webIcon) {
        this.webIcon = webIcon;
    }

    public ConfluenceWebLink getUrl() {
        return new ConfluenceWebLink(this.webIcon.getUrl());
    }

    public int getWidth() {
        return this.webIcon.getWidth();
    }

    public int getHeight() {
        return this.webIcon.getHeight();
    }
}

