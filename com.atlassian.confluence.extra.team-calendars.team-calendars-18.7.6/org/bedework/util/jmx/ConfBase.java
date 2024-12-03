/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.util.jmx;

import java.io.File;
import java.io.FileReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import org.apache.log4j.Logger;
import org.bedework.util.config.ConfigBase;
import org.bedework.util.config.ConfigException;
import org.bedework.util.config.ConfigurationFileStore;
import org.bedework.util.config.ConfigurationStore;
import org.bedework.util.jmx.AnnotatedMBean;
import org.bedework.util.jmx.ConfBaseMBean;
import org.bedework.util.jmx.MBeanInfo;
import org.bedework.util.jmx.ManagementContext;
import org.bedework.util.misc.Logged;
import org.bedework.util.misc.Util;

public abstract class ConfBase<T extends ConfigBase>
extends Logged
implements ConfBaseMBean {
    public static final String statusDone = "Done";
    public static final String statusFailed = "Failed";
    public static final String statusRunning = "Running";
    public static final String statusStopped = "Stopped";
    public static final String statusTimedout = "Timedout";
    public static final String statusInterrupted = "Interrupted";
    public static final String statusUnknown = "Unknown";
    protected T cfg;
    private String configName;
    private String configuri;
    private String status = "Unknown";
    private static volatile Object pfileLock = new Object();
    private static Properties pfile;
    private static final String pfilePname = "org.bedework.config.pfile";
    private static final String configBasePname = "org.bedework.config.base";
    private static String configBase;
    private static boolean configBaseIsFile;
    private static boolean configBaseIsHttp;
    private static final List<String> httpSchemes;
    private String configPname;
    private String pathSuffix;
    private static Set<ObjectName> registeredMBeans;
    private static ManagementContext managementContext;
    private String serviceName;
    private ConfigurationStore store;
    private ObjectName serviceObjectName;

    protected ConfBase() {
    }

    protected ConfBase(String serviceName) {
        this.serviceName = serviceName;
    }

    public void setServiceName(String val) {
        this.serviceName = val;
    }

    @Override
    public String getServiceName() {
        return this.serviceName;
    }

    @Override
    public void setStatus(String val) {
        this.status = val;
    }

    @Override
    public String getStatus() {
        return this.status;
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
    }

    @Override
    public boolean isRunning() {
        return true;
    }

    public void setConfigUri(String val) {
        this.configuri = val;
        this.store = null;
    }

    public String getConfigUri() {
        return this.configuri;
    }

    public void setConfigPname(String val) {
        this.configPname = val;
        this.store = null;
    }

    public String getConfigPname() {
        return this.configPname;
    }

    public void setPathSuffix(String val) {
        this.pathSuffix = val;
        this.store = null;
    }

    public String getPathSuffix() {
        return this.pathSuffix;
    }

    public void setStore(ConfigurationStore val) {
        this.store = val;
    }

    public ConfigurationStore getStore() throws ConfigException {
        if (this.store != null) {
            return this.store;
        }
        String uriStr = this.getConfigUri();
        if (uriStr == null) {
            int lastDotpos;
            int pos;
            this.getPfile();
            String configPname = this.getConfigPname();
            if (configPname == null) {
                throw new ConfigException("Either a uri or property name must be specified");
            }
            uriStr = pfile.getProperty(configPname);
            if (uriStr == null && configPname.endsWith(".confuri") && (pos = configPname.lastIndexOf(46, (lastDotpos = configPname.length() - 8) - 1)) > 0) {
                uriStr = configPname.substring(pos + 1, lastDotpos);
            }
            if (uriStr == null) {
                throw new ConfigException("No property with name \"" + configPname + "\"");
            }
        }
        try {
            URI uri = new URI(uriStr);
            String scheme = uri.getScheme();
            if (scheme == null) {
                String path = uri.getPath();
                File f = new File(path);
                if (!f.isAbsolute() && configBase != null) {
                    path = configBase + path;
                }
                uri = new URI(path);
                scheme = uri.getScheme();
            }
            if (scheme == null || scheme.equals("file")) {
                String path = uri.getPath();
                if (this.getPathSuffix() != null) {
                    if (!path.endsWith(File.separator)) {
                        path = path + File.separator;
                    }
                    path = path + this.getPathSuffix() + File.separator;
                }
                this.store = new ConfigurationFileStore(path);
                return this.store;
            }
            throw new ConfigException("Unsupported ConfigurationStore: " + uri);
        }
        catch (URISyntaxException use) {
            throw new ConfigException(use);
        }
    }

    public T getConfig() {
        return this.cfg;
    }

    @MBeanInfo(value="(Re)load the configuration")
    public abstract String loadConfig();

    protected Set<ObjectName> getRegisteredMBeans() {
        return registeredMBeans;
    }

    @Override
    public void setConfigName(String val) {
        this.configName = val;
    }

    @Override
    public String getConfigName() {
        return this.configName;
    }

    @Override
    public String saveConfig() {
        try {
            T config = this.getConfig();
            if (config == null) {
                return "No configuration to save";
            }
            ConfigurationStore cs = this.getStore();
            ((ConfigBase)config).setName(this.configName);
            cs.saveConfiguration((ConfigBase)config);
            return "saved";
        }
        catch (Throwable t) {
            this.error(t);
            return t.getLocalizedMessage();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void getPfile() throws ConfigException {
        if (pfile != null) {
            return;
        }
        String pfileUri = System.getProperty(pfilePname);
        if (pfileUri == null) {
            throw new ConfigException("No property with name \"org.bedework.config.pfile\"");
        }
        try {
            String path = pfileUri;
            File f = new File(path);
            if (!f.exists()) {
                throw new ConfigException("No configuration pfile at " + path);
            }
            if (!f.isFile()) {
                throw new ConfigException(path + " is not a file");
            }
            Util.PropertiesPropertyFetcher ppf = new Util.PropertiesPropertyFetcher(System.getProperties());
            Object object = pfileLock;
            synchronized (object) {
                if (pfile != null) {
                    return;
                }
                pfile = new Properties();
                pfile.load(new FileReader(f));
                Set<Object> pfileNames = pfile.keySet();
                for (Object o : pfileNames) {
                    pfile.put(o, Util.propertyReplace(pfile.getProperty((String)o), ppf));
                }
                configBase = pfile.getProperty(configBasePname);
                URI uri = new URI(configBase);
                String scheme = uri.getScheme();
                if (scheme == null || scheme.equals("file")) {
                    configBase = uri.getPath();
                    configBaseIsFile = true;
                } else if (httpSchemes.contains(scheme)) {
                    configBaseIsHttp = true;
                } else {
                    throw new ConfigException("Unsupported scheme in " + uri);
                }
            }
        }
        catch (ConfigException ce) {
            throw ce;
        }
        catch (Throwable t) {
            throw new ConfigException(t);
        }
    }

    protected void register(String serviceType, String name, Object view) {
        try {
            ObjectName objectName = this.createObjectName(serviceType, name);
            this.register(objectName, view);
        }
        catch (Throwable t) {
            this.error("Failed to register " + serviceType + ":" + name);
            this.error(t);
        }
    }

    protected void unregister(String serviceType, String name) {
        try {
            ObjectName objectName = this.createObjectName(serviceType, name);
            this.unregister(objectName);
        }
        catch (Throwable t) {
            this.error("Failed to unregister " + serviceType + ":" + name);
            this.error(t);
        }
    }

    protected ObjectName getServiceObjectName() throws MalformedObjectNameException {
        if (this.serviceObjectName == null) {
            this.serviceObjectName = new ObjectName(this.getServiceName());
        }
        return this.serviceObjectName;
    }

    protected ObjectName createObjectName(String serviceType, String name) throws MalformedObjectNameException {
        Hashtable<String, String> props = this.getServiceObjectName().getKeyPropertyList();
        ObjectName objectName = new ObjectName(this.getServiceObjectName().getDomain() + ":service=" + props.get("service") + ",Type=" + ManagementContext.encodeObjectNamePart(serviceType) + ",Name=" + ManagementContext.encodeObjectNamePart(name));
        return objectName;
    }

    protected T getConfigInfo(Class<T> cl) throws ConfigException {
        return this.getConfigInfo(this.getStore(), this.getConfigName(), cl);
    }

    protected T getConfigInfo(String configName, Class<T> cl) throws ConfigException {
        return this.getConfigInfo(this.getStore(), configName, cl);
    }

    protected T getConfigInfo(ConfigurationStore cfs, String configName, Class<T> cl) throws ConfigException {
        try {
            return (T)cfs.getConfig(configName, cl);
        }
        catch (ConfigException cfe) {
            throw cfe;
        }
        catch (Throwable t) {
            throw new ConfigException(t);
        }
    }

    protected String loadConfig(Class<T> cl) {
        try {
            this.cfg = this.getConfigInfo(cl);
            if (this.cfg == null) {
                return "Unable to read configuration";
            }
            return "OK";
        }
        catch (Throwable t) {
            this.error("Failed to load configuration: " + t.getLocalizedMessage());
            this.error(t);
            return "failed";
        }
    }

    protected String loadOnlyConfig(Class<T> cl) {
        try {
            ConfigurationStore cs = this.getStore();
            List<String> configNames = cs.getConfigs();
            if (configNames.isEmpty()) {
                this.error("No configuration on path " + cs.getLocation());
                return "No configuration on path " + cs.getLocation();
            }
            if (configNames.size() != 1) {
                this.error("1 and only 1 configuration allowed");
                return "1 and only 1 configuration allowed";
            }
            String configName = configNames.iterator().next();
            this.cfg = this.getConfigInfo(cs, configName, cl);
            if (this.cfg == null) {
                this.error("Unable to read configuration");
                return "Unable to read configuration";
            }
            this.setConfigName(configName);
            return null;
        }
        catch (Throwable t) {
            this.error("Failed to load configuration: " + t.getLocalizedMessage());
            this.error(t);
            return "failed";
        }
    }

    protected void register(ObjectName key, Object bean) throws Exception {
        block2: {
            try {
                AnnotatedMBean.registerMBean(ConfBase.getManagementContext(), bean, key);
                this.getRegisteredMBeans().add(key);
            }
            catch (Throwable e) {
                this.warn("Failed to register MBean: " + key + ": " + e.getLocalizedMessage());
                if (!this.debug) break block2;
                this.error(e);
            }
        }
    }

    protected void unregister(ObjectName key) throws Exception {
        block3: {
            if (this.getRegisteredMBeans().remove(key)) {
                try {
                    ConfBase.getManagementContext().unregisterMBean(key);
                }
                catch (Throwable e) {
                    this.warn("Failed to unregister MBean: " + key);
                    if (!this.debug) break block3;
                    this.error(e);
                }
            }
        }
    }

    public static ManagementContext getManagementContext() {
        if (managementContext == null) {
            managementContext = new ManagementContext(ManagementContext.DEFAULT_DOMAIN);
        }
        return managementContext;
    }

    protected static Object makeObject(String className) {
        try {
            Object o = Class.forName(className).newInstance();
            if (o == null) {
                Logger.getLogger(ConfBase.class).error("Class " + className + " not found");
                return null;
            }
            return o;
        }
        catch (Throwable t) {
            Logger.getLogger(ConfBase.class).error("Unable to make object ", t);
            return null;
        }
    }

    static {
        ArrayList<String> hs = new ArrayList<String>();
        hs.add("http");
        hs.add("https");
        httpSchemes = Collections.unmodifiableList(hs);
        registeredMBeans = new CopyOnWriteArraySet<ObjectName>();
    }
}

