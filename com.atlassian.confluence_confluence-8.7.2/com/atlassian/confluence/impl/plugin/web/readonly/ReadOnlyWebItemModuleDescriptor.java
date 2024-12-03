/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.web.descriptors.WebItemModuleDescriptor
 *  com.atlassian.plugin.web.model.WebIcon
 *  com.atlassian.plugin.web.model.WebLink
 */
package com.atlassian.confluence.impl.plugin.web.readonly;

import com.atlassian.confluence.impl.plugin.web.readonly.ReadOnlyWebFragmentModuleDescriptor;
import com.atlassian.confluence.impl.plugin.web.readonly.ReadOnlyWebIcon;
import com.atlassian.confluence.impl.plugin.web.readonly.ReadOnlyWebLink;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.plugin.web.descriptors.WebItemModuleDescriptor;
import com.atlassian.plugin.web.model.WebIcon;
import com.atlassian.plugin.web.model.WebLink;

public class ReadOnlyWebItemModuleDescriptor
extends ReadOnlyWebFragmentModuleDescriptor<Void>
implements WebItemModuleDescriptor {
    private final WebItemModuleDescriptor delegate;

    public ReadOnlyWebItemModuleDescriptor(WebItemModuleDescriptor delegate) {
        super(delegate);
        this.delegate = delegate;
    }

    public String getSection() {
        return this.delegate.getSection();
    }

    public WebLink getLink() {
        return GeneralUtil.applyIfNonNull(this.delegate.getLink(), ReadOnlyWebLink::new);
    }

    public WebIcon getIcon() {
        return GeneralUtil.applyIfNonNull(this.delegate.getIcon(), ReadOnlyWebIcon::new);
    }

    public String getStyleClass() {
        return this.delegate.getStyleClass();
    }

    public String getEntryPoint() {
        return this.delegate.getEntryPoint();
    }
}

