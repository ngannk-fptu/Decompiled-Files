/*
 * Decompiled with CFR 0.152.
 */
package org.radeox.macro;

import java.io.IOException;
import java.io.Writer;
import org.radeox.api.engine.ImageRenderEngine;
import org.radeox.api.engine.RenderEngine;
import org.radeox.api.engine.context.RenderContext;
import org.radeox.macro.BaseLocaleMacro;
import org.radeox.macro.parameter.MacroParameter;
import org.radeox.util.Encoder;

public class LinkMacro
extends BaseLocaleMacro {
    public String getLocaleKey() {
        return "macro.link";
    }

    public void execute(Writer writer, MacroParameter params) throws IllegalArgumentException, IOException {
        RenderContext context = params.getContext();
        RenderEngine engine = context.getRenderEngine();
        String text = params.get("text", 0);
        String url = params.get("url", 1);
        String img = params.get("img", 2);
        if (params.getLength() == 1) {
            url = text;
            text = Encoder.toEntity(text.charAt(0)) + Encoder.escape(text.substring(1));
        }
        if (url != null && text != null) {
            writer.write("<span class=\"nobr\">");
            if (!"none".equals(img) && engine instanceof ImageRenderEngine) {
                writer.write(((ImageRenderEngine)((Object)engine)).getExternalImageLink(null));
            }
        } else {
            throw new IllegalArgumentException("link needs a name and a url as argument");
        }
        writer.write("<a href=\"");
        writer.write(url);
        writer.write("\">");
        writer.write(text);
        writer.write("</a></span>");
    }
}

