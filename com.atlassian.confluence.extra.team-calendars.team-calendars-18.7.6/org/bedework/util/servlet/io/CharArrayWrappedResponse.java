/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletOutputStream
 *  javax.servlet.http.HttpServletResponse
 */
package org.bedework.util.servlet.io;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.bedework.util.servlet.io.WrappedResponse;

public class CharArrayWrappedResponse
extends WrappedResponse {
    final CharArrayWriter caw = new CharArrayWriter();
    private boolean usedOutputStream;

    public CharArrayWrappedResponse(HttpServletResponse response) {
        super(response);
    }

    public CharArrayWrappedResponse(HttpServletResponse response, Logger log) {
        super(response, log);
    }

    public boolean getUsedOutputStream() {
        return this.usedOutputStream;
    }

    public PrintWriter getWriter() {
        if (this.debug) {
            this.getLogger().debug("getWriter called");
        }
        return new PrintWriter(this.caw);
    }

    public ServletOutputStream getOutputStream() throws IOException {
        if (this.debug) {
            this.getLogger().debug("getOutputStream called");
        }
        this.usedOutputStream = true;
        return this.getResponse().getOutputStream();
    }

    public String toString() {
        if (this.caw == null) {
            return null;
        }
        return this.caw.toString();
    }
}

