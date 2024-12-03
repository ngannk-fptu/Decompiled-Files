/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.radeox;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.radeox.api.engine.RenderEngine;
import org.radeox.engine.BaseRenderEngine;
import org.radeox.util.Service;

public class EngineManager {
    private static Log log = LogFactory.getLog((Class)(class$org$radeox$EngineManager == null ? (class$org$radeox$EngineManager = EngineManager.class$("org.radeox.EngineManager")) : class$org$radeox$EngineManager));
    public static final String DEFAULT = "radeox";
    private static Map availableEngines = new HashMap();
    static /* synthetic */ Class class$org$radeox$EngineManager;
    static /* synthetic */ Class class$org$radeox$api$engine$RenderEngine;

    public static synchronized void registerEngine(RenderEngine engine) {
        if (null == availableEngines) {
            availableEngines = new HashMap();
        }
        availableEngines.put(engine.getName(), engine);
    }

    public static synchronized RenderEngine getInstance(String name) {
        if (null == availableEngines) {
            availableEngines = new HashMap();
        }
        return (RenderEngine)availableEngines.get(name);
    }

    public static synchronized RenderEngine getInstance() {
        if (null == availableEngines) {
            availableEngines = new HashMap();
        }
        if (!availableEngines.containsKey(DEFAULT)) {
            BaseRenderEngine engine = new BaseRenderEngine();
            availableEngines.put(engine.getName(), engine);
        }
        return (RenderEngine)availableEngines.get(DEFAULT);
    }

    public static String getVersion() {
        return "0.5.1";
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    static {
        Iterator iterator = Service.providers(class$org$radeox$api$engine$RenderEngine == null ? (class$org$radeox$api$engine$RenderEngine = EngineManager.class$("org.radeox.api.engine.RenderEngine")) : class$org$radeox$api$engine$RenderEngine);
        while (iterator.hasNext()) {
            try {
                RenderEngine engine = (RenderEngine)iterator.next();
                EngineManager.registerEngine(engine);
                log.debug((Object)("Loaded RenderEngine: " + engine.getClass().getName()));
            }
            catch (Exception e) {
                log.warn((Object)"EngineManager: unable to load RenderEngine", (Throwable)e);
            }
        }
    }
}

