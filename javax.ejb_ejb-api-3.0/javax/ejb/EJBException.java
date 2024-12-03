/*
 * Decompiled with CFR 0.152.
 */
package javax.ejb;

import java.io.PrintStream;
import java.io.PrintWriter;

public class EJBException
extends RuntimeException {
    private Exception causeException = null;

    public EJBException() {
    }

    public EJBException(String message) {
        super(message);
    }

    public EJBException(Exception ex) {
        this.causeException = ex;
    }

    public EJBException(String message, Exception ex) {
        super(message);
        this.causeException = ex;
    }

    public Exception getCausedByException() {
        return this.causeException;
    }

    public String getMessage() {
        String msg = super.getMessage();
        if (this.causeException == null) {
            return msg;
        }
        if (msg == null) {
            return "nested exception is: " + this.causeException.toString();
        }
        return msg + "; nested exception is: " + this.causeException.toString();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void printStackTrace(PrintStream ps) {
        if (this.causeException == null) {
            super.printStackTrace(ps);
        } else {
            PrintStream printStream = ps;
            synchronized (printStream) {
                ps.println(this);
                this.causeException.printStackTrace(ps);
                super.printStackTrace(ps);
            }
        }
    }

    public void printStackTrace() {
        this.printStackTrace(System.err);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void printStackTrace(PrintWriter pw) {
        if (this.causeException == null) {
            super.printStackTrace(pw);
        } else {
            PrintWriter printWriter = pw;
            synchronized (printWriter) {
                pw.println(this);
                this.causeException.printStackTrace(pw);
                super.printStackTrace(pw);
            }
        }
    }
}

