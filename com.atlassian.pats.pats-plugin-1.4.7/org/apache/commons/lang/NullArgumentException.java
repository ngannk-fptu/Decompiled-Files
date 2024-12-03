/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.lang;

public class NullArgumentException
extends IllegalArgumentException {
    private static final long serialVersionUID = 1174360235354917591L;

    public NullArgumentException(String argName) {
        super((argName == null ? "Argument" : argName) + " must not be null.");
    }
}

