/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletException
 */
package org.springframework.web.multipart.support;

import javax.servlet.ServletException;

public class MissingServletRequestPartException
extends ServletException {
    private final String requestPartName;

    public MissingServletRequestPartException(String requestPartName) {
        super("Required request part '" + requestPartName + "' is not present");
        this.requestPartName = requestPartName;
    }

    public String getRequestPartName() {
        return this.requestPartName;
    }
}

