/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.jsp.JspException
 *  javax.servlet.jsp.PageContext
 *  javax.servlet.jsp.tagext.BodyTagSupport
 *  javax.servlet.jsp.tagext.Tag
 */
package com.opensymphony.module.sitemesh.taglib;

import com.opensymphony.module.sitemesh.Page;
import com.opensymphony.module.sitemesh.RequestConstants;
import com.opensymphony.module.sitemesh.util.OutputConverter;
import java.io.Writer;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.Tag;

public abstract class AbstractTag
extends BodyTagSupport
implements RequestConstants {
    protected PageContext pageContext;
    protected Tag parent;

    public abstract int doEndTag() throws JspException;

    public int doStartTag() {
        return 0;
    }

    public void release() {
    }

    public Tag getParent() {
        return this.parent;
    }

    public void setParent(Tag parent) {
        this.parent = parent;
    }

    public void setPageContext(PageContext pageContext) {
        this.pageContext = pageContext;
    }

    protected Page getPage() {
        Page p = (Page)this.pageContext.getAttribute(PAGE, 1);
        if (p == null) {
            p = (Page)this.pageContext.getAttribute(PAGE, 2);
            if (p == null) {
                this.pageContext.removeAttribute(PAGE, 1);
            } else {
                this.pageContext.setAttribute(PAGE, (Object)p, 1);
            }
            this.pageContext.removeAttribute(PAGE, 2);
        }
        return p;
    }

    protected static void trace(Exception e) {
        e.printStackTrace();
    }

    protected Writer getOut() {
        return OutputConverter.getWriter((Writer)this.pageContext.getOut());
    }
}

