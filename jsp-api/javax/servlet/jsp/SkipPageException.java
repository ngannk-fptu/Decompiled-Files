/*
 * Decompiled with CFR 0.152.
 */
package javax.servlet.jsp;

import javax.servlet.jsp.JspException;

public class SkipPageException
extends JspException {
    private static final long serialVersionUID = 1L;

    public SkipPageException() {
    }

    public SkipPageException(String message) {
        super(message);
    }

    public SkipPageException(String message, Throwable rootCause) {
        super(message, rootCause);
    }

    public SkipPageException(Throwable rootCause) {
        super(rootCause);
    }
}

