/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.conversion;

import org.apache.jackrabbit.spi.commons.conversion.NameException;

public class IllegalNameException
extends NameException {
    public IllegalNameException(String message) {
        super(message);
    }

    public IllegalNameException(String message, Throwable rootCause) {
        super(message, rootCause);
    }
}

