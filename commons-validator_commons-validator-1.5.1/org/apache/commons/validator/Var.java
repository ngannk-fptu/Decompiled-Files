/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.validator;

import java.io.Serializable;

public class Var
implements Cloneable,
Serializable {
    private static final long serialVersionUID = -684185211548420224L;
    public static final String JSTYPE_INT = "int";
    public static final String JSTYPE_STRING = "string";
    public static final String JSTYPE_REGEXP = "regexp";
    private String name = null;
    private String value = null;
    private String jsType = null;
    private boolean resource = false;
    private String bundle = null;

    public Var() {
    }

    public Var(String name, String value, String jsType) {
        this.name = name;
        this.value = value;
        this.jsType = jsType;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isResource() {
        return this.resource;
    }

    public void setResource(boolean resource) {
        this.resource = resource;
    }

    public String getBundle() {
        return this.bundle;
    }

    public void setBundle(String bundle) {
        this.bundle = bundle;
    }

    public String getJsType() {
        return this.jsType;
    }

    public void setJsType(String jsType) {
        this.jsType = jsType;
    }

    public Object clone() {
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new RuntimeException(e.toString());
        }
    }

    public String toString() {
        StringBuilder results = new StringBuilder();
        results.append("Var: name=");
        results.append(this.name);
        results.append("  value=");
        results.append(this.value);
        results.append("  resource=");
        results.append(this.resource);
        if (this.resource) {
            results.append("  bundle=");
            results.append(this.bundle);
        }
        results.append("  jsType=");
        results.append(this.jsType);
        results.append("\n");
        return results.toString();
    }
}

