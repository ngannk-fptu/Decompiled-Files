/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.atlassian.crowd.model.authentication;

import com.google.common.base.Preconditions;
import java.io.Serializable;

public class ValidationFactor
implements Serializable {
    public static final String REMOTE_ADDRESS = "remote_address";
    public static final String REMOTE_HOST = "remote_host";
    public static final String X_FORWARDED_FOR = "X-Forwarded-For";
    public static final String RANDOM_NUMBER = "Random-Number";
    public static final String NAME = "NAME";
    public static final String PRIVILEGE_LEVEL = "PRIVILEGE_LEVEL";
    private String name;
    private String value;

    public ValidationFactor() {
    }

    public ValidationFactor(String name, String value) {
        this.name = (String)Preconditions.checkNotNull((Object)name);
        this.value = (String)Preconditions.checkNotNull((Object)value);
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = (String)Preconditions.checkNotNull((Object)name);
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = (String)Preconditions.checkNotNull((Object)value);
    }

    public String toString() {
        return "ValidationFactor[" + this.name + "=" + this.value + "]";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ValidationFactor)) {
            return false;
        }
        ValidationFactor that = (ValidationFactor)o;
        if (!this.name.equals(that.name)) {
            return false;
        }
        return this.value.equals(that.value);
    }

    public int hashCode() {
        int result = this.name.hashCode();
        result = 31 * result + this.value.hashCode();
        return result;
    }
}

