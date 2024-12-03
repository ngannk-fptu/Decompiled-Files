/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  freemarker.template.ObjectWrapper
 *  freemarker.template.SimpleHash
 *  freemarker.template.TemplateModel
 *  freemarker.template.TemplateModelException
 *  javax.servlet.ServletContext
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpSession
 */
package org.apache.struts2.views.freemarker;

import com.opensymphony.xwork2.util.ValueStack;
import freemarker.template.ObjectWrapper;
import freemarker.template.SimpleHash;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class ScopesHashModel
extends SimpleHash
implements TemplateModel {
    private static final long serialVersionUID = 5551686380141886764L;
    private HttpServletRequest request;
    private ServletContext servletContext;
    private ValueStack stack;
    private final Map<String, TemplateModel> unlistedModels = new HashMap<String, TemplateModel>();
    private volatile Object parametersCache;

    public ScopesHashModel(ObjectWrapper objectWrapper, ServletContext context, HttpServletRequest request, ValueStack stack) {
        super(objectWrapper);
        this.servletContext = context;
        this.request = request;
        this.stack = stack;
    }

    public ScopesHashModel(ObjectWrapper objectWrapper, ServletContext context, HttpServletRequest request) {
        super(objectWrapper);
        this.servletContext = context;
        this.request = request;
    }

    public void putUnlistedModel(String key, TemplateModel model) {
        this.unlistedModels.put(key, model);
    }

    public TemplateModel get(String key) throws TemplateModelException {
        Object obj;
        TemplateModel model = super.get(key);
        if (model != null) {
            return model;
        }
        if (this.stack != null) {
            obj = this.findValueOnStack(key);
            if (obj != null) {
                return this.wrap(obj);
            }
            obj = this.stack.getContext().get(key);
            if (obj != null) {
                return this.wrap(obj);
            }
        }
        if (this.request != null) {
            obj = this.request.getAttribute(key);
            if (obj != null) {
                return this.wrap(obj);
            }
            HttpSession session = this.request.getSession(false);
            if (session != null && (obj = session.getAttribute(key)) != null) {
                return this.wrap(obj);
            }
        }
        if (this.servletContext != null && (obj = this.servletContext.getAttribute(key)) != null) {
            return this.wrap(obj);
        }
        model = this.unlistedModels.get(key);
        if (model != null) {
            return this.wrap(model);
        }
        return null;
    }

    private Object findValueOnStack(String key) {
        if ("parameters".equals(key)) {
            Object parametersLocal;
            if (this.parametersCache != null) {
                return this.parametersCache;
            }
            this.parametersCache = parametersLocal = this.stack.findValue(key);
            return parametersLocal;
        }
        return this.stack.findValue(key);
    }

    public void put(String string, boolean b) {
        super.put(string, b);
    }

    public void put(String string, Object object) {
        super.put(string, object);
    }
}

