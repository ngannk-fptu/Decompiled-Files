/*
 * Decompiled with CFR 0.152.
 */
package javax.mail;

public class MessagingException
extends Exception {
    private Exception next;
    private static final long serialVersionUID = -7569192289819959253L;

    public MessagingException() {
        this.initCause(null);
    }

    public MessagingException(String s) {
        super(s);
        this.initCause(null);
    }

    public MessagingException(String s, Exception e) {
        super(s);
        this.next = e;
        this.initCause(null);
    }

    public synchronized Exception getNextException() {
        return this.next;
    }

    @Override
    public synchronized Throwable getCause() {
        return this.next;
    }

    public synchronized boolean setNextException(Exception ex) {
        Exception theEnd = this;
        while (theEnd instanceof MessagingException && theEnd.next != null) {
            theEnd = theEnd.next;
        }
        if (theEnd instanceof MessagingException) {
            theEnd.next = ex;
            return true;
        }
        return false;
    }

    @Override
    public synchronized String toString() {
        String s = super.toString();
        Exception n = this.next;
        if (n == null) {
            return s;
        }
        StringBuilder sb = new StringBuilder(s == null ? "" : s);
        while (n != null) {
            sb.append(";\n  nested exception is:\n\t");
            if (n instanceof MessagingException) {
                MessagingException mex = (MessagingException)n;
                sb.append(mex.superToString());
                n = mex.next;
                continue;
            }
            sb.append(n.toString());
            n = null;
        }
        return sb.toString();
    }

    private final String superToString() {
        return super.toString();
    }
}

