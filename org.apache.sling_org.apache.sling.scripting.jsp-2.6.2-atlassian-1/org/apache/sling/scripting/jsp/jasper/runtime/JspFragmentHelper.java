/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.jsp.JspContext
 *  javax.servlet.jsp.PageContext
 *  javax.servlet.jsp.tagext.JspFragment
 *  javax.servlet.jsp.tagext.JspTag
 */
package org.apache.sling.scripting.jsp.jasper.runtime;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.JspTag;

public abstract class JspFragmentHelper
extends JspFragment {
    protected int discriminator;
    protected JspContext jspContext;
    protected PageContext _jspx_page_context;
    protected JspTag parentTag;

    public JspFragmentHelper(int discriminator, JspContext jspContext, JspTag parentTag) {
        this.discriminator = discriminator;
        this.jspContext = jspContext;
        this._jspx_page_context = null;
        if (jspContext instanceof PageContext) {
            this._jspx_page_context = (PageContext)jspContext;
        }
        this.parentTag = parentTag;
    }

    public JspContext getJspContext() {
        return this.jspContext;
    }

    public JspTag getParentTag() {
        return this.parentTag;
    }
}

