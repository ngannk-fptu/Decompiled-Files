/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.catalina.tribes.jmx;

import java.lang.management.ManagementFactory;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import org.apache.catalina.tribes.Channel;
import org.apache.catalina.tribes.JmxChannel;
import org.apache.catalina.tribes.util.StringManager;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public class JmxRegistry {
    private static final Log log = LogFactory.getLog(JmxRegistry.class);
    protected static final StringManager sm = StringManager.getManager(JmxRegistry.class);
    private static ConcurrentHashMap<String, JmxRegistry> registryCache = new ConcurrentHashMap();
    private MBeanServer mbserver = ManagementFactory.getPlatformMBeanServer();
    private ObjectName baseOname = null;

    private JmxRegistry() {
    }

    public static JmxRegistry getRegistry(Channel channel) {
        if (channel == null || channel.getName() == null) {
            return null;
        }
        JmxRegistry registry = registryCache.get(channel.getName());
        if (registry != null) {
            return registry;
        }
        if (!(channel instanceof JmxChannel)) {
            return null;
        }
        JmxChannel jmxChannel = (JmxChannel)((Object)channel);
        if (!jmxChannel.isJmxEnabled()) {
            return null;
        }
        ObjectName baseOn = JmxRegistry.createBaseObjectName(jmxChannel.getJmxDomain(), jmxChannel.getJmxPrefix(), channel.getName());
        if (baseOn == null) {
            return null;
        }
        registry = new JmxRegistry();
        registry.baseOname = baseOn;
        registryCache.put(channel.getName(), registry);
        return registry;
    }

    public static void removeRegistry(Channel channel, boolean clear) {
        JmxRegistry registry = registryCache.get(channel.getName());
        if (registry == null) {
            return;
        }
        if (clear) {
            registry.clearMBeans();
        }
        registryCache.remove(channel.getName());
    }

    private static ObjectName createBaseObjectName(String domain, String prefix, String name) {
        if (domain == null) {
            log.warn((Object)sm.getString("jmxRegistry.no.domain"));
            return null;
        }
        ObjectName on = null;
        StringBuilder sb = new StringBuilder(domain);
        sb.append(':');
        sb.append(prefix);
        sb.append("type=Channel,channel=");
        sb.append(name);
        try {
            on = new ObjectName(sb.toString());
        }
        catch (MalformedObjectNameException e) {
            log.error((Object)sm.getString("jmxRegistry.objectName.failed", sb.toString()), (Throwable)e);
        }
        return on;
    }

    public ObjectName registerJmx(String keyprop, Object bean) {
        if (this.mbserver == null) {
            return null;
        }
        String oNameStr = this.baseOname.toString() + keyprop;
        ObjectName oName = null;
        try {
            oName = new ObjectName(oNameStr);
            if (this.mbserver.isRegistered(oName)) {
                this.mbserver.unregisterMBean(oName);
            }
            this.mbserver.registerMBean(bean, oName);
        }
        catch (NotCompliantMBeanException e) {
            log.warn((Object)sm.getString("jmxRegistry.registerJmx.notCompliant", bean), (Throwable)e);
            return null;
        }
        catch (MalformedObjectNameException e) {
            log.error((Object)sm.getString("jmxRegistry.objectName.failed", oNameStr), (Throwable)e);
            return null;
        }
        catch (Exception e) {
            log.error((Object)sm.getString("jmxRegistry.registerJmx.failed", bean, oNameStr), (Throwable)e);
            return null;
        }
        return oName;
    }

    public void unregisterJmx(ObjectName oname) {
        if (oname == null) {
            return;
        }
        try {
            this.mbserver.unregisterMBean(oname);
        }
        catch (InstanceNotFoundException e) {
            log.warn((Object)sm.getString("jmxRegistry.unregisterJmx.notFound", oname), (Throwable)e);
        }
        catch (Exception e) {
            log.warn((Object)sm.getString("jmxRegistry.unregisterJmx.failed", oname), (Throwable)e);
        }
    }

    private void clearMBeans() {
        String query = this.baseOname.toString() + ",*";
        try {
            ObjectName name = new ObjectName(query);
            Set<ObjectName> onames = this.mbserver.queryNames(name, null);
            for (ObjectName objectName : onames) {
                this.unregisterJmx(objectName);
            }
        }
        catch (MalformedObjectNameException e) {
            log.error((Object)sm.getString("jmxRegistry.objectName.failed", query), (Throwable)e);
        }
    }
}

