/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.jmx.access;

import javax.management.JMRuntimeException;

public class InvalidInvocationException
extends JMRuntimeException {
    public InvalidInvocationException(String msg) {
        super(msg);
    }
}

