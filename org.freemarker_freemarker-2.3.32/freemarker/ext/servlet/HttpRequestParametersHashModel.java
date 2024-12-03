/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package freemarker.ext.servlet;

import freemarker.template.SimpleCollection;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateHashModelEx;
import freemarker.template.TemplateModel;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

public class HttpRequestParametersHashModel
implements TemplateHashModelEx {
    private final HttpServletRequest request;
    private List keys;

    public HttpRequestParametersHashModel(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public TemplateModel get(String key) {
        String value = this.request.getParameter(key);
        return value == null ? null : new SimpleScalar(value);
    }

    @Override
    public boolean isEmpty() {
        return !this.request.getParameterNames().hasMoreElements();
    }

    @Override
    public int size() {
        return this.getKeys().size();
    }

    @Override
    public TemplateCollectionModel keys() {
        return new SimpleCollection(this.getKeys().iterator());
    }

    @Override
    public TemplateCollectionModel values() {
        final Iterator iter = this.getKeys().iterator();
        return new SimpleCollection(new Iterator(){

            @Override
            public boolean hasNext() {
                return iter.hasNext();
            }

            public Object next() {
                return HttpRequestParametersHashModel.this.request.getParameter((String)iter.next());
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        });
    }

    protected String transcode(String string) {
        return string;
    }

    private synchronized List getKeys() {
        if (this.keys == null) {
            this.keys = new ArrayList();
            Enumeration enumeration = this.request.getParameterNames();
            while (enumeration.hasMoreElements()) {
                this.keys.add(enumeration.nextElement());
            }
        }
        return this.keys;
    }
}

