/*
 * Decompiled with CFR 0.152.
 */
package javax.servlet.jsp.tagext;

import java.io.IOException;
import java.io.Writer;
import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;

public abstract class JspFragment {
    public abstract void invoke(Writer var1) throws JspException, IOException;

    public abstract JspContext getJspContext();
}

