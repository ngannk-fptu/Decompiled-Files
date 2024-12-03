/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.macro.ImagePlaceholder
 */
package com.atlassian.confluence.extra.widgetconnector.services;

import com.atlassian.confluence.macro.ImagePlaceholder;
import java.util.Map;

public interface PlaceholderService {
    public ImagePlaceholder generatePlaceholder(String var1, Map<String, String> var2);
}

