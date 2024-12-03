/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletRequest
 */
package org.apache.velocity.tools.view;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletRequest;
import org.apache.velocity.tools.config.DefaultKey;
import org.apache.velocity.tools.config.ValidScope;
import org.apache.velocity.tools.generic.ValueParser;

@DefaultKey(value="params")
@ValidScope(value={"request"})
public class ParameterTool
extends ValueParser {
    private ServletRequest request;

    public ParameterTool() {
    }

    public ParameterTool(ServletRequest request) {
        this.setRequest(request);
    }

    @Override
    protected void configure(ValueParser values) {
        super.configure(values);
        ServletRequest req = (ServletRequest)values.getValue("request");
        this.setRequest(req);
    }

    public void setRequest(ServletRequest request) {
        this.request = request;
    }

    protected ServletRequest getRequest() {
        if (this.request == null) {
            throw new UnsupportedOperationException("Request is null. ParameterTool must be initialized first!");
        }
        return this.request;
    }

    @Override
    public Object getValue(String key) {
        return this.getRequest().getParameter(key);
    }

    @Override
    public Object[] getValues(String key) {
        Object[] strings = this.getRequest().getParameterValues(key);
        if (strings == null || strings.length == 0) {
            return null;
        }
        if (strings.length == 1) {
            return this.parseStringList((String)strings[0]);
        }
        return strings;
    }

    protected void setSource(Map source) {
        throw new UnsupportedOperationException();
    }

    protected Map getSource() {
        Map<String, Object> source = super.getSource();
        if (source == null) {
            source = this.expandSingletonArrays(this.getRequest().getParameterMap());
            super.setSource(source);
        }
        return source;
    }

    public Map getAll() {
        return this.getSource();
    }

    private boolean isSingletonArray(Object value) {
        return value != null && value.getClass().isArray() && Array.getLength(value) == 1;
    }

    private Map<String, Object> expandSingletonArrays(Map<String, Object> original) {
        HashMap<String, Object> expanded = new HashMap<String, Object>(original);
        for (Map.Entry entry : expanded.entrySet()) {
            Object value = entry.getValue();
            if (!this.isSingletonArray(value)) continue;
            entry.setValue(Array.get(value, 0));
        }
        return expanded;
    }
}

