/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.coyote.ProtocolHandler
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.res.StringManager
 *  org.apache.tomcat.util.threads.ThreadPoolExecutor
 */
package org.apache.catalina.core;

import java.util.concurrent.Executor;
import org.apache.catalina.ContainerEvent;
import org.apache.catalina.Context;
import org.apache.catalina.Engine;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Server;
import org.apache.catalina.Service;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.FrameworkListener;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardThreadExecutor;
import org.apache.coyote.ProtocolHandler;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.util.threads.ThreadPoolExecutor;

public class ThreadLocalLeakPreventionListener
extends FrameworkListener {
    private static final Log log = LogFactory.getLog(ThreadLocalLeakPreventionListener.class);
    private volatile boolean serverStopping = false;
    protected static final StringManager sm = StringManager.getManager(ThreadLocalLeakPreventionListener.class);

    @Override
    public void lifecycleEvent(LifecycleEvent event) {
        try {
            super.lifecycleEvent(event);
            Lifecycle lifecycle = event.getLifecycle();
            if ("before_stop".equals(event.getType()) && lifecycle instanceof Server) {
                this.serverStopping = true;
            }
            if ("after_stop".equals(event.getType()) && lifecycle instanceof Context) {
                this.stopIdleThreads((Context)lifecycle);
            }
        }
        catch (Exception e) {
            String msg = sm.getString("threadLocalLeakPreventionListener.lifecycleEvent.error", new Object[]{event});
            log.error((Object)msg, (Throwable)e);
        }
    }

    @Override
    public void containerEvent(ContainerEvent event) {
        try {
            super.containerEvent(event);
        }
        catch (Exception e) {
            String msg = sm.getString("threadLocalLeakPreventionListener.containerEvent.error", new Object[]{event});
            log.error((Object)msg, (Throwable)e);
        }
    }

    private void stopIdleThreads(Context context) {
        if (this.serverStopping) {
            return;
        }
        if (!(context instanceof StandardContext) || !((StandardContext)context).getRenewThreadsWhenStoppingContext()) {
            log.debug((Object)"Not renewing threads when the context is stopping. It is not configured to do it.");
            return;
        }
        Engine engine = (Engine)context.getParent().getParent();
        Service service = engine.getService();
        Connector[] connectors = service.findConnectors();
        if (connectors != null) {
            for (Connector connector : connectors) {
                ProtocolHandler handler = connector.getProtocolHandler();
                Executor executor = null;
                if (handler != null) {
                    executor = handler.getExecutor();
                }
                if (executor instanceof ThreadPoolExecutor) {
                    ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor)executor;
                    threadPoolExecutor.contextStopping();
                    continue;
                }
                if (!(executor instanceof StandardThreadExecutor)) continue;
                StandardThreadExecutor stdThreadExecutor = (StandardThreadExecutor)executor;
                stdThreadExecutor.contextStopping();
            }
        }
    }

    @Override
    protected LifecycleListener createLifecycleListener(Context context) {
        return this;
    }
}

