/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.filters;

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.util.FileUtils;

public abstract class BaseFilterReader
extends FilterReader {
    private static final int BUFFER_SIZE = 8192;
    private boolean initialized = false;
    private Project project = null;

    public BaseFilterReader() {
        super(new StringReader(""));
        FileUtils.close(this);
    }

    public BaseFilterReader(Reader in) {
        super(in);
    }

    @Override
    public final int read(char[] cbuf, int off, int len) throws IOException {
        for (int i = 0; i < len; ++i) {
            int ch = this.read();
            if (ch == -1) {
                if (i == 0) {
                    return -1;
                }
                return i;
            }
            cbuf[off + i] = (char)ch;
        }
        return len;
    }

    @Override
    public final long skip(long n) throws IOException, IllegalArgumentException {
        if (n < 0L) {
            throw new IllegalArgumentException("skip value is negative");
        }
        for (long i = 0L; i < n; ++i) {
            if (this.read() != -1) continue;
            return i;
        }
        return n;
    }

    protected final void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    protected final boolean getInitialized() {
        return this.initialized;
    }

    public final void setProject(Project project) {
        this.project = project;
    }

    protected final Project getProject() {
        return this.project;
    }

    protected final String readLine() throws IOException {
        int ch = this.in.read();
        if (ch == -1) {
            return null;
        }
        StringBuilder line = new StringBuilder();
        while (ch != -1) {
            line.append((char)ch);
            if (ch == 10) break;
            ch = this.in.read();
        }
        return line.toString();
    }

    protected final String readFully() throws IOException {
        return FileUtils.readFully(this.in, 8192);
    }
}

