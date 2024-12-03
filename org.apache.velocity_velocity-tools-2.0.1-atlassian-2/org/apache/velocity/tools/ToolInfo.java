/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.beanutils.PropertyUtils
 */
package org.apache.velocity.tools;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.velocity.tools.ClassUtils;
import org.apache.velocity.tools.config.SkipSetters;

public class ToolInfo
implements Serializable {
    private static final long serialVersionUID = -8145087882015742757L;
    public static final String CONFIGURE_METHOD_NAME = "configure";
    private String key;
    private Class clazz;
    private boolean restrictToIsExact;
    private String restrictTo;
    private Map<String, Object> properties;
    private Boolean skipSetters;
    private transient Method configure = null;

    public ToolInfo(String key, Class clazz) {
        this.setKey(key);
        this.setClass(clazz);
    }

    public void setKey(String key) {
        this.key = key;
        if (this.key == null) {
            throw new NullPointerException("Key cannot be null");
        }
    }

    public void setClass(Class clazz) {
        if (clazz == null) {
            throw new NullPointerException("Tool class must not be null");
        }
        this.clazz = clazz;
    }

    public void restrictTo(String path) {
        if (path != null && !path.startsWith("/")) {
            path = "/" + path;
        }
        if (path == null || path.equals("*")) {
            this.restrictToIsExact = false;
            this.restrictTo = null;
        } else if (path.endsWith("*")) {
            this.restrictToIsExact = false;
            this.restrictTo = path.substring(0, path.length() - 1);
        } else {
            this.restrictToIsExact = true;
            this.restrictTo = path;
        }
    }

    public void setSkipSetters(boolean cfgOnly) {
        this.skipSetters = cfgOnly;
    }

    public void addProperties(Map<String, Object> parentProps) {
        Map<String, Object> properties = this.getProps();
        for (Map.Entry<String, Object> prop : parentProps.entrySet()) {
            if (properties.containsKey(prop.getKey())) continue;
            properties.put(prop.getKey(), prop.getValue());
        }
    }

    public Object putProperty(String name, Object value) {
        return this.getProps().put(name, value);
    }

    protected synchronized Map<String, Object> getProps() {
        if (this.properties == null) {
            this.properties = new HashMap<String, Object>();
        }
        return this.properties;
    }

    public String getKey() {
        return this.key;
    }

    public String getClassname() {
        return this.clazz.getName();
    }

    public Class getToolClass() {
        return this.clazz;
    }

    public Map<String, Object> getProperties() {
        return this.getProps();
    }

    public boolean hasConfigure() {
        return this.getConfigure() != null;
    }

    public boolean isSkipSetters() {
        if (this.skipSetters == null) {
            this.skipSetters = this.clazz.getAnnotation(SkipSetters.class) != null;
        }
        return this.skipSetters;
    }

    public boolean hasPermission(String path) {
        if (this.restrictTo == null) {
            return true;
        }
        if (this.restrictToIsExact) {
            return this.restrictTo.equals(path);
        }
        if (path != null) {
            return path.startsWith(this.restrictTo);
        }
        return false;
    }

    public Object create(Map<String, Object> dynamicProperties) {
        Object tool = this.newInstance();
        Map<String, Object> props = this.properties == null ? dynamicProperties : this.combine(dynamicProperties, this.properties);
        this.configure(tool, props);
        return tool;
    }

    protected void configure(Object tool, Map<String, Object> configuration) {
        if (!this.isSkipSetters() && configuration != null) {
            try {
                for (Map.Entry<String, Object> conf : configuration.entrySet()) {
                    this.setProperty(tool, conf.getKey(), conf.getValue());
                }
            }
            catch (RuntimeException re) {
                throw re;
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        if (this.hasConfigure()) {
            this.invoke(this.getConfigure(), tool, configuration);
        }
    }

    protected Method getConfigure() {
        if (this.configure == null) {
            try {
                this.configure = ClassUtils.findMethod(this.clazz, CONFIGURE_METHOD_NAME, new Class[]{Map.class});
            }
            catch (SecurityException se) {
                String msg = "Unable to gain access to 'configure(Map)' method for '" + this.clazz.getName() + "' under the current security manager.  This tool cannot be properly configured for use.";
                throw new IllegalStateException(msg, se);
            }
        }
        return this.configure;
    }

    protected Object newInstance() {
        try {
            return this.clazz.newInstance();
        }
        catch (IllegalAccessException iae) {
            String message = "Unable to instantiate instance of \"" + this.getClassname() + "\"";
            throw new IllegalStateException(message, iae);
        }
        catch (InstantiationException ie) {
            String message = "Exception while instantiating instance of \"" + this.getClassname() + "\"";
            throw new IllegalStateException(message, ie);
        }
    }

    protected void invoke(Method method, Object tool, Object param) {
        try {
            method.invoke(tool, param);
        }
        catch (IllegalAccessException iae) {
            String msg = "Unable to invoke " + method + " on " + tool;
            throw new IllegalStateException(msg, iae);
        }
        catch (InvocationTargetException ite) {
            String msg = "Exception when invoking " + method + " on " + tool;
            throw new RuntimeException(msg, ite.getCause());
        }
    }

    protected void setProperty(Object tool, String name, Object value) throws Exception {
        if (PropertyUtils.isWriteable((Object)tool, (String)name)) {
            PropertyUtils.setProperty((Object)tool, (String)name, (Object)value);
        }
    }

    protected Map<String, Object> combine(Map<String, Object> ... maps) {
        HashMap<String, Object> combined = new HashMap<String, Object>();
        for (Map<String, Object> map : maps) {
            if (map == null) continue;
            combined.putAll(map);
        }
        return combined;
    }
}

