/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.xpath;

import java.io.PrintStream;
import java.io.PrintWriter;

public class XPathException
extends Exception {
    private final Throwable cause;
    private static final long serialVersionUID = -1837080260374986980L;

    public XPathException(String string) {
        super(string);
        if (string == null) {
            throw new NullPointerException("message can't be null");
        }
        this.cause = null;
    }

    public XPathException(Throwable throwable) {
        super(throwable == null ? null : throwable.toString());
        this.cause = throwable;
        if (throwable == null) {
            throw new NullPointerException("cause can't be null");
        }
    }

    public Throwable getCause() {
        return this.cause;
    }

    public void printStackTrace(PrintStream printStream) {
        if (this.getCause() != null) {
            this.getCause().printStackTrace(printStream);
            printStream.println("--------------- linked to ------------------");
        }
        super.printStackTrace(printStream);
    }

    public void printStackTrace() {
        this.printStackTrace(System.err);
    }

    public void printStackTrace(PrintWriter printWriter) {
        if (this.getCause() != null) {
            this.getCause().printStackTrace(printWriter);
            printWriter.println("--------------- linked to ------------------");
        }
        super.printStackTrace(printWriter);
    }
}

