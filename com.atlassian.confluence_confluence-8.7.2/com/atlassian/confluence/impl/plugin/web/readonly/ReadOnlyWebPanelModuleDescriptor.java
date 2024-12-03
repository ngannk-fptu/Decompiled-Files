/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.web.descriptors.WebPanelModuleDescriptor
 *  com.atlassian.plugin.web.model.WebPanel
 */
package com.atlassian.confluence.impl.plugin.web.readonly;

import com.atlassian.confluence.impl.plugin.web.readonly.ReadOnlyWebFragmentModuleDescriptor;
import com.atlassian.plugin.web.descriptors.WebPanelModuleDescriptor;
import com.atlassian.plugin.web.model.WebPanel;

public class ReadOnlyWebPanelModuleDescriptor
extends ReadOnlyWebFragmentModuleDescriptor<WebPanel>
implements WebPanelModuleDescriptor {
    private final WebPanelModuleDescriptor delegate;

    public ReadOnlyWebPanelModuleDescriptor(WebPanelModuleDescriptor delegate) {
        super(delegate);
        this.delegate = delegate;
    }

    public String getLocation() {
        return this.delegate.getLocation();
    }
}

