/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.exceptions;

import java.io.PrintStream;
import java.text.MessageFormat;
import org.apache.xml.security.utils.I18n;

public class XMLSecurityException
extends Exception {
    private static final long serialVersionUID = 1L;
    protected String msgID;

    public XMLSecurityException() {
        super("Missing message string");
        this.msgID = null;
    }

    public XMLSecurityException(String msgID) {
        super(I18n.getExceptionMessage(msgID));
        this.msgID = msgID;
    }

    public XMLSecurityException(String msgID, Object[] exArgs) {
        super(MessageFormat.format(I18n.getExceptionMessage(msgID), exArgs));
        this.msgID = msgID;
    }

    public XMLSecurityException(Exception originalException) {
        super(originalException.getMessage(), originalException);
    }

    public XMLSecurityException(Exception originalException, String msgID) {
        super(I18n.getExceptionMessage(msgID, originalException), originalException);
        this.msgID = msgID;
    }

    @Deprecated
    public XMLSecurityException(String msgID, Exception originalException) {
        this(originalException, msgID);
    }

    public XMLSecurityException(Exception originalException, String msgID, Object[] exArgs) {
        super(MessageFormat.format(I18n.getExceptionMessage(msgID), exArgs), originalException);
        this.msgID = msgID;
    }

    @Deprecated
    public XMLSecurityException(String msgID, Object[] exArgs, Exception originalException) {
        this(originalException, msgID, exArgs);
    }

    public String getMsgID() {
        if (this.msgID == null) {
            return "Missing message ID";
        }
        return this.msgID;
    }

    @Override
    public String toString() {
        String s = this.getClass().getName();
        String message = super.getLocalizedMessage();
        message = message != null ? s + ": " + message : s;
        if (super.getCause() != null) {
            message = message + "\nOriginal Exception was " + super.getCause().toString();
        }
        return message;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void printStackTrace() {
        PrintStream printStream = System.err;
        synchronized (printStream) {
            super.printStackTrace(System.err);
        }
    }

    public Exception getOriginalException() {
        if (this.getCause() instanceof Exception) {
            return (Exception)this.getCause();
        }
        return null;
    }
}

