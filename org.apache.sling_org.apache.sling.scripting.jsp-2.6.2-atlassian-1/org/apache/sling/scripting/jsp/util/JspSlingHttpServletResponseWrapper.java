/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletOutputStream
 *  javax.servlet.jsp.JspWriter
 *  javax.servlet.jsp.PageContext
 *  org.apache.sling.api.wrappers.SlingHttpServletResponseWrapper
 */
package org.apache.sling.scripting.jsp.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import javax.servlet.ServletOutputStream;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import org.apache.sling.api.wrappers.SlingHttpServletResponseWrapper;
import org.apache.sling.scripting.jsp.util.TagUtil;

public class JspSlingHttpServletResponseWrapper
extends SlingHttpServletResponseWrapper {
    private JspWriter jspWriter;
    private PrintWriter printWriter;

    public JspSlingHttpServletResponseWrapper(PageContext pageContext) {
        super(TagUtil.getResponse(pageContext));
        this.jspWriter = pageContext.getOut();
        this.printWriter = new PrintWriter((Writer)this.jspWriter);
    }

    public PrintWriter getWriter() {
        return this.printWriter;
    }

    public ServletOutputStream getOutputStream() {
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

