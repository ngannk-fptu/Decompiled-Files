/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletException
 *  org.springframework.core.NestedExceptionUtils
 *  org.springframework.lang.Nullable
 */
package org.springframework.web.util;

import javax.servlet.ServletException;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.lang.Nullable;

public class NestedServletException
extends ServletException {
    private static final long serialVersionUID = -5292377985529381145L;

    public NestedServletException(String msg) {
        super(msg);
    }

    public NestedServletException(@Nullable String msg, @Nullable Throwable cause) {
        super(msg, cause);
    }

    @Nullable
    public String getMessage() {
        return NestedExceptionUtils.buildMessage((String)super.getMessage(), (Throwable)this.getCause());
    }

    static {
        NestedExceptionUtils.class.getName();
    }
}

