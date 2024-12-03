/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.core;

import java.util.concurrent.ConcurrentHashMap;
import org.apache.catalina.Container;
import org.apache.catalina.ContainerEvent;
import org.apache.catalina.ContainerListener;
import org.apache.catalina.Context;
import org.apache.catalina.Engine;
import org.apache.catalina.Host;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Server;
import org.apache.catalina.Service;

public abstract class FrameworkListener
implements LifecycleListener,
ContainerListener {
    protected final ConcurrentHashMap<Context, LifecycleListener> contextListeners = new ConcurrentHashMap();

    protected abstract LifecycleListener createLifecycleListener(Context var1);

    @Override
    public void lifecycleEvent(LifecycleEvent event) {
        Lifecycle lifecycle = event.getLifecycle();
        if ("before_start".equals(event.getType()) && lifecycle instanceof Server) {
            Server server = (Server)lifecycle;
            this.registerListenersForServer(server);
        }
    }

    @Override
    public void containerEvent(ContainerEvent event) {
        String type = event.getType();
        if ("addChild".equals(type)) {
            this.processContainerAddChild((Container)event.getData());
        } else if ("removeChild".equals(type)) {
            this.processContainerRemoveChild((Container)event.getData());
        }
    }

    protected void registerListenersForServer(Server server) {
        for (Service service : server.findServices()) {
            Engine engine = service.getContainer();
            if (engine == null) continue;
            engine.addContainerListener(this);
            this.registerListenersForEngine(engine);
        }
    }

    protected void registerListenersForEngine(Engine engine) {
        for (Container hostContainer : engine.findChildren()) {
            Host host = (Host)hostContainer;
            host.addContainerListener(this);
            this.registerListenersForHost(host);
        }
    }

    protected void registerListenersForHost(Host host) {
        for (Container contextContainer : host.findChildren()) {
            Context context = (Context)contextContainer;
            this.registerContextListener(context);
        }
    }

    protected void registerContextListener(Context context) {
        LifecycleListener listener = this.createLifecycleListener(context);
        this.contextListeners.put(context, listener);
        context.addLifecycleListener(listener);
    }

    protected void processContainerAddChild(Container child) {
        if (child instanceof Context) {
            this.registerContextListener((Context)child);
        } else if (child instanceof Engine) {
            this.registerListenersForEngine((Engine)child);
        } else if (child instanceof Host) {
            this.registerListenersForHost((Host)child);
        }
    }

    protected void processContainerRemoveChild(Container child) {
        if (child instanceof Context) {
            LifecycleListener listener = this.contextListeners.remove(child);
            if (listener != null) {
                child.removeLifecycleListener(listener);
            }
        } else if (child instanceof Host || child instanceof Engine) {
            child.removeContainerListener(this);
        }
    }
}

