/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletConfig
 *  javax.servlet.jsp.JspException
 *  javax.servlet.jsp.tagext.Tag
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.InstanceManager
 */
package org.apache.jasper.runtime;

import javax.servlet.ServletConfig;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;
import org.apache.jasper.Constants;
import org.apache.jasper.compiler.Localizer;
import org.apache.jasper.runtime.ExceptionUtils;
import org.apache.jasper.runtime.InstanceManagerFactory;
import org.apache.jasper.runtime.JspRuntimeLibrary;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.InstanceManager;

public class TagHandlerPool {
    private Tag[] handlers;
    public static final String OPTION_TAGPOOL = "tagpoolClassName";
    public static final String OPTION_MAXSIZE = "tagpoolMaxSize";
    private int current;
    protected InstanceManager instanceManager = null;

    public static TagHandlerPool getTagHandlerPool(ServletConfig config) {
        TagHandlerPool result = null;
        String tpClassName = TagHandlerPool.getOption(config, OPTION_TAGPOOL, null);
        if (tpClassName != null) {
            try {
                Class<?> c = Class.forName(tpClassName);
                result = (TagHandlerPool)c.getConstructor(new Class[0]).newInstance(new Object[0]);
            }
            catch (Exception e) {
                LogFactory.getLog(TagHandlerPool.class).info((Object)Localizer.getMessage("jsp.error.tagHandlerPool"), (Throwable)e);
                result = null;
            }
        }
        if (result == null) {
            result = new TagHandlerPool();
        }
        result.init(config);
        return result;
    }

    protected void init(ServletConfig config) {
        int maxSize = -1;
        String maxSizeS = TagHandlerPool.getOption(config, OPTION_MAXSIZE, null);
        if (maxSizeS != null) {
            try {
                maxSize = Integer.parseInt(maxSizeS);
            }
            catch (Exception ex) {
                maxSize = -1;
            }
        }
        if (maxSize < 0) {
            maxSize = 5;
        }
        this.handlers = new Tag[maxSize];
        this.current = -1;
        this.instanceManager = InstanceManagerFactory.getInstanceManager(config);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Tag get(Class<? extends Tag> handlerClass) throws JspException {
        TagHandlerPool tagHandlerPool = this;
        synchronized (tagHandlerPool) {
            if (this.current >= 0) {
                Tag handler = this.handlers[this.current--];
                return handler;
            }
        }
        try {
            if (Constants.USE_INSTANCE_MANAGER_FOR_TAGS) {
                return (Tag)this.instanceManager.newInstance(handlerClass.getName(), handlerClass.getClassLoader());
            }
            Tag instance = handlerClass.getConstructor(new Class[0]).newInstance(new Object[0]);
            this.instanceManager.newInstance((Object)instance);
            return instance;
        }
        catch (Exception e) {
            Throwable t = ExceptionUtils.unwrapInvocationTargetException(e);
            ExceptionUtils.handleThrowable(t);
            throw new JspException(e.getMessage(), t);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void reuse(Tag handler) {
        TagHandlerPool tagHandlerPool = this;
        synchronized (tagHandlerPool) {
            if (this.current < this.handlers.length - 1) {
                this.handlers[++this.current] = handler;
                return;
            }
        }
        JspRuntimeLibrary.releaseTag(handler, this.instanceManager);
    }

    public synchronized void release() {
        for (int i = this.current; i >= 0; --i) {
            JspRuntimeLibrary.releaseTag(this.handlers[i], this.instanceManager);
        }
    }

    protected static String getOption(ServletConfig config, String name, String defaultV) {
        if (config == null) {
            return defaultV;
        }
        String value = config.getInitParameter(name);
        if (value != null) {
            return value;
        }
        if (config.getServletContext() == null) {
            return defaultV;
        }
        value = config.getServletContext().getInitParameter(name);
        if (value != null) {
            return value;
        }
        return defaultV;
    }
}

