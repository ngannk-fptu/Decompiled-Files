/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import org.xhtmlrenderer.util.Util;
import org.xhtmlrenderer.util.XRLog;

public class Uu
extends Util {
    private static Util util;
    private static Util utilAsString;

    private Uu() {
        super(System.out);
    }

    public static void on() {
        Uu.init();
        util.setOn(true);
    }

    public static void off() {
        Uu.init();
        util.setOn(false);
    }

    public static void p(Object object) {
        Uu.init();
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        utilAsString.setPrintWriter(pw);
        utilAsString.print(object);
        pw.flush();
        if (XRLog.isLoggingEnabled()) {
            XRLog.general(sw.getBuffer().toString());
        }
    }

    public static void pr(Object object) {
        Uu.init();
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        utilAsString.setPrintWriter(pw);
        utilAsString.print(object);
        pw.flush();
        if (XRLog.isLoggingEnabled()) {
            XRLog.general(sw.getBuffer().toString());
        }
    }

    public static void sleep(int msec) throws InterruptedException {
        Thread.sleep(msec);
    }

    public static void dump_stack() {
        Uu.p(Uu.stack_to_string(new Exception()));
    }

    public static void main(String[] args) {
        try {
            Uu.p(new Object());
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void init() {
        if (util == null) {
            util = new Util(System.out);
        }
        if (utilAsString == null) {
            utilAsString = new Util(System.out);
        }
    }
}

