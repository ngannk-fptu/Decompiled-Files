/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 */
package com.atlassian.confluence.extra.webdav;

import com.atlassian.plugin.ModuleDescriptor;
import org.apache.jackrabbit.webdav.DavResourceFactory;

public interface DavResourceFactoryModuleDescriptor
extends ModuleDescriptor<DavResourceFactory> {
    public DavResourceFactory getModule();

    public String getWorkspaceName();
}

