/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.messaging.saaj.packaging.mime;

public class MessagingException
extends Exception {
    private Exception next;

    public MessagingException() {
    }

    public MessagingException(String s) {
        super(s);
    }

    public MessagingException(String s, Exception e) {
        super(s);
        this.next = e;
    }

    public synchronized Exception getNextException() {
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
    public String getMessage() {
        if (this.next == null) {
            return super.getMessage();
        }
        Exception n = this.next;
        String s = super.getMessage();
        StringBuilder sb = new StringBuilder(s == null ? "" : s);
        while (n != null) {
            sb.append(";\n  nested exception is:\n\t");
            if (n instanceof MessagingException) {
                MessagingException mex = (MessagingException)n;
                sb.append(n.getClass().toString());
                String msg = mex.getSuperMessage();
                if (msg != null) {
                    sb.append(": ");
                    sb.append(msg);
                }
                n = mex.next;
                continue;
            }
            sb.append(n.toString());
            n = null;
        }
        return sb.toString();
    }

    private String getSuperMessage() {
        return super.getMessage();
    }
}

