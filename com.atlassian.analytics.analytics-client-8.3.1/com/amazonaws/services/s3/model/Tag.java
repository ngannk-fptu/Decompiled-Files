/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model;

import java.io.Serializable;

public class Tag
implements Serializable {
    private String key;
    private String value;

    public Tag(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Tag withKey(String key) {
        this.setKey(key);
        return this;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Tag withValue(String value) {
        this.setValue(value);
        return this;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Tag tag = (Tag)o;
        if (this.key != null ? !this.key.equals(tag.key) : tag.key != null) {
            return false;
        }
        return this.value != null ? this.value.equals(tag.value) : tag.value == null;
    }

    public int hashCode() {
        int result = this.key != null ? this.key.hashCode() : 0;
        result = 31 * result + (this.value != null ? this.value.hashCode() : 0);
        return result;
    }
}

