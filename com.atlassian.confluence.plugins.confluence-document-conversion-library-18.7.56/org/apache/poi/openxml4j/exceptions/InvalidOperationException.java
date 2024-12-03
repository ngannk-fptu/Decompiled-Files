/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.openxml4j.exceptions;

import org.apache.poi.openxml4j.exceptions.OpenXML4JRuntimeException;

public class InvalidOperationException
extends OpenXML4JRuntimeException {
    public InvalidOperationException(String message) {
        super(message);
    }

    public InvalidOperationException(String message, Throwable reason) {
        super(message, reason);
    }
}

