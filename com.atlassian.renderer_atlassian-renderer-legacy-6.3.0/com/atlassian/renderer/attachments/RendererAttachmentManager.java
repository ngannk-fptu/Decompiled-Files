/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.attachments;

import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.attachments.RendererAttachment;
import com.atlassian.renderer.embedded.EmbeddedImage;
import com.atlassian.renderer.embedded.EmbeddedResource;

public interface RendererAttachmentManager {
    public RendererAttachment getAttachment(RenderContext var1, EmbeddedResource var2);

    public RendererAttachment getThumbnail(RendererAttachment var1, RenderContext var2, EmbeddedImage var3);

    public boolean systemSupportsThumbnailing();
}

