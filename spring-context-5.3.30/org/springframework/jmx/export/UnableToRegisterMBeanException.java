/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.jmx.export;

import org.springframework.jmx.export.MBeanExportException;

public class UnableToRegisterMBeanException
extends MBeanExportException {
    public UnableToRegisterMBeanException(String msg) {
        super(msg);
    }

    public UnableToRegisterMBeanException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

