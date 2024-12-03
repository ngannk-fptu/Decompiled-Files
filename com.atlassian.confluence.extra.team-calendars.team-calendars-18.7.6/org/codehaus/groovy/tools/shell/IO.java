/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.fusesource.jansi.AnsiRenderWriter
 */
package org.codehaus.groovy.tools.shell;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import org.codehaus.groovy.tools.shell.util.Preferences;
import org.fusesource.jansi.AnsiRenderWriter;

public class IO
implements Closeable {
    public final InputStream inputStream;
    public final OutputStream outputStream;
    public final OutputStream errorStream;
    public final Reader in;
    public final PrintWriter out;
    public final PrintWriter err;

    public IO(InputStream inputStream, OutputStream outputStream, OutputStream errorStream) {
        assert (inputStream != null);
        assert (outputStream != null);
        assert (errorStream != null);
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.errorStream = errorStream;
        this.in = new InputStreamReader(inputStream);
        this.out = new AnsiRenderWriter(outputStream, true);
        this.err = new AnsiRenderWriter(errorStream, true);
    }

    public IO() {
        this(System.in, System.out, System.err);
    }

    public void setVerbosity(Verbosity verbosity) {
        assert (verbosity != null);
        Preferences.verbosity = verbosity;
    }

    public Verbosity getVerbosity() {
        return Preferences.verbosity;
    }

    public boolean isQuiet() {
        return this.getVerbosity() == Verbosity.QUIET;
    }

    public boolean isInfo() {
        return this.getVerbosity() == Verbosity.INFO;
    }

    public boolean isVerbose() {
        return this.getVerbosity() == Verbosity.VERBOSE;
    }

    public boolean isDebug() {
        return this.getVerbosity() == Verbosity.DEBUG;
    }

    public void flush() {
        this.out.flush();
        this.err.flush();
    }

    @Override
    public void close() throws IOException {
        this.in.close();
        this.out.close();
        this.err.close();
    }

    public static final class Verbosity {
        public static final Verbosity QUIET = new Verbosity("QUIET");
        public static final Verbosity INFO = new Verbosity("INFO");
        public static final Verbosity VERBOSE = new Verbosity("VERBOSE");
        public static final Verbosity DEBUG = new Verbosity("DEBUG");
        public final String name;

        private Verbosity(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }

        public static Verbosity forName(String name) {
            assert (name != null);
            if (Verbosity.QUIET.name.equalsIgnoreCase(name)) {
                return QUIET;
            }
            if (Verbosity.INFO.name.equalsIgnoreCase(name)) {
                return INFO;
            }
            if (Verbosity.VERBOSE.name.equalsIgnoreCase(name)) {
                return VERBOSE;
            }
            if (Verbosity.DEBUG.name.equalsIgnoreCase(name)) {
                return DEBUG;
            }
            throw new IllegalArgumentException("Invalid verbosity name: " + name);
        }
    }
}

