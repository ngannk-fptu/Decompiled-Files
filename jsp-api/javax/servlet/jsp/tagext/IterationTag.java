/*
 * Decompiled with CFR 0.152.
 */
package javax.servlet.jsp.tagext;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;

public interface IterationTag
extends Tag {
    public static final int EVAL_BODY_AGAIN = 2;

    public int doAfterBody() throws JspException;
}

