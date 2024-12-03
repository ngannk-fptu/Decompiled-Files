/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.renderer.embedded;

import com.atlassian.confluence.renderer.embedded.EmbeddedObject;
import com.atlassian.confluence.renderer.embedded.EmbeddedResourceRenderer;

public interface EmbeddedResourceRendererManager {
    public EmbeddedResourceRenderer getResourceRenderer(EmbeddedObject var1);
}

