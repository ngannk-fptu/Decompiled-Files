/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.stax2.validation;

import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import org.codehaus.stax2.validation.XMLValidationProblem;

public class XMLValidationException
extends XMLStreamException {
    private static final long serialVersionUID = 1L;
    protected XMLValidationProblem mCause;

    protected XMLValidationException(XMLValidationProblem cause) {
        if (cause == null) {
            XMLValidationException.throwMissing();
        }
        this.mCause = cause;
    }

    protected XMLValidationException(XMLValidationProblem cause, String msg) {
        super(msg);
        if (cause == null) {
            XMLValidationException.throwMissing();
        }
        this.mCause = cause;
    }

    protected XMLValidationException(XMLValidationProblem cause, String msg, Location loc) {
        super(msg, loc);
        if (cause == null) {
            XMLValidationException.throwMissing();
        }
        this.mCause = cause;
    }

    public static XMLValidationException createException(XMLValidationProblem cause) {
        String msg = cause.getMessage();
        if (msg == null) {
            return new XMLValidationException(cause);
        }
        Location loc = cause.getLocation();
        if (loc == null) {
            return new XMLValidationException(cause, msg);
        }
        return new XMLValidationException(cause, msg, loc);
    }

    public XMLValidationProblem getValidationProblem() {
        return this.mCause;
    }

    protected static void throwMissing() throws RuntimeException {
        throw new IllegalArgumentException("Validation problem argument can not be null");
    }
}

