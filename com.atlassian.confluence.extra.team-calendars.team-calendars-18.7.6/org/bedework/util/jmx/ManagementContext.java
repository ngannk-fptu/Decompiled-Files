/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.util.jmx;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.MBeanServerInvocationHandler;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.QueryExp;
import org.apache.log4j.Logger;
import org.bedework.util.misc.Util;

public class ManagementContext {
    private transient Logger log;
    public static final String DEFAULT_DOMAIN = System.getProperty("org.bedework.jmx.defaultdomain");
    public static final boolean isJboss5 = Boolean.getBoolean("org.bedework.jmx.isJboss5");
    static final String JMI_DOMAIN = "JMImplementation";
    static final String MBEAN_REGISTRY = "JMImplementation:type=MBeanRegistry";
    static final String CLASSLOADER = System.getProperty("org.bedework.jmx.classloader");
    private MBeanServer beanServer;
    private String jmxDomainName = DEFAULT_DOMAIN;
    private boolean useDomainSpecifiedForServer = false;
    private boolean useMBeanServer = true;
    private boolean createMBeanServer = true;
    private boolean locallyCreateMBeanServer;
    private AtomicBoolean started = new AtomicBoolean(false);
    private List<ObjectName> registeredMBeanNames = new CopyOnWriteArrayList<ObjectName>();

    public ManagementContext() {
    }

    public ManagementContext(String domain) {
        this.setJmxDomainName(domain);
        this.useDomainSpecifiedForServer = domain != null;
    }

    public ManagementContext(MBeanServer server) {
        this.beanServer = server;
    }

    public void start() throws IOException {
        if (this.started.compareAndSet(false, true)) {
            this.getMBeanServer();
        }
    }

    public void stop() throws Exception {
        if (this.started.compareAndSet(true, false)) {
            ArrayList<MBeanServer> list;
            MBeanServer mbeanServer = this.getMBeanServer();
            if (mbeanServer != null) {
                for (ObjectName name : this.registeredMBeanNames) {
                    mbeanServer.unregisterMBean(name);
                }
            }
            this.registeredMBeanNames.clear();
            if (this.locallyCreateMBeanServer && this.beanServer != null && (list = MBeanServerFactory.findMBeanServer(null)) != null && !list.isEmpty() && list.contains(this.beanServer)) {
                MBeanServerFactory.releaseMBeanServer(this.beanServer);
            }
            this.beanServer = null;
        }
    }

    public void setJmxDomainName(String val) {
        this.jmxDomainName = val;
    }

    public String getJmxDomainName() {
        return this.jmxDomainName;
    }

    public void setMBeanServer(MBeanServer val) {
        this.beanServer = val;
    }

    public MBeanServer getMBeanServer() {
        if (this.beanServer == null) {
            this.beanServer = this.findMBeanServer();
        }
        return this.beanServer;
    }

    public boolean isUseMBeanServer() {
        return this.useMBeanServer;
    }

    public void setUseMBeanServer(boolean useMBeanServer) {
        this.useMBeanServer = useMBeanServer;
    }

    public boolean isCreateMBeanServer() {
        return this.createMBeanServer;
    }

    public void setCreateMBeanServer(boolean enableJMX) {
        this.createMBeanServer = enableJMX;
    }

    public ObjectName createCustomComponentMBeanName(String type, String name) {
        ObjectName result = null;
        String tmp = this.jmxDomainName + ":type=" + ManagementContext.sanitizeString(type) + ",name=" + ManagementContext.sanitizeString(name);
        try {
            result = new ObjectName(tmp);
        }
        catch (MalformedObjectNameException e) {
            this.error("Couldn't create ObjectName from: " + type + " , " + name);
        }
        return result;
    }

    private static String sanitizeString(String in) {
        String result = null;
        if (in != null) {
            result = in.replace(':', '_');
            result = result.replace('/', '_');
            result = result.replace('\\', '_');
        }
        return result;
    }

    public static String encodeObjectNamePart(String part) {
        String answer = part.replaceAll("[\\:\\,\\'\\\"]", "_");
        answer = answer.replaceAll("\\?", "&qe;");
        answer = answer.replaceAll("=", "&amp;");
        answer = answer.replaceAll("\\*", "&ast;");
        return answer;
    }

    public static ObjectName getSystemObjectName(String domainName, String containerName, Class theClass) throws MalformedObjectNameException {
        String tmp = domainName + ":type=" + theClass.getName() + ",name=" + ManagementContext.getRelativeName(containerName, theClass);
        return new ObjectName(tmp);
    }

    private static String getRelativeName(String containerName, Class theClass) {
        String name = theClass.getName();
        int index = name.lastIndexOf(".");
        if (index >= 0 && index + 1 < name.length()) {
            name = name.substring(index + 1);
        }
        return containerName + "." + name;
    }

    public Object newProxyInstance(ObjectName objectName, Class interfaceClass, boolean notificationBroadcaster) {
        return MBeanServerInvocationHandler.newProxyInstance(this.getMBeanServer(), objectName, interfaceClass, notificationBroadcaster);
    }

    public Object getAttribute(ObjectName name, String attribute) throws Exception {
        return this.getMBeanServer().getAttribute(name, attribute);
    }

    public void registerMBean(Object bean, ObjectName name) throws Exception {
        if (!isJboss5) {
            this.getMBeanServer().registerMBean(bean, name);
            this.registeredMBeanNames.add(name);
            return;
        }
        HashMap<String, ClassLoader> values = new HashMap<String, ClassLoader>();
        ClassLoader classLoader = this.getClass().getClassLoader();
        this.info(String.format("Registering " + name + " to JMX with classLoader [%s]", classLoader.toString()));
        values.put(CLASSLOADER, classLoader);
        this.getMBeanServer().invoke(new ObjectName(MBEAN_REGISTRY), "registerMBean", new Object[]{bean, name, values}, new String[]{Object.class.getName(), ObjectName.class.getName(), Map.class.getName()});
        this.registeredMBeanNames.add(name);
    }

    public Set queryNames(ObjectName name, QueryExp query) throws Exception {
        return this.getMBeanServer().queryNames(name, query);
    }

    public void unregisterMBean(ObjectName name) throws JMException {
        if (this.beanServer != null && this.beanServer.isRegistered(name) && this.registeredMBeanNames.remove(name)) {
            this.beanServer.unregisterMBean(name);
        }
    }

    protected synchronized MBeanServer findMBeanServer() {
        Object result = null;
        try {
            ArrayList<MBeanServer> list;
            if (this.useMBeanServer && !Util.isEmpty(list = MBeanServerFactory.findMBeanServer(null))) {
                Object mbsvr = null;
                for (MBeanServer svr : list) {
                    if (this.jmxDomainName == null) {
                        return svr;
                    }
                    String svrDomain = svr.getDefaultDomain();
                    if (svrDomain == null || !svrDomain.equals(this.jmxDomainName)) continue;
                    return svr;
                }
                this.warn("Unable to locate mbean server for domain " + this.jmxDomainName);
                if (!this.useDomainSpecifiedForServer) {
                    return (MBeanServer)list.get(0);
                }
            }
            if (this.createMBeanServer) {
                return this.createMBeanServer();
            }
        }
        catch (NoClassDefFoundError e) {
            this.error(e);
        }
        catch (Throwable e) {
            this.error(e);
        }
        return null;
    }

    protected MBeanServer createMBeanServer() throws MalformedObjectNameException, IOException {
        MBeanServer mbeanServer = MBeanServerFactory.createMBeanServer(this.jmxDomainName);
        this.locallyCreateMBeanServer = true;
        return mbeanServer;
    }

    protected void info(String msg) {
        this.getLogger().info(msg);
    }

    protected void warn(String msg) {
        this.getLogger().warn(msg);
    }

    protected void debug(String msg) {
        this.getLogger().debug(msg);
    }

    protected void error(Throwable t) {
        this.getLogger().error(this, t);
    }

    protected void error(String msg) {
        this.getLogger().error(msg);
    }

    protected Logger getLogger() {
        if (this.log == null) {
            this.log = Logger.getLogger(this.getClass());
        }
        return this.log;
    }
}

