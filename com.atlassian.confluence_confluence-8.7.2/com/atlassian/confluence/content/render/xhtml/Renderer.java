/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml;

import com.atlassian.confluence.content.render.xhtml.BatchedRenderRequest;
import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.view.BatchedRenderResult;
import com.atlassian.confluence.content.render.xhtml.view.RenderResult;
import com.atlassian.confluence.core.ContentEntityObject;
import java.util.List;

public interface Renderer {
    public String render(ContentEntityObject var1);

    public String render(ContentEntityObject var1, ConversionContext var2);

    public String render(String var1, ConversionContext var2);

    public RenderResult renderWithResult(String var1, ConversionContext var2);

    public List<BatchedRenderResult> render(BatchedRenderRequest ... var1);
}

