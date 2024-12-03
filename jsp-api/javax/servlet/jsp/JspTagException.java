/*
 * Decompiled with CFR 0.152.
 */
package javax.servlet.jsp;

import javax.servlet.jsp.JspException;

public class JspTagException
extends JspException {
    private static final long serialVersionUID = 1L;

    public JspTagException(String msg) {
        super(msg);
    }

    public JspTagException() {
    }

    public JspTagException(String message, Throwable rootCause) {
        super(message, rootCause);
    }

    public JspTagException(Throwable rootCause) {
        super(rootCause);
    }
}

