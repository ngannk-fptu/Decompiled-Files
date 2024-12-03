/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.jsp.JspException
 */
package com.opensymphony.module.sitemesh.taglib.decorator;

import com.opensymphony.module.sitemesh.taglib.AbstractTag;
import javax.servlet.jsp.JspException;

public class UsePageTag
extends AbstractTag {
    private String id = null;

    public void setId(String id) {
        this.id = id;
    }

    public final int doEndTag() throws JspException {
        this.pageContext.setAttribute(this.id, (Object)this.getPage(), 1);
        return 6;
    }
}

