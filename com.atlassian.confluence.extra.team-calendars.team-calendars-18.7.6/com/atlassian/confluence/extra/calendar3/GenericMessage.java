/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.Message
 */
package com.atlassian.confluence.extra.calendar3;

import com.atlassian.sal.api.message.Message;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class GenericMessage
implements Message {
    private final String key;
    private List<Serializable> substitutions;

    public GenericMessage(String key, Serializable ... substitutions) {
        this.key = key;
        this.substitutions = null != substitutions ? Arrays.asList(substitutions) : null;
    }

    public String getKey() {
        return this.key;
    }

    public Serializable[] getArguments() {
        return null == this.substitutions ? new Serializable[]{} : this.substitutions.toArray(new Serializable[this.substitutions.size()]);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        GenericMessage that = (GenericMessage)o;
        if (this.key != null ? !this.key.equals(that.key) : that.key != null) {
            return false;
        }
        return this.substitutions != null ? this.substitutions.equals(that.substitutions) : that.substitutions == null;
    }

    public int hashCode() {
        int result = this.key != null ? this.key.hashCode() : 0;
        result = 31 * result + (this.substitutions != null ? this.substitutions.hashCode() : 0);
        return result;
    }
}

