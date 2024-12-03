/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.web.descriptors.WebSectionModuleDescriptor
 */
package com.atlassian.confluence.impl.plugin.web.readonly;

import com.atlassian.confluence.impl.plugin.web.readonly.ReadOnlyWebFragmentModuleDescriptor;
import com.atlassian.plugin.web.descriptors.WebSectionModuleDescriptor;

public class ReadOnlyWebSectionModuleDescriptor
extends ReadOnlyWebFragmentModuleDescriptor<Void>
implements WebSectionModuleDescriptor {
    private final WebSectionModuleDescriptor delegate;

    public ReadOnlyWebSectionModuleDescriptor(WebSectionModuleDescriptor delegate) {
        super(delegate);
        this.delegate = delegate;
    }

    public String getLocation() {
        return this.delegate.getLocation();
    }
}

