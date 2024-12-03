/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.tomcat.util.modeler;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import javax.management.DynamicMBean;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.modeler.ManagedBean;
import org.apache.tomcat.util.modeler.NoDescriptorRegistry;
import org.apache.tomcat.util.modeler.RegistryMBean;
import org.apache.tomcat.util.modeler.modules.ModelerSource;
import org.apache.tomcat.util.res.StringManager;

public class Registry
implements RegistryMBean,
MBeanRegistration {
    private static final Log log = LogFactory.getLog(Registry.class);
    private static final StringManager sm = StringManager.getManager(Registry.class);
    private static Registry registry = null;
    private volatile MBeanServer server = null;
    private final Object serverLock = new Object();
    private Map<String, ManagedBean> descriptors = new HashMap<String, ManagedBean>();
    private Map<String, ManagedBean> descriptorsByClass = new HashMap<String, ManagedBean>();
    private Map<String, URL> searchedPaths = new HashMap<String, URL>();
    private Object guard;
    private final Hashtable<String, Hashtable<String, Integer>> idDomains = new Hashtable();
    private final Hashtable<String, int[]> ids = new Hashtable();

    protected Registry() {
    }

    public static synchronized Registry getRegistry(Object key, Object guard) {
        if (registry == null) {
            registry = new Registry();
            Registry.registry.guard = guard;
        }
        if (Registry.registry.guard != null && Registry.registry.guard != guard) {
            return null;
        }
        return registry;
    }

    public static synchronized void disableRegistry() {
        if (registry == null) {
            registry = new NoDescriptorRegistry();
        } else if (!(registry instanceof NoDescriptorRegistry)) {
            log.warn((Object)sm.getString("registry.noDisable"));
        }
    }

    @Override
    public void stop() {
        this.descriptorsByClass = new HashMap<String, ManagedBean>();
        this.descriptors = new HashMap<String, ManagedBean>();
        this.searchedPaths = new HashMap<String, URL>();
    }

    @Override
    public void registerComponent(Object bean, String oname, String type) throws Exception {
        this.registerComponent(bean, new ObjectName(oname), type);
    }

    @Override
    public void unregisterComponent(String oname) {
        try {
            this.unregisterComponent(new ObjectName(oname));
        }
        catch (MalformedObjectNameException e) {
            log.info((Object)sm.getString("registry.objectNameCreateError"), (Throwable)e);
        }
    }

    @Override
    public void invoke(List<ObjectName> mbeans, String operation, boolean failFirst) throws Exception {
        if (mbeans == null) {
            return;
        }
        for (ObjectName current : mbeans) {
            try {
                if (current == null || this.getMethodInfo(current, operation) == null) continue;
                this.getMBeanServer().invoke(current, operation, new Object[0], new String[0]);
            }
            catch (Exception t) {
                if (failFirst) {
                    throw t;
                }
                log.info((Object)sm.getString("registry.initError"), (Throwable)t);
            }
        }
    }

    @Override
    public synchronized int getId(String domain, String name) {
        Integer i;
        if (domain == null) {
            domain = "";
        }
        Hashtable domainTable = this.idDomains.computeIfAbsent(domain, k -> new Hashtable());
        if (name == null) {
            name = "";
        }
        if ((i = (Integer)domainTable.get(name)) != null) {
            return i;
        }
        int[] id = this.ids.computeIfAbsent(domain, k -> new int[1]);
        int n = id[0];
        id[0] = n + 1;
        int code = n;
        domainTable.put(name, code);
        return code;
    }

    public void addManagedBean(ManagedBean bean) {
        this.descriptors.put(bean.getName(), bean);
        if (bean.getType() != null) {
            this.descriptorsByClass.put(bean.getType(), bean);
        }
    }

    public ManagedBean findManagedBean(String name) {
        ManagedBean mb = this.descriptors.get(name);
        if (mb == null) {
            mb = this.descriptorsByClass.get(name);
        }
        return mb;
    }

    public String getType(ObjectName oname, String attName) {
        MBeanAttributeInfo[] attInfo;
        String type = null;
        MBeanInfo info = null;
        try {
            info = this.getMBeanServer().getMBeanInfo(oname);
        }
        catch (Exception e) {
            log.info((Object)sm.getString("registry.noMetadata", new Object[]{oname}));
            return null;
        }
        for (MBeanAttributeInfo mBeanAttributeInfo : attInfo = info.getAttributes()) {
            if (!attName.equals(mBeanAttributeInfo.getName())) continue;
            type = mBeanAttributeInfo.getType();
            return type;
        }
        return null;
    }

    public MBeanOperationInfo getMethodInfo(ObjectName oname, String opName) {
        MBeanOperationInfo[] attInfo;
        MBeanInfo info = null;
        try {
            info = this.getMBeanServer().getMBeanInfo(oname);
        }
        catch (Exception e) {
            log.info((Object)sm.getString("registry.noMetadata", new Object[]{oname}));
            return null;
        }
        for (MBeanOperationInfo mBeanOperationInfo : attInfo = info.getOperations()) {
            if (!opName.equals(mBeanOperationInfo.getName())) continue;
            return mBeanOperationInfo;
        }
        return null;
    }

    public MBeanOperationInfo getMethodInfo(ObjectName oname, String opName, int argCount) throws InstanceNotFoundException {
        MBeanOperationInfo[] attInfo;
        MBeanInfo info = null;
        try {
            info = this.getMBeanServer().getMBeanInfo(oname);
        }
        catch (InstanceNotFoundException infe) {
            throw infe;
        }
        catch (Exception e) {
            log.warn((Object)sm.getString("registry.noMetadata", new Object[]{oname}), (Throwable)e);
            return null;
        }
        for (MBeanOperationInfo mBeanOperationInfo : attInfo = info.getOperations()) {
            if (!opName.equals(mBeanOperationInfo.getName()) || argCount != mBeanOperationInfo.getSignature().length) continue;
            return mBeanOperationInfo;
        }
        return null;
    }

    public void unregisterComponent(ObjectName oname) {
        try {
            if (oname != null && this.getMBeanServer().isRegistered(oname)) {
                this.getMBeanServer().unregisterMBean(oname);
            }
        }
        catch (Throwable t) {
            log.error((Object)sm.getString("registry.unregisterError"), t);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public MBeanServer getMBeanServer() {
        if (this.server == null) {
            Object object = this.serverLock;
            synchronized (object) {
                if (this.server == null) {
                    long t1 = System.currentTimeMillis();
                    if (MBeanServerFactory.findMBeanServer(null).size() > 0) {
                        this.server = MBeanServerFactory.findMBeanServer(null).get(0);
                        if (log.isDebugEnabled()) {
                            log.debug((Object)("Using existing MBeanServer " + (System.currentTimeMillis() - t1)));
                        }
                    } else {
                        this.server = ManagementFactory.getPlatformMBeanServer();
                        if (log.isDebugEnabled()) {
                            log.debug((Object)("Created MBeanServer" + (System.currentTimeMillis() - t1)));
                        }
                    }
                }
            }
        }
        return this.server;
    }

    public ManagedBean findManagedBean(Object bean, Class<?> beanClass, String type) throws Exception {
        ManagedBean managed;
        if (bean != null && beanClass == null) {
            beanClass = bean.getClass();
        }
        if (type == null) {
            type = beanClass.getName();
        }
        if ((managed = this.findManagedBean(type)) == null) {
            if (log.isDebugEnabled()) {
                log.debug((Object)"Looking for descriptor ");
            }
            this.findDescriptor(beanClass, type);
            managed = this.findManagedBean(type);
        }
        if (managed == null) {
            if (log.isDebugEnabled()) {
                log.debug((Object)"Introspecting ");
            }
            this.load("MbeansDescriptorsIntrospectionSource", beanClass, type);
            managed = this.findManagedBean(type);
            if (managed == null) {
                log.warn((Object)sm.getString("registry.noTypeMetadata", new Object[]{type}));
                return null;
            }
            managed.setName(type);
            this.addManagedBean(managed);
        }
        return managed;
    }

    public Object convertValue(String type, String value) {
        Object objValue = value;
        if (type == null || "java.lang.String".equals(type)) {
            objValue = value;
        } else if ("javax.management.ObjectName".equals(type) || "ObjectName".equals(type)) {
            try {
                objValue = new ObjectName(value);
            }
            catch (MalformedObjectNameException e) {
                return null;
            }
        } else if ("java.lang.Integer".equals(type) || "int".equals(type)) {
            objValue = Integer.valueOf(value);
        } else if ("java.lang.Long".equals(type) || "long".equals(type)) {
            objValue = Long.valueOf(value);
        } else if ("java.lang.Boolean".equals(type) || "boolean".equals(type)) {
            objValue = Boolean.valueOf(value);
        }
        return objValue;
    }

    public List<ObjectName> load(String sourceType, Object source, String param) throws Exception {
        if (log.isTraceEnabled()) {
            log.trace((Object)("load " + source));
        }
        String location = null;
        String type = null;
        Object inputsource = null;
        if (source instanceof URL) {
            URL url = (URL)source;
            location = url.toString();
            type = param;
            inputsource = url.openStream();
            if (sourceType == null && location.endsWith(".xml")) {
                sourceType = "MbeansDescriptorsDigesterSource";
            }
        } else if (source instanceof File) {
            location = ((File)source).getAbsolutePath();
            inputsource = new FileInputStream((File)source);
            type = param;
            if (sourceType == null && location.endsWith(".xml")) {
                sourceType = "MbeansDescriptorsDigesterSource";
            }
        } else if (source instanceof InputStream) {
            type = param;
            inputsource = source;
        } else if (source instanceof Class) {
            location = ((Class)source).getName();
            type = param;
            inputsource = source;
            if (sourceType == null) {
                sourceType = "MbeansDescriptorsIntrospectionSource";
            }
        } else {
            throw new IllegalArgumentException(sm.getString("registry.invalidSource"));
        }
        if (sourceType == null) {
            sourceType = "MbeansDescriptorsDigesterSource";
        }
        ModelerSource ds = this.getModelerSource(sourceType);
        List<ObjectName> mbeans = ds.loadDescriptors(this, type, inputsource);
        return mbeans;
    }

    public void registerComponent(Object bean, ObjectName oname, String type) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug((Object)("Managed= " + oname));
        }
        if (bean == null) {
            log.error((Object)sm.getString("registry.nullBean", new Object[]{oname}));
            return;
        }
        try {
            if (type == null) {
                type = bean.getClass().getName();
            }
            ManagedBean managed = this.findManagedBean(null, bean.getClass(), type);
            DynamicMBean mbean = managed.createMBean(bean);
            if (this.getMBeanServer().isRegistered(oname)) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)("Unregistering existing component " + oname));
                }
                this.getMBeanServer().unregisterMBean(oname);
            }
            this.getMBeanServer().registerMBean(mbean, oname);
        }
        catch (Exception ex) {
            log.error((Object)sm.getString("registry.registerError", new Object[]{oname}), (Throwable)ex);
            throw ex;
        }
    }

    public void loadDescriptors(String packageName, ClassLoader classLoader) {
        String res = packageName.replace('.', '/');
        if (log.isTraceEnabled()) {
            log.trace((Object)("Finding descriptor " + res));
        }
        if (this.searchedPaths.get(packageName) != null) {
            return;
        }
        String descriptors = res + "/mbeans-descriptors.xml";
        URL dURL = classLoader.getResource(descriptors);
        if (dURL == null) {
            return;
        }
        log.debug((Object)("Found " + dURL));
        this.searchedPaths.put(packageName, dURL);
        try {
            this.load("MbeansDescriptorsDigesterSource", dURL, null);
        }
        catch (Exception ex) {
            log.error((Object)sm.getString("registry.loadError", new Object[]{dURL}));
        }
    }

    private void findDescriptor(Class<?> beanClass, String type) {
        String className;
        if (type == null) {
            type = beanClass.getName();
        }
        ClassLoader classLoader = null;
        if (beanClass != null) {
            classLoader = beanClass.getClassLoader();
        }
        if (classLoader == null) {
            classLoader = Thread.currentThread().getContextClassLoader();
        }
        if (classLoader == null) {
            classLoader = this.getClass().getClassLoader();
        }
        String pkg = className = type;
        while (pkg.indexOf(46) > 0) {
            int lastComp = pkg.lastIndexOf(46);
            if (lastComp <= 0) {
                return;
            }
            if (this.searchedPaths.get(pkg = pkg.substring(0, lastComp)) != null) {
                return;
            }
            this.loadDescriptors(pkg, classLoader);
        }
    }

    private ModelerSource getModelerSource(String type) throws Exception {
        if (type == null) {
            type = "MbeansDescriptorsDigesterSource";
        }
        if (!type.contains(".")) {
            type = "org.apache.tomcat.util.modeler.modules." + type;
        }
        Class<?> c = Class.forName(type);
        ModelerSource ds = (ModelerSource)c.getConstructor(new Class[0]).newInstance(new Object[0]);
        return ds;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ObjectName preRegister(MBeanServer server, ObjectName name) throws Exception {
        Object object = this.serverLock;
        synchronized (object) {
            this.server = server;
        }
        return name;
    }

    @Override
    public void postRegister(Boolean registrationDone) {
    }

    @Override
    public void preDeregister() throws Exception {
    }

    @Override
    public void postDeregister() {
    }
}

