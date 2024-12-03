/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.bcel.classfile;

public class ClassFormatException
extends RuntimeException {
    private static final long serialVersionUID = 3243149520175287759L;

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

