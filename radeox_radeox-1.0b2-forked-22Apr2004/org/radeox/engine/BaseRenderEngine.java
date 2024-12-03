/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.radeox.engine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Iterator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.radeox.api.engine.RenderEngine;
import org.radeox.api.engine.context.InitialRenderContext;
import org.radeox.api.engine.context.RenderContext;
import org.radeox.engine.context.BaseInitialRenderContext;
import org.radeox.filter.Filter;
import org.radeox.filter.FilterPipe;
import org.radeox.filter.context.BaseFilterContext;
import org.radeox.util.Service;

public class BaseRenderEngine
implements RenderEngine {
    private static Log log = LogFactory.getLog((Class)(class$org$radeox$engine$BaseRenderEngine == null ? (class$org$radeox$engine$BaseRenderEngine = BaseRenderEngine.class$("org.radeox.engine.BaseRenderEngine")) : class$org$radeox$engine$BaseRenderEngine));
    protected InitialRenderContext initialContext;
    protected FilterPipe fp;
    static /* synthetic */ Class class$org$radeox$engine$BaseRenderEngine;
    static /* synthetic */ Class class$org$radeox$filter$Filter;

    public BaseRenderEngine(InitialRenderContext context) {
        this.initialContext = context;
    }

    public BaseRenderEngine() {
        this(new BaseInitialRenderContext());
    }

    protected void init() {
        if (null == this.fp) {
            this.fp = new FilterPipe(this.initialContext);
            Iterator iterator = Service.providers(class$org$radeox$filter$Filter == null ? (class$org$radeox$filter$Filter = BaseRenderEngine.class$("org.radeox.filter.Filter")) : class$org$radeox$filter$Filter);
            while (iterator.hasNext()) {
                try {
                    Filter filter = (Filter)iterator.next();
                    this.fp.addFilter(filter);
                    log.debug((Object)("Loaded filter: " + filter.getClass().getName()));
                }
                catch (Exception e) {
                    log.warn((Object)"BaseRenderEngine: unable to load filter", (Throwable)e);
                }
            }
            this.fp.init();
        }
    }

    public String getName() {
        return "radeox";
    }

    public String render(String content, RenderContext context) {
        this.init();
        BaseFilterContext filterContext = new BaseFilterContext();
        filterContext.setRenderContext(context);
        return this.fp.filter(content, filterContext);
    }

    public String render(Reader in, RenderContext context) throws IOException {
        String line;
        StringBuffer buffer = new StringBuffer();
        BufferedReader inputReader = new BufferedReader(in);
        while ((line = inputReader.readLine()) != null) {
            buffer.append(line);
        }
        return this.render(buffer.toString(), context);
    }

    public void render(Writer out, String content, RenderContext context) throws IOException {
        out.write(this.render(content, context));
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

