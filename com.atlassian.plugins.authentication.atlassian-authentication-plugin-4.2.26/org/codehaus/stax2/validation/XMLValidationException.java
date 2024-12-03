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

    protected XMLValidationException(XMLValidationProblem xMLValidationProblem) {
        if (xMLValidationProblem == null) {
            XMLValidationException.throwMissing();
        }
        this.mCause = xMLValidationProblem;
    }

    protected XMLValidationException(XMLValidationProblem xMLValidationProblem, String string) {
        super(string);
        if (xMLValidationProblem == null) {
            XMLValidationException.throwMissing();
        }
        this.mCause = xMLValidationProblem;
    }

    protected XMLValidationException(XMLValidationProblem xMLValidationProblem, String string, Location location) {
        super(string, location);
        if (xMLValidationProblem == null) {
            XMLValidationException.throwMissing();
        }
        this.mCause = xMLValidationProblem;
    }

    public static XMLValidationException createException(XMLValidationProblem xMLValidationProblem) {
        String string = xMLValidationProblem.getMessage();
        if (string == null) {
            return new XMLValidationException(xMLValidationProblem);
        }
        Location location = xMLValidationProblem.getLocation();
        if (location == null) {
            return new XMLValidationException(xMLValidationProblem, string);
        }
        return new XMLValidationException(xMLValidationProblem, string, location);
    }

    public XMLValidationProblem getValidationProblem() {
        return this.mCause;
    }

    protected static void throwMissing() throws RuntimeException {
        throw new IllegalArgumentException("Validation problem argument can not be null");
    }
}

