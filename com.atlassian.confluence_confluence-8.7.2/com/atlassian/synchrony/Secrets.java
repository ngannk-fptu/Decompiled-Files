/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.synchrony;

import java.util.Objects;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class Secrets {
    private String key;
    private String value;

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Secrets secrets = (Secrets)o;
        return Objects.equals(this.key, secrets.key) && Objects.equals(this.value, secrets.value);
    }

    public int hashCode() {
        return Objects.hash(this.key, this.value);
    }

    public String toString() {
        return new ToStringBuilder((Object)this).append("key", (Object)this.key).append("value", (Object)this.value).toString();
    }
}

