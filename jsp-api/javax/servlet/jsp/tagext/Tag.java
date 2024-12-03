/*
 * Decompiled with CFR 0.152.
 */
package javax.servlet.jsp.tagext;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.JspTag;

public interface Tag
extends JspTag {
    public static final int SKIP_BODY = 0;
    public static final int EVAL_BODY_INCLUDE = 1;
    public static final int SKIP_PAGE = 5;
    public static final int EVAL_PAGE = 6;

    public void setPageContext(PageContext var1);

    public void setParent(Tag var1);

    public Tag getParent();

    public int doStartTag() throws JspException;

    public int doEndTag() throws JspException;

    public void release();
}

