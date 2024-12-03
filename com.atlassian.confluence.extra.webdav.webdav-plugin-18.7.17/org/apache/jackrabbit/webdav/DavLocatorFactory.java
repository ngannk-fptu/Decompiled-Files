/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav;

import org.apache.jackrabbit.webdav.DavResourceLocator;

public interface DavLocatorFactory {
    public DavResourceLocator createResourceLocator(String var1, String var2);

    public DavResourceLocator createResourceLocator(String var1, String var2, String var3);

    public DavResourceLocator createResourceLocator(String var1, String var2, String var3, boolean var4);
}

