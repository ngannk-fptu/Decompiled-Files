/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.GeneralUtil
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.v2.RenderMode
 *  com.atlassian.renderer.v2.macro.BaseMacro
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.macros.basic;

import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.BaseMacro;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class NoLinkMacro
extends BaseMacro {
    public boolean isInline() {
        return true;
    }

    public boolean hasBody() {
        return false;
    }

    public RenderMode getBodyRenderMode() {
        return RenderMode.NO_RENDER;
    }

    public String execute(Map parameters, String body, RenderContext renderContext) {
        String linkParam = (String)parameters.get(": = | RAW | = :");
        return StringUtils.isNotEmpty((CharSequence)linkParam) ? GeneralUtil.escapeXml((String)linkParam) : "";
    }
}

