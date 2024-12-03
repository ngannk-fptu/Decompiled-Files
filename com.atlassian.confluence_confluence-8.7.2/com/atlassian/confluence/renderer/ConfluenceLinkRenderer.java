/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.links.Link
 *  com.atlassian.renderer.v2.V2LinkRenderer
 */
package com.atlassian.confluence.renderer;

import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.confluence.util.i18n.UserLocaleAware;
import com.atlassian.renderer.links.Link;
import com.atlassian.renderer.v2.V2LinkRenderer;

public class ConfluenceLinkRenderer
extends V2LinkRenderer
implements UserLocaleAware {
    private I18NBeanFactory i18NBeanFactory;

    public String getLinkTitle(Link link) {
        if (link.getTitleKey() != null) {
            return this.i18NBeanFactory.getI18NBean().getText(link.getTitleKey(), link.getTitleArgs());
        }
        return link.getTitle();
    }

    @Override
    public void setI18NBeanFactory(I18NBeanFactory i18NBeanFactory) {
        this.i18NBeanFactory = i18NBeanFactory;
    }
}

