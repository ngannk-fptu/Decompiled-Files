/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.exception;

import org.apache.velocity.exception.VelocityException;

public class MacroOverflowException
extends VelocityException {
    private static final long serialVersionUID = 7305635093478106342L;

    public MacroOverflowException(String exceptionMessage) {
        super(exceptionMessage);
    }

    public MacroOverflowException(String exceptionMessage, Throwable wrapped) {
        super(exceptionMessage, wrapped);
    }

    public MacroOverflowException(Throwable wrapped) {
        super(wrapped);
    }
}

