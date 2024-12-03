/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.embedded;

import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.attachments.RendererAttachmentManager;
import com.atlassian.renderer.embedded.EmbeddedObjectRenderer;
import com.atlassian.renderer.embedded.EmbeddedResource;
import com.atlassian.renderer.embedded.EmbeddedResourceRenderer;
import java.util.Map;

public class EmbeddedFlashRenderer
extends EmbeddedObjectRenderer
implements EmbeddedResourceRenderer {
    public static final String DISPLAY_INLINE_PARAMETER_NAME = "displayInline";

    public EmbeddedFlashRenderer(RendererAttachmentManager attachmentManager) {
        super(attachmentManager);
    }

    @Override
    public String renderResource(EmbeddedResource resource, RenderContext context) {
        Map contextMap = this.setupObjectProperties(resource, context);
        String source = (String)contextMap.get("src");
        if (source != null) {
            contextMap.put("src", source + "?" + DISPLAY_INLINE_PARAMETER_NAME + "=true");
        }
        if (contextMap.get("data") != null) {
            contextMap.put("data", contextMap.get("data") + "?" + DISPLAY_INLINE_PARAMETER_NAME + "=true");
        }
        if (contextMap.get("object") != null) {
            contextMap.put("object", contextMap.get("object") + "?" + DISPLAY_INLINE_PARAMETER_NAME + "=true");
        }
        if (contextMap.get("movie") == null) {
            contextMap.put("movie", contextMap.get("object"));
        } else {
            contextMap.put("movie", contextMap.get("movie") + "?" + DISPLAY_INLINE_PARAMETER_NAME + "=true");
        }
        contextMap.remove("loop");
        String renderedObjectHtml = this.renderEmbeddedObject(contextMap);
        return this.renderEmbeddedObjectWrapper(renderedObjectHtml, contextMap);
    }
}

