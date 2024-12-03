/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.mapper;

import java.util.ArrayList;
import java.util.List;
import org.apache.catalina.Container;
import org.apache.catalina.ContainerEvent;
import org.apache.catalina.ContainerListener;
import org.apache.catalina.Context;
import org.apache.catalina.Engine;
import org.apache.catalina.Host;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.Service;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.Wrapper;
import org.apache.catalina.mapper.Mapper;
import org.apache.catalina.mapper.WrapperMappingInfo;
import org.apache.catalina.util.LifecycleMBeanBase;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;

public class MapperListener
extends LifecycleMBeanBase
implements ContainerListener,
LifecycleListener {
    private static final Log log = LogFactory.getLog(MapperListener.class);
    private final Mapper mapper;
    private final Service service;
    private static final StringManager sm = StringManager.getManager((String)"org.apache.catalina.mapper");
    private final String domain;

    public MapperListener(Service service) {
        this.domain = null;
        this.service = service;
        this.mapper = service.getMapper();
    }

    @Override
    public void startInternal() throws LifecycleException {
        Container[] conHosts;
        this.setState(LifecycleState.STARTING);
        Engine engine = this.service.getContainer();
        if (engine == null) {
            return;
        }
        this.findDefaultHost();
        this.addListeners(engine);
        for (Container conHost : conHosts = engine.findChildren()) {
            Host host = (Host)conHost;
            if (LifecycleState.NEW.equals((Object)host.getState())) continue;
            this.registerHost(host);
        }
    }

    @Override
    public void stopInternal() throws LifecycleException {
        this.setState(LifecycleState.STOPPING);
        Engine engine = this.service.getContainer();
        if (engine == null) {
            return;
        }
        this.removeListeners(engine);
    }

    @Override
    protected String getDomainInternal() {
        if (this.service instanceof LifecycleMBeanBase) {
            return this.service.getDomain();
        }
        return null;
    }

    @Override
    protected String getObjectNameKeyProperties() {
        return "type=Mapper";
    }

    @Override
    public void containerEvent(ContainerEvent event) {
        if ("addChild".equals(event.getType())) {
            Container child = (Container)event.getData();
            this.addListeners(child);
            if (child.getState().isAvailable()) {
                if (child instanceof Host) {
                    this.registerHost((Host)child);
                } else if (child instanceof Context) {
                    this.registerContext((Context)child);
                } else if (child instanceof Wrapper && child.getParent().getState().isAvailable()) {
                    this.registerWrapper((Wrapper)child);
                }
            }
        } else if ("removeChild".equals(event.getType())) {
            Container child = (Container)event.getData();
            this.removeListeners(child);
        } else if ("addAlias".equals(event.getType())) {
            this.mapper.addHostAlias(((Host)event.getSource()).getName(), event.getData().toString());
        } else if ("removeAlias".equals(event.getType())) {
            this.mapper.removeHostAlias(event.getData().toString());
        } else if ("addMapping".equals(event.getType())) {
            Wrapper wrapper = (Wrapper)event.getSource();
            Context context = (Context)wrapper.getParent();
            String contextPath = context.getPath();
            if ("/".equals(contextPath)) {
                contextPath = "";
            }
            String version = context.getWebappVersion();
            String hostName = context.getParent().getName();
            String wrapperName = wrapper.getName();
            String mapping = (String)event.getData();
            boolean jspWildCard = "jsp".equals(wrapperName) && mapping.endsWith("/*");
            this.mapper.addWrapper(hostName, contextPath, version, mapping, wrapper, jspWildCard, context.isResourceOnlyServlet(wrapperName));
        } else if ("removeMapping".equals(event.getType())) {
            Wrapper wrapper = (Wrapper)event.getSource();
            Context context = (Context)wrapper.getParent();
            String contextPath = context.getPath();
            if ("/".equals(contextPath)) {
                contextPath = "";
            }
            String version = context.getWebappVersion();
            String hostName = context.getParent().getName();
            String mapping = (String)event.getData();
            this.mapper.removeWrapper(hostName, contextPath, version, mapping);
        } else if ("addWelcomeFile".equals(event.getType())) {
            Context context = (Context)event.getSource();
            String hostName = context.getParent().getName();
            String contextPath = context.getPath();
            if ("/".equals(contextPath)) {
                contextPath = "";
            }
            String welcomeFile = (String)event.getData();
            this.mapper.addWelcomeFile(hostName, contextPath, context.getWebappVersion(), welcomeFile);
        } else if ("removeWelcomeFile".equals(event.getType())) {
            Context context = (Context)event.getSource();
            String hostName = context.getParent().getName();
            String contextPath = context.getPath();
            if ("/".equals(contextPath)) {
                contextPath = "";
            }
            String welcomeFile = (String)event.getData();
            this.mapper.removeWelcomeFile(hostName, contextPath, context.getWebappVersion(), welcomeFile);
        } else if ("clearWelcomeFiles".equals(event.getType())) {
            Context context = (Context)event.getSource();
            String hostName = context.getParent().getName();
            String contextPath = context.getPath();
            if ("/".equals(contextPath)) {
                contextPath = "";
            }
            this.mapper.clearWelcomeFiles(hostName, contextPath, context.getWebappVersion());
        }
    }

    private void findDefaultHost() {
        Engine engine = this.service.getContainer();
        String defaultHost = engine.getDefaultHost();
        boolean found = false;
        if (defaultHost != null && defaultHost.length() > 0) {
            Container[] containers;
            block0: for (Container container : containers = engine.findChildren()) {
                String[] aliases;
                Host host = (Host)container;
                if (defaultHost.equalsIgnoreCase(host.getName())) {
                    found = true;
                    break;
                }
                for (String alias : aliases = host.findAliases()) {
                    if (!defaultHost.equalsIgnoreCase(alias)) continue;
                    found = true;
                    continue block0;
                }
            }
        }
        if (found) {
            this.mapper.setDefaultHostName(defaultHost);
        } else {
            log.error((Object)sm.getString("mapperListener.unknownDefaultHost", new Object[]{defaultHost, this.service}));
        }
    }

    private void registerHost(Host host) {
        String[] aliases = host.findAliases();
        this.mapper.addHost(host.getName(), aliases, host);
        for (Container container : host.findChildren()) {
            if (!container.getState().isAvailable()) continue;
            this.registerContext((Context)container);
        }
        this.findDefaultHost();
        if (log.isDebugEnabled()) {
            log.debug((Object)sm.getString("mapperListener.registerHost", new Object[]{host.getName(), this.domain, this.service}));
        }
    }

    private void unregisterHost(Host host) {
        String hostname = host.getName();
        this.mapper.removeHost(hostname);
        this.findDefaultHost();
        if (log.isDebugEnabled()) {
            log.debug((Object)sm.getString("mapperListener.unregisterHost", new Object[]{hostname, this.domain, this.service}));
        }
    }

    private void unregisterWrapper(Wrapper wrapper) {
        String[] mappings;
        Context context = (Context)wrapper.getParent();
        String contextPath = context.getPath();
        String wrapperName = wrapper.getName();
        if ("/".equals(contextPath)) {
            contextPath = "";
        }
        String version = context.getWebappVersion();
        String hostName = context.getParent().getName();
        for (String mapping : mappings = wrapper.findMappings()) {
            this.mapper.removeWrapper(hostName, contextPath, version, mapping);
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)sm.getString("mapperListener.unregisterWrapper", new Object[]{wrapperName, contextPath, this.service}));
        }
    }

    private void registerContext(Context context) {
        String contextPath = context.getPath();
        if ("/".equals(contextPath)) {
            contextPath = "";
        }
        Host host = (Host)context.getParent();
        WebResourceRoot resources = context.getResources();
        String[] welcomeFiles = context.findWelcomeFiles();
        ArrayList<WrapperMappingInfo> wrappers = new ArrayList<WrapperMappingInfo>();
        for (Container container : context.findChildren()) {
            this.prepareWrapperMappingInfo(context, (Wrapper)container, wrappers);
            if (!log.isDebugEnabled()) continue;
            log.debug((Object)sm.getString("mapperListener.registerWrapper", new Object[]{container.getName(), contextPath, this.service}));
        }
        this.mapper.addContextVersion(host.getName(), host, contextPath, context.getWebappVersion(), context, welcomeFiles, resources, wrappers);
        if (log.isDebugEnabled()) {
            log.debug((Object)sm.getString("mapperListener.registerContext", new Object[]{contextPath, this.service}));
        }
    }

    private void unregisterContext(Context context) {
        String contextPath = context.getPath();
        if ("/".equals(contextPath)) {
            contextPath = "";
        }
        String hostName = context.getParent().getName();
        if (context.getPaused()) {
            if (log.isDebugEnabled()) {
                log.debug((Object)sm.getString("mapperListener.pauseContext", new Object[]{contextPath, this.service}));
            }
            this.mapper.pauseContextVersion(context, hostName, contextPath, context.getWebappVersion());
        } else {
            if (log.isDebugEnabled()) {
                log.debug((Object)sm.getString("mapperListener.unregisterContext", new Object[]{contextPath, this.service}));
            }
            this.mapper.removeContextVersion(context, hostName, contextPath, context.getWebappVersion());
        }
    }

    private void registerWrapper(Wrapper wrapper) {
        Context context = (Context)wrapper.getParent();
        String contextPath = context.getPath();
        if ("/".equals(contextPath)) {
            contextPath = "";
        }
        String version = context.getWebappVersion();
        String hostName = context.getParent().getName();
        ArrayList<WrapperMappingInfo> wrappers = new ArrayList<WrapperMappingInfo>();
        this.prepareWrapperMappingInfo(context, wrapper, wrappers);
        this.mapper.addWrappers(hostName, contextPath, version, wrappers);
        if (log.isDebugEnabled()) {
            log.debug((Object)sm.getString("mapperListener.registerWrapper", new Object[]{wrapper.getName(), contextPath, this.service}));
        }
    }

    private void prepareWrapperMappingInfo(Context context, Wrapper wrapper, List<WrapperMappingInfo> wrappers) {
        String[] mappings;
        String wrapperName = wrapper.getName();
        boolean resourceOnly = context.isResourceOnlyServlet(wrapperName);
        for (String mapping : mappings = wrapper.findMappings()) {
            boolean jspWildCard = wrapperName.equals("jsp") && mapping.endsWith("/*");
            wrappers.add(new WrapperMappingInfo(mapping, wrapper, jspWildCard, resourceOnly));
        }
    }

    @Override
    public void lifecycleEvent(LifecycleEvent event) {
        if (event.getType().equals("after_start")) {
            Object obj = event.getSource();
            if (obj instanceof Wrapper) {
                Wrapper w = (Wrapper)obj;
                if (w.getParent().getState().isAvailable()) {
                    this.registerWrapper(w);
                }
            } else if (obj instanceof Context) {
                Context c = (Context)obj;
                if (c.getParent().getState().isAvailable()) {
                    this.registerContext(c);
                }
            } else if (obj instanceof Host) {
                this.registerHost((Host)obj);
            }
        } else if (event.getType().equals("before_stop")) {
            Object obj = event.getSource();
            if (obj instanceof Wrapper) {
                this.unregisterWrapper((Wrapper)obj);
            } else if (obj instanceof Context) {
                this.unregisterContext((Context)obj);
            } else if (obj instanceof Host) {
                this.unregisterHost((Host)obj);
            }
        }
    }

    private void addListeners(Container container) {
        container.addContainerListener(this);
        container.addLifecycleListener(this);
        for (Container child : container.findChildren()) {
            this.addListeners(child);
        }
    }

    private void removeListeners(Container container) {
        container.removeContainerListener(this);
        container.removeLifecycleListener(this);
        for (Container child : container.findChildren()) {
            this.removeListeners(child);
        }
    }
}

