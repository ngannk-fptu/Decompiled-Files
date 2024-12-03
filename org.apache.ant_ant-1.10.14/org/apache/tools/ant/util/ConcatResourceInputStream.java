/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.util.FileUtils;

public class ConcatResourceInputStream
extends InputStream {
    private static final int EOF = -1;
    private boolean eof = false;
    private Iterator<Resource> iter;
    private InputStream currentStream;
    private ProjectComponent managingPc;
    private boolean ignoreErrors = false;

    public ConcatResourceInputStream(ResourceCollection rc) {
        this.iter = rc.iterator();
    }

    public void setIgnoreErrors(boolean b) {
        this.ignoreErrors = b;
    }

    public boolean isIgnoreErrors() {
        return this.ignoreErrors;
    }

    @Override
    public void close() throws IOException {
        this.closeCurrent();
        this.eof = true;
    }

    @Override
    public int read() throws IOException {
        if (this.eof) {
            return -1;
        }
        int result = this.readCurrent();
        if (result == -1) {
            this.nextResource();
            result = this.readCurrent();
        }
        return result;
    }

    public void setManagingComponent(ProjectComponent pc) {
        this.managingPc = pc;
    }

    public void log(String message, int loglevel) {
        if (this.managingPc != null) {
            this.managingPc.log(message, loglevel);
        } else {
            (loglevel > 1 ? System.out : System.err).println(message);
        }
    }

    private int readCurrent() throws IOException {
        return this.eof || this.currentStream == null ? -1 : this.currentStream.read();
    }

    private void nextResource() throws IOException {
        this.closeCurrent();
        while (this.iter.hasNext()) {
            Resource r = this.iter.next();
            if (!r.isExists()) continue;
            this.log("Concatenating " + r.toLongString(), 3);
            try {
                this.currentStream = new BufferedInputStream(r.getInputStream());
                return;
            }
            catch (IOException eyeOhEx) {
                if (this.ignoreErrors) continue;
                this.log("Failed to get input stream for " + r, 0);
                throw eyeOhEx;
            }
        }
        this.eof = true;
    }

    private void closeCurrent() {
        FileUtils.close(this.currentStream);
        this.currentStream = null;
    }
}

