/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.classfile;

public class ClassFormatException
extends RuntimeException {
    private static final long serialVersionUID = -3569097343160139969L;

    public ClassFormatException() {
    }

    public ClassFormatException(String message) {
        super(message);
    }

    public ClassFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    public ClassFormatException(Throwable cause) {
        super(cause);
    }
}

