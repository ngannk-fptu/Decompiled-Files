/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.Environment;
import freemarker.template.TemplateException;
import java.io.PrintStream;
import java.io.PrintWriter;

public class StopException
extends TemplateException {
    StopException(Environment env) {
        super(env);
    }

    StopException(Environment env, String s) {
        super(s, env);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void printStackTrace(PrintWriter pw) {
        PrintWriter printWriter = pw;
        synchronized (printWriter) {
            String msg = this.getMessage();
            pw.print("Encountered stop instruction");
            if (msg != null && !msg.equals("")) {
                pw.println("\nCause given: " + msg);
            } else {
                pw.println();
            }
            super.printStackTrace(pw);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void printStackTrace(PrintStream ps) {
        PrintStream printStream = ps;
        synchronized (printStream) {
            String msg = this.getMessage();
            ps.print("Encountered stop instruction");
            if (msg != null && !msg.equals("")) {
                ps.println("\nCause given: " + msg);
            } else {
                ps.println();
            }
            super.printStackTrace(ps);
        }
    }
}

