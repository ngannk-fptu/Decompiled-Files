/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.model.property;

import java.io.Serializable;

public class PropertyId
implements Serializable {
    private String key;
    private String name;

    protected PropertyId() {
    }

    public PropertyId(String key, String name) {
        this.key = key;
        this.name = name;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PropertyId)) {
            return false;
        }
        PropertyId that = (PropertyId)o;
        if (this.key != null ? !this.key.equals(that.key) : that.key != null) {
            return false;
        }
        return !(this.name != null ? !this.name.equals(that.name) : that.name != null);
    }

    public int hashCode() {
        int result = this.key != null ? this.key.hashCode() : 0;
        result = 31 * result + (this.name != null ? this.name.hashCode() : 0);
        return result;
    }

    public String toString() {
        return "PropertyId{key='" + this.key + '\'' + ", name='" + this.name + '\'' + '}';
    }
}

