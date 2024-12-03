/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.elements.ResourceLocation
 */
package com.atlassian.confluence.plugin.dev;

import com.atlassian.plugin.elements.ResourceLocation;
import java.util.HashMap;
import java.util.Map;

public abstract class ResourceLocationDelegate
extends ResourceLocation {
    private ResourceLocation delegate;

    public ResourceLocationDelegate(ResourceLocation delegate) {
        super(null, null, null, null, null, new HashMap());
        this.delegate = delegate;
    }

    public String getLocation() {
        return this.delegate.getLocation();
    }

    public String getName() {
        return this.delegate.getName();
    }

    public String getType() {
        return this.delegate.getType();
    }

    public String getContentType() {
        return this.delegate.getContentType();
    }

    public String getContent() {
        return this.delegate.getContent();
    }

    public String getParameter(String key) {
        return this.delegate.getParameter(key);
    }

    public Map<String, String> getParams() {
        return this.delegate.getParams();
    }

    public String toString() {
        return this.delegate.toString();
    }
}

