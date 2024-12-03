/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.macro.Macro
 *  com.atlassian.renderer.macro.RadeoxCompatibilityMacro
 *  com.atlassian.renderer.v2.macro.MacroException
 */
package com.atlassian.confluence.renderer.v2.macros;

import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.util.ConfluenceRenderUtils;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.macro.Macro;
import com.atlassian.renderer.v2.macro.MacroException;
import java.util.Map;

public class RadeoxCompatibilityMacro
extends com.atlassian.renderer.macro.RadeoxCompatibilityMacro {
    public RadeoxCompatibilityMacro(Macro macro) {
        super(macro);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String execute(Map parameters, String content, RenderContext context) throws MacroException {
        String newRawParams;
        Object rawParams;
        String attachmentsPath = context.getAttachmentsPath();
        if (attachmentsPath == null) {
            context.setAttachmentsPath(ConfluenceRenderUtils.getAttachmentsRemotePath((PageContext)context));
        }
        if ((rawParams = parameters.get(": = | RAW | = :")) == null && (newRawParams = RadeoxCompatibilityMacro.constructRadeoxRawParams(parameters)) != null) {
            parameters.put(": = | RAW | = :", newRawParams);
        }
        try {
            String string = super.execute(parameters, content, context);
            return string;
        }
        finally {
            context.setAttachmentsPath(attachmentsPath);
            if (rawParams == null) {
                parameters.remove(": = | RAW | = :");
            }
        }
    }

    public static String constructRadeoxRawParams(Map parameters) {
        if (parameters == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        parameters.forEach((key, value) -> {
            if ("".equals(key)) {
                if (sb.length() > 0) {
                    sb.insert(0, '|');
                }
                sb.insert(0, value);
            } else {
                if (sb.length() > 0) {
                    sb.append('|');
                }
                sb.append(key);
                sb.append('=');
                sb.append(value);
            }
        });
        return sb.length() == 0 ? null : sb.toString();
    }
}

