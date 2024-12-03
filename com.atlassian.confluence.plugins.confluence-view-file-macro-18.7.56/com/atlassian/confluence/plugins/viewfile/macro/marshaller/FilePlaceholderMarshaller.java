/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.Streamable
 *  com.atlassian.confluence.macro.ImagePlaceholder
 *  com.atlassian.confluence.pages.Attachment
 */
package com.atlassian.confluence.plugins.viewfile.macro.marshaller;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.macro.ImagePlaceholder;
import com.atlassian.confluence.pages.Attachment;
import java.util.Map;

public interface FilePlaceholderMarshaller {
    public ImagePlaceholder getImagePlaceholder(Attachment var1, Map<String, String> var2);

    public boolean handles(Attachment var1);

    public Streamable getRenderedContentStreamable(Attachment var1, Map<String, String> var2, ConversionContext var3);
}

