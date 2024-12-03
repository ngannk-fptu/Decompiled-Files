/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 */
package org.apache.catalina;

import java.beans.PropertyChangeListener;
import java.io.File;
import javax.management.ObjectName;
import org.apache.catalina.AccessLog;
import org.apache.catalina.Cluster;
import org.apache.catalina.ContainerListener;
import org.apache.catalina.Engine;
import org.apache.catalina.Host;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.Pipeline;
import org.apache.catalina.Realm;
import org.apache.catalina.Service;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.juli.logging.Log;

public interface Container
extends Lifecycle {
    public static final String ADD_CHILD_EVENT = "addChild";
    public static final String ADD_VALVE_EVENT = "addValve";
    public static final String REMOVE_CHILD_EVENT = "removeChild";
    public static final String REMOVE_VALVE_EVENT = "removeValve";

    public Log getLogger();

    public String getLogName();

    public ObjectName getObjectName();

    public String getDomain();

    public String getMBeanKeyProperties();

    public Pipeline getPipeline();

    public Cluster getCluster();

    public void setCluster(Cluster var1);

    public int getBackgroundProcessorDelay();

    public void setBackgroundProcessorDelay(int var1);

    public String getName();

    public void setName(String var1);

    public Container getParent();

    public void setParent(Container var1);

    public ClassLoader getParentClassLoader();

    public void setParentClassLoader(ClassLoader var1);

    public Realm getRealm();

    public void setRealm(Realm var1);

    public static String getConfigPath(Container container, String resourceName) {
        StringBuilder result = new StringBuilder();
        Container host = null;
        Container engine = null;
        while (container != null) {
            if (container instanceof Host) {
                host = container;
            } else if (container instanceof Engine) {
                engine = container;
            }
            container = container.getParent();
        }
        if (host != null && ((Host)host).getXmlBase() != null) {
            result.append(((Host)host).getXmlBase()).append('/');
        } else {
            result.append("conf/");
            if (engine != null) {
                result.append(engine.getName()).append('/');
            }
            if (host != null) {
                result.append(host.getName()).append('/');
            }
        }
        result.append(resourceName);
        return result.toString();
    }

    public static Service getService(Container container) {
        while (container != null && !(container instanceof Engine)) {
            container = container.getParent();
        }
        if (container == null) {
            return null;
        }
        return ((Engine)container).getService();
    }

    public void backgroundProcess();

    public void addChild(Container var1);

    public void addContainerListener(ContainerListener var1);

    public void addPropertyChangeListener(PropertyChangeListener var1);

    public Container findChild(String var1);

    public Container[] findChildren();

    public ContainerListener[] findContainerListeners();

    public void removeChild(Container var1);

    public void removeContainerListener(ContainerListener var1);

    public void removePropertyChangeListener(PropertyChangeListener var1);

    public void fireContainerEvent(String var1, Object var2);

    public void logAccess(Request var1, Response var2, long var3, boolean var5);

    public AccessLog getAccessLog();

    public int getStartStopThreads();

    public void setStartStopThreads(int var1);

    public File getCatalinaBase();

    public File getCatalinaHome();
}

