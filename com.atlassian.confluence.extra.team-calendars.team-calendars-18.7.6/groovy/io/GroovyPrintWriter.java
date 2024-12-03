/*
 * Decompiled with CFR 0.152.
 */
package groovy.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import org.codehaus.groovy.runtime.InvokerHelper;

public class GroovyPrintWriter
extends PrintWriter {
    public GroovyPrintWriter(File file) throws FileNotFoundException {
        super(file);
    }

    public GroovyPrintWriter(File file, String csn) throws FileNotFoundException, UnsupportedEncodingException {
        super(file, csn);
    }

    public GroovyPrintWriter(Writer out) {
        super(out);
    }

    public GroovyPrintWriter(Writer out, boolean autoflush) {
        super(out, autoflush);
    }

    public GroovyPrintWriter(OutputStream out) {
        super(out);
    }

    public GroovyPrintWriter(OutputStream out, boolean autoflush) {
        super(out, autoflush);
    }

    public GroovyPrintWriter(String filename) throws FileNotFoundException {
        super(filename);
    }

    public GroovyPrintWriter(String filename, String csn) throws FileNotFoundException, UnsupportedEncodingException {
        super(filename, csn);
    }

    @Override
    public void print(Object x) {
        this.write(InvokerHelper.toString(x));
    }

    @Override
    public void println(Object x) {
        this.println(InvokerHelper.toString(x));
    }
}

