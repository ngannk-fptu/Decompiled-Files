/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.web.descriptors;

import com.atlassian.plugin.web.descriptors.WebFragmentModuleDescriptor;
import com.atlassian.plugin.web.descriptors.WeightedDescriptor;
import com.atlassian.plugin.web.model.WebPanel;

public interface WebPanelModuleDescriptor
extends WebFragmentModuleDescriptor<WebPanel>,
WeightedDescriptor {
    public String getLocation();
}

