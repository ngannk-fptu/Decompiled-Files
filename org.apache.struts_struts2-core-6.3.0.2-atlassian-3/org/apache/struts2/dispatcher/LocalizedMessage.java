/*
 * Decompiled with CFR 0.152.
 */
package org.apache.struts2.dispatcher;

import java.util.Arrays;

public class LocalizedMessage {
    private final Class clazz;
    private final String textKey;
    private final String defaultMessage;
    private final Object[] args;

    public LocalizedMessage(Class clazz, String textKey, String defaultMessage, Object[] args) {
        this.clazz = clazz;
        this.textKey = textKey;
        this.defaultMessage = defaultMessage;
        this.args = args;
    }

    public Class getClazz() {
        return this.clazz;
    }

    public String getTextKey() {
        return this.textKey;
    }

    public String getDefaultMessage() {
        return this.defaultMessage;
    }

    public Object[] getArgs() {
        return this.args;
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + Arrays.hashCode(this.args);
        result = 31 * result + (this.clazz == null ? 0 : this.clazz.hashCode());
        result = 31 * result + (this.defaultMessage == null ? 0 : this.defaultMessage.hashCode());
        result = 31 * result + (this.textKey == null ? 0 : this.textKey.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        LocalizedMessage other = (LocalizedMessage)obj;
        if (!Arrays.equals(this.args, other.args)) {
            return false;
        }
        if (this.clazz == null ? other.clazz != null : !this.clazz.equals(other.clazz)) {
            return false;
        }
        if (this.defaultMessage == null ? other.defaultMessage != null : !this.defaultMessage.equals(other.defaultMessage)) {
            return false;
        }
        return !(this.textKey == null ? other.textKey != null : !this.textKey.equals(other.textKey));
    }
}

