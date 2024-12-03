/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletException
 */
package org.springframework.web;

import javax.servlet.ServletException;
import org.springframework.lang.Nullable;

public class HttpSessionRequiredException
extends ServletException {
    @Nullable
    private String expectedAttribute;

    public HttpSessionRequiredException(String msg) {
        super(msg);
    }

    public HttpSessionRequiredException(String msg, String expectedAttribute) {
        super(msg);
        this.expectedAttribute = expectedAttribute;
    }

    @Nullable
    public String getExpectedAttribute() {
        return this.expectedAttribute;
    }
}

