/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonInclude
 *  com.fasterxml.jackson.annotation.JsonInclude$Include
 *  com.fasterxml.jackson.annotation.JsonProperty
 */
package com.atlassian.confluence.plugins.gatekeeper.model.space;

import com.atlassian.confluence.plugins.gatekeeper.model.Copiable;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TinySpace
implements Cloneable,
Copiable<TinySpace> {
    private String key;
    private String name;
    private boolean current = true;

    private TinySpace() {
    }

    public TinySpace(String key, String name) {
        this(key, name, true);
    }

    public TinySpace(String key, String name, boolean current) {
        this.key = key != null ? key.intern() : "(null key)";
        this.name = name != null ? name.intern() : "(empty name)";
        this.current = current;
    }

    @JsonProperty(value="k")
    public String getKey() {
        return this.key;
    }

    @JsonProperty(value="n")
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty(value="c")
    @JsonInclude(value=JsonInclude.Include.NON_DEFAULT)
    public boolean isCurrent() {
        return this.current;
    }

    public void setCurrent(boolean current) {
        this.current = current;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        TinySpace that = (TinySpace)o;
        return this.key.equals(that.key);
    }

    public int hashCode() {
        return this.key.hashCode();
    }

    public String toString() {
        return "TinySpace{key='" + this.key + "', name='" + this.name + "', current=" + this.current + "}";
    }

    @Override
    public TinySpace copy() {
        TinySpace result = null;
        try {
            result = (TinySpace)super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        return result;
    }
}

