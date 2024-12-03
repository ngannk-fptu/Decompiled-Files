/*
 * Decompiled with CFR 0.152.
 */
package org.radeox.example;

import org.radeox.EngineManager;
import org.radeox.api.engine.context.RenderContext;
import org.radeox.engine.context.BaseRenderContext;

public class XSLTExtension {
    public static String render(String arg) {
        if (arg == null) {
            arg = "";
        }
        BaseRenderContext context = new BaseRenderContext();
        return EngineManager.getInstance().render(arg, (RenderContext)context);
    }
}

