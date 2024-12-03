/*
 * Decompiled with CFR 0.152.
 */
package org.osgi.framework.hooks.weaving;

public class WeavingException
extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public WeavingException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public WeavingException(String msg) {
        super(msg);
    }
}

