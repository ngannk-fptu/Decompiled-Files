/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.web.model.WebIcon
 *  com.atlassian.plugin.web.model.WebLink
 */
package com.atlassian.confluence.impl.plugin.web.readonly;

import com.atlassian.confluence.impl.plugin.web.readonly.ReadOnlyWebLink;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.plugin.web.model.WebIcon;
import com.atlassian.plugin.web.model.WebLink;

public class ReadOnlyWebIcon
implements WebIcon {
    private final WebIcon delegate;

    public ReadOnlyWebIcon(WebIcon delegate) {
        this.delegate = delegate;
    }

    public WebLink getUrl() {
        return GeneralUtil.applyIfNonNull(this.delegate.getUrl(), ReadOnlyWebLink::new);
    }

    public int getWidth() {
        return this.delegate.getWidth();
    }

    public int getHeight() {
        return this.delegate.getHeight();
    }
}

