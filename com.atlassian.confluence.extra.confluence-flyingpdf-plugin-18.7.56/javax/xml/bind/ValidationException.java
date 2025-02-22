/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.bind;

import javax.xml.bind.JAXBException;

public class ValidationException
extends JAXBException {
    public ValidationException(String message) {
        this(message, null, null);
    }

    public ValidationException(String message, String errorCode) {
        this(message, errorCode, null);
    }

    public ValidationException(Throwable exception) {
        this(null, null, exception);
    }

    public ValidationException(String message, Throwable exception) {
        this(message, null, exception);
    }

    public ValidationException(String message, String errorCode, Throwable exception) {
        super(message, errorCode, exception);
    }
}

