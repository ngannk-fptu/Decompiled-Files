/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.macro;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.ImagePlaceholder;
import java.util.Map;

public interface EditorImagePlaceholder {
    public ImagePlaceholder getImagePlaceholder(Map<String, String> var1, ConversionContext var2);
}

