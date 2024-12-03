/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.sitemesh;

import com.opensymphony.sitemesh.Content;
import com.opensymphony.sitemesh.Decorator;
import com.opensymphony.sitemesh.SiteMeshContext;

public interface DecoratorSelector {
    public Decorator selectDecorator(Content var1, SiteMeshContext var2);
}

