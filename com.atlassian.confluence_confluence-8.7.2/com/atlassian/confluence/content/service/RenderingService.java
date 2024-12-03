/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.service;

import com.atlassian.confluence.content.render.xhtml.ConversionContextOutputType;
import com.atlassian.confluence.content.service.IdContentLocator;
import com.atlassian.confluence.content.service.RenderContentCommand;
import com.atlassian.confluence.content.service.space.SpaceLocator;

public interface RenderingService {
    public RenderContentCommand newRenderXHtmlContentCommand(IdContentLocator var1, SpaceLocator var2, String var3, String var4, ConversionContextOutputType var5);

    public RenderContentCommand newRenderWikiMarkupContentCommand(IdContentLocator var1, SpaceLocator var2, String var3, String var4, ConversionContextOutputType var5);

    public IdContentLocator getContentLocator(long var1);
}

