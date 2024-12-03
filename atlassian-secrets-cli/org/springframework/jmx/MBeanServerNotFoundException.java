/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.jmx;

import org.springframework.jmx.JmxException;

public class MBeanServerNotFoundException
extends JmxException {
    public MBeanServerNotFoundException(String msg) {
        super(msg);
    }

    public MBeanServerNotFoundException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

