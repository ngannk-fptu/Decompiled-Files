/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.web.descriptors;

import com.atlassian.plugin.web.descriptors.WebFragmentModuleDescriptor;
import com.atlassian.plugin.web.model.WebIcon;
import com.atlassian.plugin.web.model.WebLink;

public interface WebItemModuleDescriptor
extends WebFragmentModuleDescriptor<Void> {
    public String getSection();

    public WebLink getLink();

    public WebIcon getIcon();

    public String getStyleClass();

    public String getEntryPoint();
}

