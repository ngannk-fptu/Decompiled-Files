/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.transform;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.xml.transform.SourceLocator;

public class TransformerException
extends Exception {
    private static final long serialVersionUID = 975798773772956428L;
    SourceLocator locator;
    Throwable containedException;
    static /* synthetic */ Class class$java$lang$Throwable;

    public SourceLocator getLocator() {
        return this.locator;
    }

    public void setLocator(SourceLocator sourceLocator) {
        this.locator = sourceLocator;
    }

    public Throwable getException() {
        return this.containedException;
    }

    public Throwable getCause() {
        return this.containedException == this ? null : this.containedException;
    }

    public synchronized Throwable initCause(Throwable throwable) {
        if (this.containedException != null) {
            throw new IllegalStateException("Can't overwrite cause");
        }
        if (throwable == this) {
            throw new IllegalArgumentException("Self-causation not permitted");
        }
        this.containedException = throwable;
        return this;
    }

    public TransformerException(String string) {
        super(string);
        this.containedException = null;
        this.locator = null;
    }

    public TransformerException(Throwable throwable) {
        super(throwable.toString());
        this.containedException = throwable;
        this.locator = null;
    }

    public TransformerException(String string, Throwable throwable) {
        super(string == null || string.length() == 0 ? throwable.toString() : string);
        this.containedException = throwable;
        this.locator = null;
    }

    public TransformerException(String string, SourceLocator sourceLocator) {
        super(string);
        this.containedException = null;
        this.locator = sourceLocator;
    }

    public TransformerException(String string, SourceLocator sourceLocator, Throwable throwable) {
        super(string);
        this.containedException = throwable;
        this.locator = sourceLocator;
    }

    public String getMessageAndLocation() {
        StringBuffer stringBuffer = new StringBuffer();
        String string = super.getMessage();
        if (null != string) {
            stringBuffer.append(string);
        }
        if (null != this.locator) {
            String string2 = this.locator.getSystemId();
            int n = this.locator.getLineNumber();
            int n2 = this.locator.getColumnNumber();
            if (null != string2) {
                stringBuffer.append("; SystemID: ");
                stringBuffer.append(string2);
            }
            if (0 != n) {
                stringBuffer.append("; Line#: ");
                stringBuffer.append(n);
            }
            if (0 != n2) {
                stringBuffer.append("; Column#: ");
                stringBuffer.append(n2);
            }
        }
        return stringBuffer.toString();
    }

    public String getLocationAsString() {
        if (null != this.locator) {
            StringBuffer stringBuffer = new StringBuffer();
            String string = this.locator.getSystemId();
            int n = this.locator.getLineNumber();
            int n2 = this.locator.getColumnNumber();
            if (null != string) {
                stringBuffer.append("; SystemID: ");
                stringBuffer.append(string);
            }
            if (0 != n) {
                stringBuffer.append("; Line#: ");
                stringBuffer.append(n);
            }
            if (0 != n2) {
                stringBuffer.append("; Column#: ");
                stringBuffer.append(n2);
            }
            return stringBuffer.toString();
        }
        return null;
    }

    public void printStackTrace() {
        this.printStackTrace(new PrintWriter(System.err, true));
    }

    public void printStackTrace(PrintStream printStream) {
        this.printStackTrace(new PrintWriter(printStream));
    }

    public void printStackTrace(PrintWriter printWriter) {
        if (printWriter == null) {
            printWriter = new PrintWriter(System.err, true);
        }
        try {
            String string = this.getLocationAsString();
            if (null != string) {
                printWriter.println(string);
            }
            super.printStackTrace(printWriter);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        boolean bl = false;
        try {
            (class$java$lang$Throwable == null ? (class$java$lang$Throwable = TransformerException.class$("java.lang.Throwable")) : class$java$lang$Throwable).getMethod("getCause", null);
            bl = true;
        }
        catch (NoSuchMethodException noSuchMethodException) {
            // empty catch block
        }
        if (!bl) {
            Throwable throwable = this.getException();
            int n = 0;
            while (n < 10 && null != throwable) {
                Object object;
                printWriter.println("---------");
                try {
                    if (throwable instanceof TransformerException && null != (object = ((TransformerException)throwable).getLocationAsString())) {
                        printWriter.println((String)object);
                    }
                    throwable.printStackTrace(printWriter);
                }
                catch (Throwable throwable2) {
                    printWriter.println("Could not print stack trace...");
                }
                try {
                    object = throwable.getClass().getMethod("getException", null);
                    if (null != object) {
                        Throwable throwable3 = throwable;
                        if (throwable3 == (throwable = (Throwable)((Method)object).invoke((Object)throwable, (Object[])null))) {
                            break;
                        }
                    } else {
                        throwable = null;
                    }
                }
                catch (InvocationTargetException invocationTargetException) {
                    throwable = null;
                }
                catch (IllegalAccessException illegalAccessException) {
                    throwable = null;
                }
                catch (NoSuchMethodException noSuchMethodException) {
                    throwable = null;
                }
                ++n;
            }
        }
        printWriter.flush();
    }

    static /* synthetic */ Class class$(String string) {
        try {
            return Class.forName(string);
        }
        catch (ClassNotFoundException classNotFoundException) {
            throw new NoClassDefFoundError(classNotFoundException.getMessage());
        }
    }
}

