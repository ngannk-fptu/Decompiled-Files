/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.conversion;

import org.apache.jackrabbit.spi.commons.conversion.NameException;

public class MalformedPathException
extends NameException {
    public MalformedPathException(String message) {
        super(message);
    }

    public MalformedPathException(String message, Throwable rootCause) {
        super(message, rootCause);
    }
}

