/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2;

public class JDOMException
extends Exception {
    private static final long serialVersionUID = 200L;

    public JDOMException() {
        super("Error occurred in JDOM application.");
    }

    public JDOMException(String message) {
        super(message);
    }

    public JDOMException(String message, Throwable cause) {
        super(message, cause);
    }
}

