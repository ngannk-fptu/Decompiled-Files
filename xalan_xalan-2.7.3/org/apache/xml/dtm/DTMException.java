/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.dtm;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.xml.transform.SourceLocator;
import org.apache.xml.res.XMLMessages;

public class DTMException
extends RuntimeException {
    static final long serialVersionUID = -775576419181334734L;
    SourceLocator locator;
    Throwable containedException;

    public SourceLocator getLocator() {
        return this.locator;
    }

    public void setLocator(SourceLocator location) {
        this.locator = location;
    }

    public Throwable getException() {
        return this.containedException;
    }

    @Override
    public Throwable getCause() {
        return this.containedException == this ? null : this.containedException;
    }

    @Override
    public synchronized Throwable initCause(Throwable cause) {
        if (this.containedException == null && cause != null) {
            throw new IllegalStateException(XMLMessages.createXMLMessage("ER_CANNOT_OVERWRITE_CAUSE", null));
        }
        if (cause == this) {
            throw new IllegalArgumentException(XMLMessages.createXMLMessage("ER_SELF_CAUSATION_NOT_PERMITTED", null));
        }
        this.containedException = cause;
        return this;
    }

    public DTMException(String message) {
        super(message);
        this.containedException = null;
        this.locator = null;
    }

    public DTMException(Throwable e) {
        super(e.getMessage());
        this.containedException = e;
        this.locator = null;
    }

    public DTMException(String message, Throwable e) {
        super(message == null || message.length() == 0 ? e.getMessage() : message);
        this.containedException = e;
        this.locator = null;
    }

    public DTMException(String message, SourceLocator locator) {
        super(message);
        this.containedException = null;
        this.locator = locator;
    }

    public DTMException(String message, SourceLocator locator, Throwable e) {
        super(message);
        this.containedException = e;
        this.locator = locator;
    }

    public String getMessageAndLocation() {
        StringBuffer sbuffer = new StringBuffer();
        String message = super.getMessage();
        if (null != message) {
            sbuffer.append(message);
        }
        if (null != this.locator) {
            String systemID = this.locator.getSystemId();
            int line = this.locator.getLineNumber();
            int column = this.locator.getColumnNumber();
            if (null != systemID) {
                sbuffer.append("; SystemID: ");
                sbuffer.append(systemID);
            }
            if (0 != line) {
                sbuffer.append("; Line#: ");
                sbuffer.append(line);
            }
            if (0 != column) {
                sbuffer.append("; Column#: ");
                sbuffer.append(column);
            }
        }
        return sbuffer.toString();
    }

    public String getLocationAsString() {
        if (null != this.locator) {
            StringBuffer sbuffer = new StringBuffer();
            String systemID = this.locator.getSystemId();
            int line = this.locator.getLineNumber();
            int column = this.locator.getColumnNumber();
            if (null != systemID) {
                sbuffer.append("; SystemID: ");
                sbuffer.append(systemID);
            }
            if (0 != line) {
                sbuffer.append("; Line#: ");
                sbuffer.append(line);
            }
            if (0 != column) {
                sbuffer.append("; Column#: ");
                sbuffer.append(column);
            }
            return sbuffer.toString();
        }
        return null;
    }

    @Override
    public void printStackTrace() {
        this.printStackTrace(new PrintWriter(System.err, true));
    }

    @Override
    public void printStackTrace(PrintStream s) {
        this.printStackTrace(new PrintWriter(s));
    }

    @Override
    public void printStackTrace(PrintWriter s) {
        if (s == null) {
            s = new PrintWriter(System.err, true);
        }
        try {
            String locInfo = this.getLocationAsString();
            if (null != locInfo) {
                s.println(locInfo);
            }
            super.printStackTrace(s);
        }
        catch (Throwable locInfo) {
            // empty catch block
        }
        boolean isJdk14OrHigher = false;
        try {
            Throwable.class.getMethod("getCause", null);
            isJdk14OrHigher = true;
        }
        catch (NoSuchMethodException noSuchMethodException) {
            // empty catch block
        }
        if (!isJdk14OrHigher) {
            Throwable exception = this.getException();
            for (int i = 0; i < 10 && null != exception; ++i) {
                s.println("---------");
                try {
                    String locInfo;
                    if (exception instanceof DTMException && null != (locInfo = ((DTMException)exception).getLocationAsString())) {
                        s.println(locInfo);
                    }
                    exception.printStackTrace(s);
                }
                catch (Throwable e) {
                    s.println("Could not print stack trace...");
                }
                try {
                    Method meth = exception.getClass().getMethod("getException", null);
                    if (null != meth) {
                        Throwable prev = exception;
                        if (prev != (exception = (Throwable)meth.invoke((Object)exception, null))) continue;
                        break;
                    }
                    exception = null;
                    continue;
                }
                catch (InvocationTargetException ite) {
                    exception = null;
                    continue;
                }
                catch (IllegalAccessException iae) {
                    exception = null;
                    continue;
                }
                catch (NoSuchMethodException nsme) {
                    exception = null;
                }
            }
        }
    }
}

