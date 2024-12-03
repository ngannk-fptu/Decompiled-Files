/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.embedded;

import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.attachments.RendererAttachmentManager;
import com.atlassian.renderer.embedded.EmbeddedAudio;
import com.atlassian.renderer.embedded.EmbeddedFlash;
import com.atlassian.renderer.embedded.EmbeddedFlashRenderer;
import com.atlassian.renderer.embedded.EmbeddedImage;
import com.atlassian.renderer.embedded.EmbeddedImageRenderer;
import com.atlassian.renderer.embedded.EmbeddedObject;
import com.atlassian.renderer.embedded.EmbeddedObjectRenderer;
import com.atlassian.renderer.embedded.EmbeddedQuicktime;
import com.atlassian.renderer.embedded.EmbeddedRealMedia;
import com.atlassian.renderer.embedded.EmbeddedRealMediaRenderer;
import com.atlassian.renderer.embedded.EmbeddedResource;
import com.atlassian.renderer.embedded.EmbeddedResourceRenderer;
import com.atlassian.renderer.embedded.EmbeddedWindowsMedia;
import com.atlassian.renderer.embedded.PlaceholderImageRenderer;
import com.atlassian.renderer.embedded.UnembeddableObject;
import com.atlassian.renderer.embedded.UnembeddableObjectRenderer;
import com.atlassian.renderer.v2.RenderUtils;
import java.util.HashMap;

public class DefaultEmbeddedResourceRenderer
implements EmbeddedResourceRenderer {
    protected HashMap renderMap;
    protected RendererAttachmentManager attachmentManager;

    public DefaultEmbeddedResourceRenderer() {
    }

    public DefaultEmbeddedResourceRenderer(RendererAttachmentManager attachmentManager) {
        this.attachmentManager = attachmentManager;
    }

    @Override
    public String renderResource(EmbeddedResource resource, RenderContext context) {
        try {
            if (resource.isInternal() && this.attachmentManager.getAttachment(context, resource) == null) {
                if (context.isRenderingForWysiwyg()) {
                    return new PlaceholderImageRenderer().renderResource(resource, context);
                }
                throw new IllegalArgumentException("Unable to render embedded object: File (" + resource.getFilename() + ") not found.");
            }
            if (!this.getRenderMap().containsKey(resource.getClass())) {
                if (context.isRenderingForWysiwyg()) {
                    return new PlaceholderImageRenderer().renderResource(resource, context);
                }
                throw new IllegalArgumentException("Unsupported embedded resource type: " + resource.getType());
            }
            EmbeddedResourceRenderer delegate = (EmbeddedResourceRenderer)this.getRenderMap().get(resource.getClass());
            if (context.isRenderingForWysiwyg() && !EmbeddedImage.class.equals(resource.getClass())) {
                delegate = new PlaceholderImageRenderer();
            }
            return delegate.renderResource(resource, context);
        }
        catch (Exception e) {
            return RenderUtils.error(e.getMessage());
        }
    }

    public RendererAttachmentManager getAttachmentManager() {
        return this.attachmentManager;
    }

    public void setAttachmentManager(RendererAttachmentManager attachmentManager) {
        this.attachmentManager = attachmentManager;
    }

    protected HashMap getRenderMap() {
        if (this.renderMap == null) {
            this.renderMap = new HashMap();
            EmbeddedObjectRenderer embeddedObjectRenderer = new EmbeddedObjectRenderer(this.attachmentManager);
            this.renderMap.put(EmbeddedFlash.class, new EmbeddedFlashRenderer(this.attachmentManager));
            this.renderMap.put(EmbeddedImage.class, new EmbeddedImageRenderer(this.attachmentManager));
            this.renderMap.put(EmbeddedRealMedia.class, new EmbeddedRealMediaRenderer(this.attachmentManager));
            this.renderMap.put(EmbeddedObject.class, embeddedObjectRenderer);
            this.renderMap.put(EmbeddedQuicktime.class, embeddedObjectRenderer);
            this.renderMap.put(EmbeddedWindowsMedia.class, embeddedObjectRenderer);
            this.renderMap.put(EmbeddedAudio.class, embeddedObjectRenderer);
            this.renderMap.put(UnembeddableObject.class, new UnembeddableObjectRenderer());
        }
        return this.renderMap;
    }
}

