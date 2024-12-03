/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.RenderContext
 */
package com.atlassian.confluence.renderer.embedded;

import com.atlassian.confluence.renderer.embedded.EmbeddedObject;
import com.atlassian.renderer.RenderContext;

public interface EmbeddedResourceRenderer {
    public String renderResource(EmbeddedObject var1, RenderContext var2);

    public boolean matchesType(EmbeddedObject var1);
}

