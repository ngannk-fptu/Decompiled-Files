/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.threads;

public class StopPooledThreadException
extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public StopPooledThreadException(String msg) {
        super(msg, null, false, false);
    }
}

