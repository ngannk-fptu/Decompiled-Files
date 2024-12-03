/*
 * Decompiled with CFR 0.152.
 */
package org.owasp.validator.html;

public class ScanException
extends Exception {
    private static final long serialVersionUID = 1L;

    public ScanException(Exception e) {
        super(e);
    }

    public ScanException(String s) {
        super(s);
    }
}

