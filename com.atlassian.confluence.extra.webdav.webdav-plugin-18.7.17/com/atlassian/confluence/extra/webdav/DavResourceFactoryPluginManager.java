/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.webdav;

import org.apache.jackrabbit.webdav.DavResourceFactory;

public interface DavResourceFactoryPluginManager {
    public DavResourceFactory getFactoryForWorkspace(String var1);
}

