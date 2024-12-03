/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.webresources;

import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.webresources.EmptyResource;

public class VirtualResource
extends EmptyResource {
    private final String name;

    public VirtualResource(WebResourceRoot root, String webAppPath, String name) {
        super(root, webAppPath);
        this.name = name;
    }

    @Override
    public boolean isVirtual() {
        return true;
    }

    @Override
    public boolean isDirectory() {
        return true;
    }

    @Override
    public String getName() {
        return this.name;
    }
}

