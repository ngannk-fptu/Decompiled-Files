/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.datatype;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;

public class DatatypeConfigurationException
extends Exception {
    private static final long serialVersionUID = -1699373159027047238L;
    private Throwable causeOnJDK13OrBelow;
    private transient boolean isJDK14OrAbove = false;
    static /* synthetic */ Class class$java$lang$Throwable;

    public DatatypeConfigurationException() {
    }

    public DatatypeConfigurationException(String string) {
        super(string);
    }

    public DatatypeConfigurationException(String string, Throwable throwable) {
        super(string);
        this.initCauseByReflection(throwable);
    }

    public DatatypeConfigurationException(Throwable throwable) {
        super(throwable == null ? null : throwable.toString());
        this.initCauseByReflection(throwable);
    }

    public void printStackTrace() {
        if (!this.isJDK14OrAbove && this.causeOnJDK13OrBelow != null) {
            this.printStackTrace0(new PrintWriter(System.err, true));
        } else {
            super.printStackTrace();
        }
    }

    public void printStackTrace(PrintStream printStream) {
        if (!this.isJDK14OrAbove && this.causeOnJDK13OrBelow != null) {
            this.printStackTrace0(new PrintWriter(printStream));
        } else {
            super.printStackTrace(printStream);
        }
    }

    public void printStackTrace(PrintWriter printWriter) {
        if (!this.isJDK14OrAbove && this.causeOnJDK13OrBelow != null) {
            this.printStackTrace0(printWriter);
        } else {
            super.printStackTrace(printWriter);
        }
    }

    private void printStackTrace0(PrintWriter printWriter) {
        this.causeOnJDK13OrBelow.printStackTrace(printWriter);
        printWriter.println("------------------------------------------");
        super.printStackTrace(printWriter);
    }

    private void initCauseByReflection(Throwable throwable) {
        this.causeOnJDK13OrBelow = throwable;
        try {
            Method method = this.getClass().getMethod("initCause", class$java$lang$Throwable == null ? (class$java$lang$Throwable = DatatypeConfigurationException.class$("java.lang.Throwable")) : class$java$lang$Throwable);
            method.invoke((Object)this, throwable);
            this.isJDK14OrAbove = true;
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        try {
            Method method = this.getClass().getMethod("getCause", new Class[0]);
            Throwable throwable = (Throwable)method.invoke((Object)this, new Object[0]);
            if (this.causeOnJDK13OrBelow == null) {
                this.causeOnJDK13OrBelow = throwable;
            } else if (throwable == null) {
                Method method2 = this.getClass().getMethod("initCause", class$java$lang$Throwable == null ? (class$java$lang$Throwable = DatatypeConfigurationException.class$("java.lang.Throwable")) : class$java$lang$Throwable);
                method2.invoke((Object)this, this.causeOnJDK13OrBelow);
            }
            this.isJDK14OrAbove = true;
        }
        catch (Exception exception) {
            // empty catch block
        }
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

