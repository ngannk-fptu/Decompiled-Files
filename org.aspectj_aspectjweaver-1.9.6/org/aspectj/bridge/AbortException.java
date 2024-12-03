/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.bridge;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import org.aspectj.bridge.IMessage;

public class AbortException
extends RuntimeException {
    private static final long serialVersionUID = -7211791639898586417L;
    private boolean isSilent = false;
    public static final String NO_MESSAGE_TEXT = "AbortException (no message)";
    private static final ArrayList<AbortException> porters = new ArrayList();
    protected IMessage message;
    protected boolean isPorter;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static AbortException borrowPorter(IMessage message) {
        AbortException result;
        ArrayList<AbortException> arrayList = porters;
        synchronized (arrayList) {
            if (porters.size() > 0) {
                result = porters.get(0);
            } else {
                result = new AbortException();
                result.setIsSilent(false);
            }
        }
        result.setIMessage(message);
        result.isPorter = true;
        return result;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void returnPorter(AbortException porter) {
        ArrayList<AbortException> arrayList = porters;
        synchronized (arrayList) {
            if (porters.contains(porter)) {
                throw new IllegalStateException("already have " + porter);
            }
            porters.add(porter);
        }
    }

    private static String extractMessage(IMessage message) {
        if (null == message) {
            return NO_MESSAGE_TEXT;
        }
        String m = message.getMessage();
        if (null == m) {
            return NO_MESSAGE_TEXT;
        }
        return m;
    }

    public AbortException() {
        this("ABORT");
        this.isSilent = true;
    }

    public AbortException(String s) {
        super(null != s ? s : NO_MESSAGE_TEXT);
        this.message = null;
    }

    public AbortException(IMessage message) {
        super(AbortException.extractMessage(message));
        this.message = message;
    }

    public IMessage getIMessage() {
        return this.message;
    }

    public boolean isPorter() {
        return this.isPorter;
    }

    public Throwable getThrown() {
        Throwable result = null;
        IMessage m = this.getIMessage();
        if (null != m && (result = m.getThrown()) instanceof AbortException) {
            return ((AbortException)result).getThrown();
        }
        return result;
    }

    private void setIMessage(IMessage message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        String message = super.getMessage();
        if (null == message || NO_MESSAGE_TEXT == message) {
            Throwable thrown;
            IMessage m = this.getIMessage();
            if (null != m && null == (message = m.getMessage()) && null != (thrown = m.getThrown())) {
                message = thrown.getMessage();
            }
            if (null == message) {
                message = NO_MESSAGE_TEXT;
            }
        }
        return message;
    }

    @Override
    public void printStackTrace() {
        this.printStackTrace(System.out);
    }

    @Override
    public void printStackTrace(PrintStream s) {
        Throwable thrown;
        IMessage m = this.getIMessage();
        Throwable throwable = thrown = null == m ? null : m.getThrown();
        if (!this.isPorter() || null == thrown) {
            s.println("Message: " + m);
            super.printStackTrace(s);
        } else {
            thrown.printStackTrace(s);
        }
    }

    @Override
    public void printStackTrace(PrintWriter s) {
        Throwable thrown;
        IMessage m = this.getIMessage();
        Throwable throwable = thrown = null == m ? null : m.getThrown();
        if (null == thrown) {
            if (this.isPorter()) {
                s.println("(Warning porter AbortException without thrown)");
            }
            s.println("Message: " + m);
            super.printStackTrace(s);
        } else {
            thrown.printStackTrace(s);
        }
    }

    public boolean isSilent() {
        return this.isSilent;
    }

    public void setIsSilent(boolean isSilent) {
        this.isSilent = isSilent;
    }
}

