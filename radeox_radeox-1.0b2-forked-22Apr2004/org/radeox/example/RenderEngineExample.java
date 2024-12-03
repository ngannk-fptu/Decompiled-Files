/*
 * Decompiled with CFR 0.152.
 */
package org.radeox.example;

import java.util.Locale;
import org.radeox.api.engine.context.RenderContext;
import org.radeox.engine.BaseRenderEngine;
import org.radeox.engine.context.BaseInitialRenderContext;
import org.radeox.engine.context.BaseRenderContext;

public class RenderEngineExample {
    public static void main(String[] args) {
        String test = "__SnipSnap__ {link:Radeox|http://radeox.org} ==Other Bold==";
        BaseRenderContext context = new BaseRenderContext();
        BaseRenderEngine engine = new BaseRenderEngine();
        System.out.println("Rendering with default:");
        System.out.println(engine.render(test, (RenderContext)context));
        System.out.println("Rendering with alternative Wiki:");
        BaseInitialRenderContext initialContext = new BaseInitialRenderContext();
        initialContext.set("RenderContext.input_locale", new Locale("otherwiki", ""));
        BaseRenderEngine engineWithContext = new BaseRenderEngine(initialContext);
        System.out.println(engineWithContext.render(test, (RenderContext)context));
    }
}

