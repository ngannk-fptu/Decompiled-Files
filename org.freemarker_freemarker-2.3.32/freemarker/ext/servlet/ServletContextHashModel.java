/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.GenericServlet
 *  javax.servlet.ServletContext
 */
package freemarker.ext.servlet;

import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import javax.servlet.GenericServlet;
import javax.servlet.ServletContext;

public final class ServletContextHashModel
implements TemplateHashModel {
    private final GenericServlet servlet;
    private final ServletContext servletctx;
    private final ObjectWrapper wrapper;

    public ServletContextHashModel(GenericServlet servlet, ObjectWrapper wrapper) {
        this.servlet = servlet;
        this.servletctx = servlet.getServletContext();
        this.wrapper = wrapper;
    }

    @Deprecated
    public ServletContextHashModel(ServletContext servletctx, ObjectWrapper wrapper) {
        this.servlet = null;
        this.servletctx = servletctx;
        this.wrapper = wrapper;
    }

    @Override
    public TemplateModel get(String key) throws TemplateModelException {
        return this.wrapper.wrap(this.servletctx.getAttribute(key));
    }

    @Override
    public boolean isEmpty() {
        return !this.servletctx.getAttributeNames().hasMoreElements();
    }

    public GenericServlet getServlet() {
        return this.servlet;
    }
}

