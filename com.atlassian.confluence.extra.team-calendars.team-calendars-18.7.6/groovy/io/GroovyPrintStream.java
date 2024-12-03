/*
 * Decompiled with CFR 0.152.
 */
package groovy.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import org.codehaus.groovy.runtime.InvokerHelper;

public class GroovyPrintStream
extends PrintStream {
    public GroovyPrintStream(OutputStream out) {
        super(out, false);
    }

    public GroovyPrintStream(OutputStream out, boolean autoFlush) {
        super(out, autoFlush);
    }

    public GroovyPrintStream(OutputStream out, boolean autoFlush, String encoding) throws UnsupportedEncodingException {
        super(out, autoFlush, encoding);
    }

    public GroovyPrintStream(String fileName) throws FileNotFoundException {
        super(fileName);
    }

    public GroovyPrintStream(String fileName, String csn) throws FileNotFoundException, UnsupportedEncodingException {
        super(fileName, csn);
    }

    public GroovyPrintStream(File file) throws FileNotFoundException {
        super(file);
    }

    public GroovyPrintStream(File file, String csn) throws FileNotFoundException, UnsupportedEncodingException {
        super(file, csn);
    }

    @Override
    public void print(Object obj) {
        this.print(InvokerHelper.toString(obj));
    }

    @Override
    public void println(Object obj) {
        this.println(InvokerHelper.toString(obj));
    }
}

