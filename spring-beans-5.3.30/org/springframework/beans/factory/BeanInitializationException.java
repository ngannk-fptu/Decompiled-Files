/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans.factory;

import org.springframework.beans.FatalBeanException;

public class BeanInitializationException
extends FatalBeanException {
    public BeanInitializationException(String msg) {
        super(msg);
    }

    public BeanInitializationException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

