/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.widgetconnector.services;

import java.util.Map;

public interface VelocityRenderService {
    public static final String WIDTH_PARAM = "width";
    public static final String HEIGHT_PARAM = "height";
    public static final String TEMPLATE_PARAM = "_template";

    public String render(String var1, Map<String, String> var2);
}

