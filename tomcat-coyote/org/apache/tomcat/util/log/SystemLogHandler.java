/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.log;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.apache.tomcat.util.log.CaptureLog;

public class SystemLogHandler
extends PrintStream {
    private final PrintStream out;
    private static final ThreadLocal<Deque<CaptureLog>> logs = new ThreadLocal();
    private static final Queue<CaptureLog> reuse = new ConcurrentLinkedQueue<CaptureLog>();

    public SystemLogHandler(PrintStream wrapped) {
        super(wrapped);
        this.out = wrapped;
    }

    public static void startCapture() {
        Deque<CaptureLog> stack;
        CaptureLog log = null;
        if (!reuse.isEmpty()) {
            try {
                log = reuse.remove();
            }
            catch (NoSuchElementException e) {
                log = new CaptureLog();
            }
        } else {
            log = new CaptureLog();
        }
        if ((stack = logs.get()) == null) {
            stack = new ArrayDeque<CaptureLog>();
            logs.set(stack);
        }
        stack.addFirst(log);
    }

    public static String stopCapture() {
        Queue stack = logs.get();
        if (stack == null || stack.isEmpty()) {
            return null;
        }
        CaptureLog log = (CaptureLog)stack.remove();
        if (log == null) {
            return null;
        }
        String capture = log.getCapture();
        log.reset();
        reuse.add(log);
        return capture;
    }

    protected PrintStream findStream() {
        PrintStream ps;
        CaptureLog log;
        Queue stack = logs.get();
        if (stack != null && !stack.isEmpty() && (log = (CaptureLog)stack.peek()) != null && (ps = log.getStream()) != null) {
            return ps;
        }
        return this.out;
    }

    @Override
    public void flush() {
        this.findStream().flush();
    }

    @Override
    public void close() {
        this.findStream().close();
    }

    @Override
    public boolean checkError() {
        return this.findStream().checkError();
    }

    @Override
    protected void setError() {
    }

    @Override
    public void write(int b) {
        this.findStream().write(b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        this.findStream().write(b);
    }

    @Override
    public void write(byte[] buf, int off, int len) {
        this.findStream().write(buf, off, len);
    }

    @Override
    public void print(boolean b) {
        this.findStream().print(b);
    }

    @Override
    public void print(char c) {
        this.findStream().print(c);
    }

    @Override
    public void print(int i) {
        this.findStream().print(i);
    }

    @Override
    public void print(long l) {
        this.findStream().print(l);
    }

    @Override
    public void print(float f) {
        this.findStream().print(f);
    }

    @Override
    public void print(double d) {
        this.findStream().print(d);
    }

    @Override
    public void print(char[] s) {
        this.findStream().print(s);
    }

    @Override
    public void print(String s) {
        this.findStream().print(s);
    }

    @Override
    public void print(Object obj) {
        this.findStream().print(obj);
    }

    @Override
    public void println() {
        this.findStream().println();
    }

    @Override
    public void println(boolean x) {
        this.findStream().println(x);
    }

    @Override
    public void println(char x) {
        this.findStream().println(x);
    }

    @Override
    public void println(int x) {
        this.findStream().println(x);
    }

    @Override
    public void println(long x) {
        this.findStream().println(x);
    }

    @Override
    public void println(float x) {
        this.findStream().println(x);
    }

    @Override
    public void println(double x) {
        this.findStream().println(x);
    }

    @Override
    public void println(char[] x) {
        this.findStream().println(x);
    }

    @Override
    public void println(String x) {
        this.findStream().println(x);
    }

    @Override
    public void println(Object x) {
        this.findStream().println(x);
    }
}

