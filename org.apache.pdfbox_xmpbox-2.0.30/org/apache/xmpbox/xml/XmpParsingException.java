/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmpbox.xml;

public class XmpParsingException
extends Exception {
    private ErrorType errorType;
    private static final long serialVersionUID = -8843096358184702908L;

    public XmpParsingException(ErrorType error, String message, Throwable cause) {
        super(message, cause);
        this.errorType = error;
    }

    public XmpParsingException(ErrorType error, String message) {
        super(message);
        this.errorType = error;
    }

    public ErrorType getErrorType() {
        return this.errorType;
    }

    public static enum ErrorType {
        Undefined,
        Configuration,
        XpacketBadStart,
        XpacketBadEnd,
        NoRootElement,
        NoSchema,
        InvalidPdfaSchema,
        NoType,
        InvalidType,
        Format,
        NoValueType,
        RequiredProperty,
        InvalidPrefix;

    }
}

