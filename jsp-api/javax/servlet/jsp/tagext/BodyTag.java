/*
 * Decompiled with CFR 0.152.
 */
package javax.servlet.jsp.tagext;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.IterationTag;

public interface BodyTag
extends IterationTag {
    public static final int EVAL_BODY_TAG = 2;
    public static final int EVAL_BODY_BUFFERED = 2;

    public void setBodyContent(BodyContent var1);

    public void doInitBody() throws JspException;
}

