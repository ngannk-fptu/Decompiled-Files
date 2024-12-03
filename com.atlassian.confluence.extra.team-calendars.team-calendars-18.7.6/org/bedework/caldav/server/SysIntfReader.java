/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.caldav.server;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import org.apache.log4j.Logger;

public class SysIntfReader
extends Reader {
    private transient Logger log;
    private static volatile int objnum;
    private LineNumberReader lnr;
    private char[] curChars;
    private int len;
    private int pos;
    private boolean doneCr;
    private boolean doneLf = true;
    private boolean eof;
    private char nextChar;

    public SysIntfReader(Reader rdr) {
        this.lnr = new LineNumberReader(rdr);
        ++objnum;
    }

    @Override
    public int read() throws IOException {
        if (!this.getNextChar()) {
            return -1;
        }
        return this.nextChar;
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        int ct;
        if (this.eof) {
            return -1;
        }
        for (ct = 0; ct < len; ++ct) {
            if (!this.getNextChar()) {
                this.eof = true;
                return ct;
            }
            cbuf[off + ct] = this.nextChar;
        }
        return ct;
    }

    private boolean getNextChar() throws IOException {
        if (this.eof) {
            return false;
        }
        if (this.doneLf) {
            String ln = this.lnr.readLine();
            if (ln == null) {
                this.eof = true;
                return false;
            }
            if (this.getLogger().isDebugEnabled()) {
                this.trace(ln);
            }
            this.pos = 0;
            this.len = ln.length();
            this.curChars = ln.toCharArray();
            this.doneLf = false;
            this.doneCr = false;
        }
        if (this.pos == this.len) {
            if (!this.doneCr) {
                this.doneCr = true;
                this.nextChar = (char)13;
                return true;
            }
            this.doneLf = true;
            this.nextChar = (char)10;
            return true;
        }
        this.nextChar = this.curChars[this.pos];
        ++this.pos;
        return true;
    }

    @Override
    public void close() {
    }

    protected Logger getLogger() {
        if (this.log == null) {
            this.log = Logger.getLogger(this.getClass());
        }
        return this.log;
    }

    protected void trace(String msg) {
        this.getLogger().debug("[" + objnum + "] " + msg);
    }
}

