/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.httpclient;

import java.io.Serializable;
import org.apache.commons.httpclient.util.LangUtils;

public class NameValuePair
implements Serializable {
    private String name = null;
    private String value = null;

    public NameValuePair() {
        this(null, null);
    }

    public NameValuePair(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public String toString() {
        return "name=" + this.name + ", " + "value=" + this.value;
    }

    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (this == object) {
            return true;
        }
        if (object instanceof NameValuePair) {
            NameValuePair that = (NameValuePair)object;
            return LangUtils.equals(this.name, that.name) && LangUtils.equals(this.value, that.value);
        }
        return false;
    }

    public int hashCode() {
        int hash = 17;
        hash = LangUtils.hashCode(hash, this.name);
        hash = LangUtils.hashCode(hash, this.value);
        return hash;
    }
}

