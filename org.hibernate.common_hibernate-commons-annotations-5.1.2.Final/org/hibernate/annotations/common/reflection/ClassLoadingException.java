/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.annotations.common.reflection;

@Deprecated
public class ClassLoadingException
extends RuntimeException {
    public ClassLoadingException(String message) {
        super(message);
    }

    public ClassLoadingException(String message, Throwable cause) {
        super(message, cause);
    }
}

