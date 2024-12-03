/*
 * Decompiled with CFR 0.152.
 */
package org.apache.struts2.dispatcher.mapper;

import com.opensymphony.xwork2.Result;
import java.util.HashMap;
import java.util.Map;

public class ActionMapping {
    private String name;
    private String namespace;
    private String method;
    private String extension;
    private Map<String, Object> params;
    private Result result;

    public ActionMapping() {
        this.params = new HashMap<String, Object>();
    }

    public ActionMapping(Result result) {
        this.result = result;
    }

    public ActionMapping(String name, String namespace, String method, Map<String, Object> params) {
        this.name = name;
        this.namespace = namespace;
        this.method = method;
        this.params = params;
    }

    public String getName() {
        return this.name;
    }

    public String getNamespace() {
        return this.namespace;
    }

    public Map<String, Object> getParams() {
        return this.params;
    }

    public String getMethod() {
        if ("".equals(this.method)) {
            return null;
        }
        return this.method;
    }

    public Result getResult() {
        return this.result;
    }

    public String getExtension() {
        return this.extension;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String toString() {
        return "ActionMapping{name='" + this.name + '\'' + ", namespace='" + this.namespace + '\'' + ", method='" + this.method + '\'' + ", extension='" + this.extension + '\'' + ", params=" + this.params + ", result=" + (this.result != null ? this.result.getClass().getName() : "null") + '}';
    }
}

