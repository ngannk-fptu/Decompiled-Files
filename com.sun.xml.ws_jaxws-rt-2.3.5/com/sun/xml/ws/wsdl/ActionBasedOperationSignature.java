/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 */
package com.sun.xml.ws.wsdl;

import com.sun.istack.NotNull;
import javax.xml.namespace.QName;

public class ActionBasedOperationSignature {
    private final String action;
    private final QName payloadQName;

    public ActionBasedOperationSignature(@NotNull String action, @NotNull QName payloadQName) {
        this.action = action;
        this.payloadQName = payloadQName;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ActionBasedOperationSignature that = (ActionBasedOperationSignature)o;
        if (!this.action.equals(that.action)) {
            return false;
        }
        return this.payloadQName.equals(that.payloadQName);
    }

    public int hashCode() {
        int result = this.action.hashCode();
        result = 31 * result + this.payloadQName.hashCode();
        return result;
    }
}

