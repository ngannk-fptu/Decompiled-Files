/*
 * Decompiled with CFR 0.152.
 */
package groovy.ui;

import groovy.lang.Closure;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class SystemOutputInterceptor
extends FilterOutputStream {
    private Closure callback;
    private boolean output;
    private static final ThreadLocal<Integer> consoleId = new InheritableThreadLocal<Integer>(){

        @Override
        protected Integer initialValue() {
            return 0;
        }
    };

    public SystemOutputInterceptor(Closure callback) {
        this(callback, true);
    }

    public SystemOutputInterceptor(Closure callback, boolean output) {
        super(output ? System.out : System.err);
        assert (callback != null);
        this.callback = callback;
        this.output = output;
    }

    public void start() {
        if (this.output) {
            System.setOut(new PrintStream(this));
        } else {
            System.setErr(new PrintStream(this));
        }
    }

    public void stop() {
        if (this.output) {
            System.setOut((PrintStream)this.out);
        } else {
            System.setErr((PrintStream)this.out);
        }
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        Boolean result = (Boolean)this.callback.call(consoleId.get(), new String(b, off, len));
        if (result.booleanValue()) {
            this.out.write(b, off, len);
        }
    }

    @Override
    public void write(int b) throws IOException {
        Boolean result = (Boolean)this.callback.call(consoleId.get(), String.valueOf((char)b));
        if (result.booleanValue()) {
            this.out.write(b);
        }
    }

    public void setConsoleId(int consoleId) {
        SystemOutputInterceptor.consoleId.set(consoleId);
    }

    public void removeConsoleId() {
        consoleId.remove();
    }
}

