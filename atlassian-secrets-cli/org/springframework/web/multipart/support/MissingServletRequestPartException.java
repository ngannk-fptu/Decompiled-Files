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
    private final String partName;

    public MissingServletRequestPartException(String partName) {
        super("Required request part '" + partName + "' is not present");
        this.partName = partName;
    }

    public String getRequestPartName() {
        return this.partName;
    }
}

