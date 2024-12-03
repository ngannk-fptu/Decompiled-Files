/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.velocity.tools.view;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.tools.view.ToolInfo;

@Deprecated
public class ViewToolInfo
implements ToolInfo {
    protected static final Log LOG = LogFactory.getLog(ViewToolInfo.class);
    private String key;
    private Class clazz;
    private Map parameters;
    private Method init = null;
    private Method configure = null;

    protected Class getApplicationClass(String name) throws ClassNotFoundException {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (loader == null) {
            loader = ViewToolInfo.class.getClassLoader();
        }
        return loader.loadClass(name);
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setClassname(String classname) throws Exception {
        if (classname != null && classname.length() != 0) {
            this.clazz = this.getApplicationClass(classname);
            this.clazz.newInstance();
            try {
                this.init = this.clazz.getMethod("init", Object.class);
            }
            catch (NoSuchMethodException noSuchMethodException) {
                // empty catch block
            }
            try {
                this.configure = this.clazz.getMethod("configure", Map.class);
            }
            catch (NoSuchMethodException noSuchMethodException) {}
        } else {
            this.clazz = null;
        }
    }

    public void setParameters(Map parameters) {
        this.parameters = parameters;
    }

    public void setParameter(String name, String value) {
        if (this.parameters == null) {
            this.parameters = new HashMap();
        }
        this.parameters.put(name, value);
    }

    @Override
    public String getKey() {
        return this.key;
    }

    @Override
    public String getClassname() {
        return this.clazz != null ? this.clazz.getName() : null;
    }

    public Map getParameters() {
        return this.parameters;
    }

    @Override
    public Object getInstance(Object initData) {
        if (this.clazz == null) {
            LOG.error((Object)("Tool " + this.key + " has no Class definition!"));
            return null;
        }
        Object tool = null;
        try {
            tool = this.clazz.newInstance();
        }
        catch (IllegalAccessException e) {
            LOG.error((Object)("Exception while instantiating instance of \"" + this.getClassname() + "\""), (Throwable)e);
        }
        catch (InstantiationException e) {
            LOG.error((Object)("Exception while instantiating instance of \"" + this.getClassname() + "\""), (Throwable)e);
        }
        if (this.configure != null && this.parameters != null) {
            try {
                this.configure.invoke(tool, this.parameters);
            }
            catch (IllegalAccessException iae) {
                LOG.error((Object)("Exception when calling configure(Map) on " + tool), (Throwable)iae);
            }
            catch (InvocationTargetException ite) {
                LOG.error((Object)("Exception when calling configure(Map) on " + tool), (Throwable)ite);
            }
        }
        if (this.init != null) {
            try {
                this.init.invoke(tool, initData);
            }
            catch (IllegalAccessException iae) {
                LOG.error((Object)("Exception when calling init(Object) on " + tool), (Throwable)iae);
            }
            catch (InvocationTargetException ite) {
                LOG.error((Object)("Exception when calling init(Object) on " + tool), (Throwable)ite);
            }
        }
        return tool;
    }
}

