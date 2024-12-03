/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 */
package com.atlassian.confluence.extra.widgetconnector;

import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import java.util.Map;

@ExportAsService
public interface WidgetRenderer {
    public boolean matches(String var1);

    public String getEmbeddedHtml(String var1, Map<String, String> var2);

    default public String getServiceName() {
        return "Not specified";
    }
}

