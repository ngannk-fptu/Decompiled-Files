/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.templates.PageTemplate
 */
package com.atlassian.confluence.plugins.createcontent.services;

import com.atlassian.confluence.pages.templates.PageTemplate;

public interface TemplateUpdater {
    public void updateContentTemplateRef(PageTemplate var1);

    public void revertContentTemplateRef(PageTemplate var1);
}

