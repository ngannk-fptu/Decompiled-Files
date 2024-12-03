/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.picocontainer.defaults.DefaultPicoContainer
 */
package org.radeox.example;

import java.util.Locale;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.radeox.api.engine.RenderEngine;
import org.radeox.api.engine.context.RenderContext;
import org.radeox.engine.context.BaseInitialRenderContext;
import org.radeox.engine.context.BaseRenderContext;

public class PicoExample {
    static /* synthetic */ Class class$org$radeox$api$engine$context$InitialRenderContext;
    static /* synthetic */ Class class$org$radeox$api$engine$RenderEngine;
    static /* synthetic */ Class class$org$radeox$engine$BaseRenderEngine;

    public static void main(String[] args) {
        String test = "==SnipSnap== {link:Radeox|http://radeox.org}";
        DefaultPicoContainer c = new DefaultPicoContainer();
        try {
            BaseInitialRenderContext initialContext = new BaseInitialRenderContext();
            initialContext.set("RenderContext.input_locale", new Locale("otherwiki", ""));
            c.registerComponentInstance((Object)(class$org$radeox$api$engine$context$InitialRenderContext == null ? (class$org$radeox$api$engine$context$InitialRenderContext = PicoExample.class$("org.radeox.api.engine.context.InitialRenderContext")) : class$org$radeox$api$engine$context$InitialRenderContext), (Object)initialContext);
            c.registerComponentImplementation((Object)(class$org$radeox$api$engine$RenderEngine == null ? (class$org$radeox$api$engine$RenderEngine = PicoExample.class$("org.radeox.api.engine.RenderEngine")) : class$org$radeox$api$engine$RenderEngine), class$org$radeox$engine$BaseRenderEngine == null ? (class$org$radeox$engine$BaseRenderEngine = PicoExample.class$("org.radeox.engine.BaseRenderEngine")) : class$org$radeox$engine$BaseRenderEngine);
            c.getComponentInstances();
        }
        catch (Exception e) {
            System.err.println("Could not register component: " + e);
        }
        DefaultPicoContainer container = c;
        RenderEngine engine = (RenderEngine)container.getComponentInstance((Object)(class$org$radeox$api$engine$RenderEngine == null ? (class$org$radeox$api$engine$RenderEngine = PicoExample.class$("org.radeox.api.engine.RenderEngine")) : class$org$radeox$api$engine$RenderEngine));
        BaseRenderContext context = new BaseRenderContext();
        System.out.println(engine.render(test, (RenderContext)context));
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

