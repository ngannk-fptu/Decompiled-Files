/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.protocol.error;

import org.apache.abdera.Abdera;
import org.apache.abdera.protocol.error.Error;

public class ProtocolException
extends RuntimeException {
    private static final long serialVersionUID = 1017447143200419489L;
    private final Error error;

    public ProtocolException(Error error) {
        super(error.getCode() + "::" + error.getMessage());
        this.error = error;
    }

    public ProtocolException(Abdera abdera, int code, String message) {
        super(code + "::" + message);
        this.error = Error.create(abdera, code, message);
    }

    public Error getError() {
        return this.error;
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        String message = this.error != null ? this.error.getMessage() : null;
        int code = this.error != null ? this.error.getCode() : 0;
        result = 31 * result + (message == null ? 0 : message.hashCode());
        result = 31 * result + code;
        return result;
    }

    public boolean equals(Object obj) {
        int ocode;
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        ProtocolException other = (ProtocolException)obj;
        String message = this.error != null ? this.error.getMessage() : null;
        int code = this.error != null ? this.error.getCode() : 0;
        String omessage = other.error != null ? other.error.getMessage() : null;
        int n = ocode = other.error != null ? other.error.getCode() : 0;
        if (message == null ? omessage != null : !message.equals(omessage)) {
            return false;
        }
        return code == ocode;
    }
}

