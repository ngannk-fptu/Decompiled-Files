/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletOutputStream
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.http.HttpServletResponseWrapper
 *  javax.servlet.jsp.JspWriter
 */
package org.apache.jasper.runtime;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.jsp.JspWriter;

public class ServletResponseWrapperInclude
extends HttpServletResponseWrapper {
    private final PrintWriter printWriter;
    private final JspWriter jspWriter;

    public ServletResponseWrapperInclude(ServletResponse response, JspWriter jspWriter) {
        super((HttpServletResponse)response);
        this.printWriter = new PrintWriter((Writer)jspWriter);
        this.jspWriter = jspWriter;
    }

    public PrintWriter getWriter() throws IOException {
        return this.printWriter;
    }

    public ServletOutputStream getOutputStream() throws IOException {
        throw new IllegalStateException();
    }

    public void resetBuffer() {
        try {
            this.jspWriter.clearBuffer();
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }
}

