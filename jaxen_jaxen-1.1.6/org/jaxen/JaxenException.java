/*
 * Decompiled with CFR 0.152.
 */
package org.jaxen;

import org.jaxen.saxpath.SAXPathException;

public class JaxenException
extends SAXPathException {
    private static final long serialVersionUID = 7132891439526672639L;
    static double javaVersion = 1.4;

    public JaxenException(String message) {
        super(message);
    }

    public JaxenException(Throwable rootCause) {
        super(rootCause);
    }

    public JaxenException(String message, Throwable nestedException) {
        super(message, nestedException);
    }

    static {
        try {
            String versionString = System.getProperty("java.version");
            versionString = versionString.substring(0, 3);
            javaVersion = Double.valueOf(versionString);
        }
        catch (RuntimeException runtimeException) {
            // empty catch block
        }
    }
}

