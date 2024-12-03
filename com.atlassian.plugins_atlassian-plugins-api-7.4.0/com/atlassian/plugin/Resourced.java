/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin;

import com.atlassian.plugin.elements.ResourceDescriptor;
import com.atlassian.plugin.elements.ResourceLocation;
import java.util.List;

public interface Resourced {
    public List<ResourceDescriptor> getResourceDescriptors();

    public ResourceDescriptor getResourceDescriptor(String var1, String var2);

    public ResourceLocation getResourceLocation(String var1, String var2);
}

