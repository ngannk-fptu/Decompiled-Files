/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.startup;

import org.apache.catalina.Engine;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;

public class EngineConfig
implements LifecycleListener {
    private static final Log log = LogFactory.getLog(EngineConfig.class);
    protected Engine engine = null;
    protected static final StringManager sm = StringManager.getManager((String)"org.apache.catalina.startup");

    @Override
    public void lifecycleEvent(LifecycleEvent event) {
        try {
            this.engine = (Engine)event.getLifecycle();
        }
        catch (ClassCastException e) {
            log.error((Object)sm.getString("engineConfig.cce", new Object[]{event.getLifecycle()}), (Throwable)e);
            return;
        }
        if (event.getType().equals("start")) {
            this.start();
        } else if (event.getType().equals("stop")) {
            this.stop();
        }
    }

    protected void start() {
        if (this.engine.getLogger().isDebugEnabled()) {
            this.engine.getLogger().debug((Object)sm.getString("engineConfig.start"));
        }
    }

    protected void stop() {
        if (this.engine.getLogger().isDebugEnabled()) {
            this.engine.getLogger().debug((Object)sm.getString("engineConfig.stop"));
        }
    }
}

