/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.Message
 */
package com.atlassian.sal.core.message;

import com.atlassian.sal.api.message.Message;
import java.io.Serializable;
import java.util.Arrays;

public class DefaultMessage
implements Message {
    private final Serializable[] arguments;
    private String key;

    public DefaultMessage(String key, Serializable ... arguments) {
        this.key = key;
        this.arguments = arguments;
    }

    public Serializable[] getArguments() {
        return this.arguments;
    }

    public String getKey() {
        return this.key;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(this.key);
        builder.append(": ");
        for (Serializable argument : this.arguments) {
            builder.append(argument);
            builder.append(",");
        }
        return builder.toString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DefaultMessage)) {
            return false;
        }
        DefaultMessage that = (DefaultMessage)o;
        if (!Arrays.equals(this.arguments, that.arguments)) {
            return false;
        }
        return !(this.key != null ? !this.key.equals(that.key) : that.key != null);
    }

    public int hashCode() {
        int result = this.arguments != null ? Arrays.hashCode(this.arguments) : 0;
        result = 31 * result + (this.key != null ? this.key.hashCode() : 0);
        return result;
    }
}

