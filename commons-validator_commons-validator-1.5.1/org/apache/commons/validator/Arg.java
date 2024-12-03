/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.validator;

import java.io.Serializable;

public class Arg
implements Cloneable,
Serializable {
    private static final long serialVersionUID = -8922606779669839294L;
    protected String bundle = null;
    protected String key = null;
    protected String name = null;
    protected int position = -1;
    protected boolean resource = true;

    public Object clone() {
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new RuntimeException(e.toString());
        }
    }

    public String getBundle() {
        return this.bundle;
    }

    public String getKey() {
        return this.key;
    }

    public String getName() {
        return this.name;
    }

    public int getPosition() {
        return this.position;
    }

    public boolean isResource() {
        return this.resource;
    }

    public void setBundle(String bundle) {
        this.bundle = bundle;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setResource(boolean resource) {
        this.resource = resource;
    }

    public String toString() {
        StringBuilder results = new StringBuilder();
        results.append("Arg: name=");
        results.append(this.name);
        results.append("  key=");
        results.append(this.key);
        results.append("  position=");
        results.append(this.position);
        results.append("  bundle=");
        results.append(this.bundle);
        results.append("  resource=");
        results.append(this.resource);
        results.append("\n");
        return results.toString();
    }
}

