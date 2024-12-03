/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.util.TextUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.renderer.v2.macro.basic;

import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.RenderedContentStore;
import com.atlassian.renderer.util.RendererUtil;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.BaseMacro;
import com.atlassian.renderer.v2.macro.MacroException;
import com.opensymphony.util.TextUtils;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BasicAnchorMacro
extends BaseMacro {
    private static final Logger log = LoggerFactory.getLogger(BasicAnchorMacro.class);

    @Override
    public boolean isInline() {
        return true;
    }

    @Override
    public boolean hasBody() {
        return false;
    }

    @Override
    public RenderMode getBodyRenderMode() {
        return RenderMode.INLINE;
    }

    @Override
    public String execute(Map parameters, String body, RenderContext renderContext) throws MacroException {
        if (!TextUtils.stringSet((String)((String)parameters.get("0")))) {
            return "";
        }
        return "<a name=\"" + BasicAnchorMacro.getAnchor(renderContext, (String)parameters.get("0")) + "\">" + body + "</a>";
    }

    public static String getAnchor(RenderContext context, String body) {
        String result = "";
        result = result + RendererUtil.summarise(TextUtils.noNull((String)RenderedContentStore.stripTokens(body)).trim());
        result = result.replaceAll(" ", "");
        try {
            result = URLEncoder.encode(result, context.getCharacterEncoding());
        }
        catch (UnsupportedEncodingException e) {
            log.warn("Unable to escape anchor value because of an unsupported character encoding of: " + context.getCharacterEncoding());
        }
        return result;
    }
}

