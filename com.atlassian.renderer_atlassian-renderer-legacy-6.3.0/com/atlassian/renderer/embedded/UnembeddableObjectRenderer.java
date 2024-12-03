/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.embedded;

import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.embedded.EmbeddedResource;
import com.atlassian.renderer.embedded.EmbeddedResourceRenderer;
import com.atlassian.renderer.v2.RenderUtils;

public class UnembeddableObjectRenderer
implements EmbeddedResourceRenderer {
    @Override
    public String renderResource(EmbeddedResource resource, RenderContext context) {
        return RenderUtils.error("Unable to embed resource: " + resource.getFilename() + " of type " + resource.getType());
    }
}

