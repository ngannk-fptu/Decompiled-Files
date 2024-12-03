/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.mbeans;

import java.util.ArrayList;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import org.apache.catalina.Container;
import org.apache.catalina.ContainerListener;
import org.apache.catalina.JmxEnabled;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Valve;
import org.apache.catalina.core.ContainerBase;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardHost;
import org.apache.catalina.mbeans.BaseCatalinaMBean;
import org.apache.catalina.startup.ContextConfig;
import org.apache.catalina.startup.HostConfig;

public class ContainerMBean
extends BaseCatalinaMBean<ContainerBase> {
    public void addChild(String type, String name) throws MBeanException {
        LifecycleListener config;
        Container contained = (Container)ContainerMBean.newInstance(type);
        contained.setName(name);
        if (contained instanceof StandardHost) {
            config = new HostConfig();
            contained.addLifecycleListener(config);
        } else if (contained instanceof StandardContext) {
            config = new ContextConfig();
            contained.addLifecycleListener(config);
        }
        boolean oldValue = true;
        ContainerBase container = (ContainerBase)this.doGetManagedResource();
        try {
            oldValue = container.getStartChildren();
            container.setStartChildren(false);
            container.addChild(contained);
            contained.init();
        }
        catch (LifecycleException e) {
            throw new MBeanException(e);
        }
        finally {
            if (container != null) {
                container.setStartChildren(oldValue);
            }
        }
    }

    public void removeChild(String name) throws MBeanException {
        if (name != null) {
            Container container = (Container)this.doGetManagedResource();
            Container contained = container.findChild(name);
            container.removeChild(contained);
        }
    }

    public String addValve(String valveType) throws MBeanException {
        Valve valve = (Valve)ContainerMBean.newInstance(valveType);
        Container container = (Container)this.doGetManagedResource();
        container.getPipeline().addValve(valve);
        if (valve instanceof JmxEnabled) {
            return ((JmxEnabled)((Object)valve)).getObjectName().toString();
        }
        return null;
    }

    public void removeValve(String valveName) throws MBeanException {
        ObjectName oname;
        Container container = (Container)this.doGetManagedResource();
        try {
            oname = new ObjectName(valveName);
        }
        catch (NullPointerException | MalformedObjectNameException e) {
            throw new MBeanException(e);
        }
        if (container != null) {
            Valve[] valves;
            for (Valve valve : valves = container.getPipeline().getValves()) {
                ObjectName voname;
                if (!(valve instanceof JmxEnabled) || !(voname = ((JmxEnabled)((Object)valve)).getObjectName()).equals(oname)) continue;
                container.getPipeline().removeValve(valve);
            }
        }
    }

    public void addLifecycleListener(String type) throws MBeanException {
        LifecycleListener listener = (LifecycleListener)ContainerMBean.newInstance(type);
        Container container = (Container)this.doGetManagedResource();
        container.addLifecycleListener(listener);
    }

    public void removeLifecycleListeners(String type) throws MBeanException {
        LifecycleListener[] listeners;
        Container container = (Container)this.doGetManagedResource();
        for (LifecycleListener listener : listeners = container.findLifecycleListeners()) {
            if (!listener.getClass().getName().equals(type)) continue;
            container.removeLifecycleListener(listener);
        }
    }

    public String[] findLifecycleListenerNames() throws MBeanException {
        LifecycleListener[] listeners;
        Container container = (Container)this.doGetManagedResource();
        ArrayList<String> result = new ArrayList<String>();
        for (LifecycleListener listener : listeners = container.findLifecycleListeners()) {
            result.add(listener.getClass().getName());
        }
        return result.toArray(new String[0]);
    }

    public String[] findContainerListenerNames() throws MBeanException {
        ContainerListener[] listeners;
        Container container = (Container)this.doGetManagedResource();
        ArrayList<String> result = new ArrayList<String>();
        for (ContainerListener listener : listeners = container.findContainerListeners()) {
            result.add(listener.getClass().getName());
        }
        return result.toArray(new String[0]);
    }
}

