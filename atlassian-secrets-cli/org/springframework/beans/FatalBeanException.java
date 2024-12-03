/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans;

import org.springframework.beans.BeansException;
import org.springframework.lang.Nullable;

public class FatalBeanException
extends BeansException {
    public FatalBeanException(String msg) {
        super(msg);
    }

    public FatalBeanException(String msg, @Nullable Throwable cause) {
        super(msg, cause);
    }
}

