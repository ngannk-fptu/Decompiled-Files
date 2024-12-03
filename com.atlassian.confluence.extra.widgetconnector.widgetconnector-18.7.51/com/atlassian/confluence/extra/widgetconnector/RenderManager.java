/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.macro.ImagePlaceholder
 */
package com.atlassian.confluence.extra.widgetconnector;

import com.atlassian.confluence.macro.ImagePlaceholder;
import java.util.Map;

public interface RenderManager {
    public String getEmbeddedHtml(String var1, Map<String, String> var2);

    public ImagePlaceholder getImagePlaceholder(String var1, Map<String, String> var2);
}

