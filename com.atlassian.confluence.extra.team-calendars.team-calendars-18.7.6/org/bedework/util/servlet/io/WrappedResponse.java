/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.http.HttpServletResponseWrapper
 */
package org.bedework.util.servlet.io;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import org.apache.log4j.Logger;

public class WrappedResponse
extends HttpServletResponseWrapper {
    protected boolean debug = false;
    private transient Logger log;

    public WrappedResponse(HttpServletResponse response) {
        this(response, null);
    }

    public WrappedResponse(HttpServletResponse response, Logger log) {
        super(response);
        this.log = log;
        this.debug = this.getLogger().isDebugEnabled();
    }

    public void sendError(int sc) throws IOException {
        super.sendError(sc);
        if (this.debug) {
            this.getLogger().debug("sendError(" + sc + ")");
        }
    }

    public void setStatus(int sc) {
        super.setStatus(sc);
        if (this.debug) {
            this.getLogger().debug("setStatus(" + sc + ")");
        }
    }

    public void addHeader(String name, String value) {
        super.addHeader(name, value);
        if (this.debug) {
            this.getLogger().debug("addHeader(\"" + name + "\", \"" + value + "\")");
        }
    }

    public void setHeader(String name, String value) {
        super.setHeader(name, value);
        if (this.debug) {
            this.getLogger().debug("setHeader(\"" + name + "\", \"" + value + "\")");
        }
    }

    public int getBufferSize() {
        return 0;
    }

    public void flushBuffer() {
        if (this.debug) {
            this.getLogger().debug("flushBuffer called");
        }
    }

    public void setContentType(String type) {
        this.getResponse().setContentType(type);
    }

    public void close() {
    }

    protected Logger getLogger() {
        if (this.log == null) {
            this.log = Logger.getLogger(((Object)((Object)this)).getClass());
        }
        return this.log;
    }
}

