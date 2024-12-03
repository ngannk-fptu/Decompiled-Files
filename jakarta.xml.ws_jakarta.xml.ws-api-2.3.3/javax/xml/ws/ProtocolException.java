/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.ws;

import javax.xml.ws.WebServiceException;

public class ProtocolException
extends WebServiceException {
    public ProtocolException() {
    }

    public ProtocolException(String message) {
        super(message);
    }

    public ProtocolException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProtocolException(Throwable cause) {
        super(cause);
    }
}

