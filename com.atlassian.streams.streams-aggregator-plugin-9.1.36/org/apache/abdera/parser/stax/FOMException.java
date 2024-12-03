/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.parser.stax;

import org.apache.abdera.parser.ParseException;

public class FOMException
extends ParseException {
    private static final long serialVersionUID = 7631230122836829559L;

    public FOMException() {
    }

    public FOMException(String message) {
        super(message);
    }

    public FOMException(String message, Throwable cause) {
        super(message, cause);
    }

    public FOMException(Throwable cause) {
        super(cause);
    }
}

