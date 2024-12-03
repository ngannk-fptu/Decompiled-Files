/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.bind;

import javax.xml.bind.JAXBException;

public class UnmarshalException
extends JAXBException {
    public UnmarshalException(String message) {
        this(message, null, null);
    }

    public UnmarshalException(String message, String errorCode) {
        this(message, errorCode, null);
    }

    public UnmarshalException(Throwable exception) {
        this(null, null, exception);
    }

    public UnmarshalException(String message, Throwable exception) {
        this(message, null, exception);
    }

    public UnmarshalException(String message, String errorCode, Throwable exception) {
        super(message, errorCode, exception);
    }
}

