/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.security.persistence.dao.hibernate;

import java.io.Serializable;
import java.security.Key;

public class AliasedKey
implements Serializable {
    private long id;
    private String alias;
    private Key key;

    public long getId() {
        return this.id;
    }

    void setId(long id) {
        this.id = id;
    }

    public String getAlias() {
        return this.alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Key getKey() {
        return this.key;
    }

    public void setKey(Key key) {
        this.key = key;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AliasedKey)) {
            return false;
        }
        AliasedKey key1 = (AliasedKey)o;
        if (this.id != key1.id) {
            return false;
        }
        if (this.alias != null ? !this.alias.equals(key1.alias) : key1.alias != null) {
            return false;
        }
        return !(this.key != null ? !this.key.equals(key1.key) : key1.key != null);
    }

    public int hashCode() {
        int result = (int)(this.id ^ this.id >>> 32);
        result = 31 * result + (this.alias != null ? this.alias.hashCode() : 0);
        result = 31 * result + (this.key != null ? this.key.hashCode() : 0);
        return result;
    }
}

