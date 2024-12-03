/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.module.sitemesh.taglib.decorator;

import com.opensymphony.module.sitemesh.taglib.AbstractTag;

public class TitleTag
extends AbstractTag {
    private String defaultTitle = null;

    public void setDefault(String defaultTitle) {
        this.defaultTitle = defaultTitle;
    }

    public final int doEndTag() {
        try {
            String title = this.getPage().getTitle();
            if (title == null || title.trim().length() == 0) {
                title = this.defaultTitle;
            }
            if (title != null) {
                this.getOut().write(title);
            }
        }
        catch (Exception e) {
            TitleTag.trace(e);
        }
        return 6;
    }
}

