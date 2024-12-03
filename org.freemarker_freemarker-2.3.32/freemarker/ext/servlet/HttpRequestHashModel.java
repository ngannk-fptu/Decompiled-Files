/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package freemarker.ext.servlet;

import freemarker.template.ObjectWrapper;
import freemarker.template.SimpleCollection;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateHashModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import java.util.ArrayList;
import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public final class HttpRequestHashModel
implements TemplateHashModelEx {
    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final ObjectWrapper wrapper;

    public HttpRequestHashModel(HttpServletRequest request, ObjectWrapper wrapper) {
        this(request, null, wrapper);
    }

    public HttpRequestHashModel(HttpServletRequest request, HttpServletResponse response, ObjectWrapper wrapper) {
        this.request = request;
        this.response = response;
        this.wrapper = wrapper;
    }

    @Override
    public TemplateModel get(String key) throws TemplateModelException {
        return this.wrapper.wrap(this.request.getAttribute(key));
    }

    @Override
    public boolean isEmpty() {
        return !this.request.getAttributeNames().hasMoreElements();
    }

    @Override
    public int size() {
        int result = 0;
        Enumeration enumeration = this.request.getAttributeNames();
        while (enumeration.hasMoreElements()) {
            enumeration.nextElement();
            ++result;
        }
        return result;
    }

    @Override
    public TemplateCollectionModel keys() {
        ArrayList keys = new ArrayList();
        Enumeration enumeration = this.request.getAttributeNames();
        while (enumeration.hasMoreElements()) {
            keys.add(enumeration.nextElement());
        }
        return new SimpleCollection(keys.iterator());
    }

    @Override
    public TemplateCollectionModel values() {
        ArrayList<Object> values = new ArrayList<Object>();
        Enumeration enumeration = this.request.getAttributeNames();
        while (enumeration.hasMoreElements()) {
            values.add(this.request.getAttribute((String)enumeration.nextElement()));
        }
        return new SimpleCollection(values.iterator(), this.wrapper);
    }

    public HttpServletRequest getRequest() {
        return this.request;
    }

    public HttpServletResponse getResponse() {
        return this.response;
    }

    public ObjectWrapper getObjectWrapper() {
        return this.wrapper;
    }
}

