/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav;

import org.apache.jackrabbit.webdav.DavLocatorFactory;

public interface DavResourceLocator {
    public String getPrefix();

    public String getResourcePath();

    public String getWorkspacePath();

    public String getWorkspaceName();

    public boolean isSameWorkspace(DavResourceLocator var1);

    public boolean isSameWorkspace(String var1);

    public String getHref(boolean var1);

    public boolean isRootLocation();

    public DavLocatorFactory getFactory();

    public String getRepositoryPath();
}

