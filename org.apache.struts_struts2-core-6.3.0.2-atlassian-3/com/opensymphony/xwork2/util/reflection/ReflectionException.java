/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.util.reflection;

import org.apache.struts2.StrutsException;

public class ReflectionException
extends StrutsException {
    public ReflectionException() {
    }

    public ReflectionException(String s) {
        super(s);
    }

    public ReflectionException(String s, Object target) {
        super(s, target);
    }

    public ReflectionException(Throwable cause) {
        super(cause);
    }

    public ReflectionException(Throwable cause, Object target) {
        super(cause, target);
    }

    public ReflectionException(String s, Throwable cause) {
        super(s, cause);
    }

    public ReflectionException(String s, Throwable cause, Object target) {
        super(s, cause, target);
    }
}

