/*
 * Decompiled with CFR 0.152.
 */
package org.owasp.validator.html;

public class PolicyException
extends Exception {
    private static final long serialVersionUID = 1L;

    public PolicyException(Exception e) {
        super(e);
    }

    public PolicyException(String string) {
        super(string);
    }
}

