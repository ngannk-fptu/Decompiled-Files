/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.module.sitemesh.Decorator
 */
package com.atlassian.confluence.themes;

import com.atlassian.confluence.plugin.descriptor.LayoutModuleDescriptor;
import com.opensymphony.module.sitemesh.Decorator;

public interface ThemedDecorator {
    public String getName();

    public Decorator getDecorator(Decorator var1);

    public void init(LayoutModuleDescriptor var1);

    public String getResourceKey();
}

