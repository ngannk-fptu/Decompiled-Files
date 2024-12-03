/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.http.HttpServletResponseWrapper
 */
package com.sun.jersey.server.impl.container.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

class Wrapper
extends HttpServletResponseWrapper {
    private final PrintWriter pw;

    public Wrapper(HttpServletResponse httpServletResponse, PrintWriter w) {
        super(httpServletResponse);
        this.pw = w;
    }

    public PrintWriter getWriter() throws IOException {
        return this.pw;
    }
}

