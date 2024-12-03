/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.bind;

import org.springframework.web.util.NestedServletException;

public class ServletRequestBindingException
extends NestedServletException {
    public ServletRequestBindingException(String msg) {
        super(msg);
    }

    public ServletRequestBindingException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

