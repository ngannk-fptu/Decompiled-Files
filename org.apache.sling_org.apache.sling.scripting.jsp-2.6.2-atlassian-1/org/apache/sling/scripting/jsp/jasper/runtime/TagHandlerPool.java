/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletConfig
 *  javax.servlet.jsp.JspException
 *  javax.servlet.jsp.tagext.Tag
 */
package org.apache.sling.scripting.jsp.jasper.runtime;

import javax.servlet.ServletConfig;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.sling.scripting.jsp.jasper.runtime.AnnotationHelper;
import org.apache.sling.scripting.jsp.jasper.runtime.AnnotationProcessor;

public class TagHandlerPool {
    private Tag[] handlers;
    public static String OPTION_TAGPOOL = "tagpoolClassName";
    public static String OPTION_MAXSIZE = "tagpoolMaxSize";
    private Log log = LogFactory.getLog(TagHandlerPool.class);
    private int current;
    protected AnnotationProcessor annotationProcessor = null;

    public static TagHandlerPool getTagHandlerPool(ServletConfig config) {
        TagHandlerPool result = new TagHandlerPool();
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
        this.annotationProcessor = (AnnotationProcessor)config.getServletContext().getAttribute(AnnotationProcessor.class.getName());
    }

    public TagHandlerPool() {
    }

    @Deprecated
    public TagHandlerPool(int capacity) {
        this.handlers = new Tag[capacity];
        this.current = -1;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Tag get(Class handlerClass) throws JspException {
        Tag handler = null;
        TagHandlerPool tagHandlerPool = this;
        synchronized (tagHandlerPool) {
            if (this.current >= 0) {
                handler = this.handlers[this.current--];
                return handler;
            }
        }
        try {
            Tag instance = (Tag)handlerClass.newInstance();
            AnnotationHelper.postConstruct(this.annotationProcessor, instance);
            return instance;
        }
        catch (Exception e) {
            throw new JspException(e.getMessage(), (Throwable)e);
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
        handler.release();
        if (this.annotationProcessor != null) {
            try {
                AnnotationHelper.preDestroy(this.annotationProcessor, handler);
            }
            catch (Exception e) {
                this.log.warn("Error processing preDestroy on tag instance of " + handler.getClass().getName(), e);
            }
        }
    }

    public synchronized void release() {
        for (int i = this.current; i >= 0; --i) {
            this.handlers[i].release();
            if (this.annotationProcessor == null) continue;
            try {
                AnnotationHelper.preDestroy(this.annotationProcessor, this.handlers[i]);
                continue;
            }
            catch (Exception e) {
                this.log.warn("Error processing preDestroy on tag instance of " + this.handlers[i].getClass().getName(), e);
            }
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

