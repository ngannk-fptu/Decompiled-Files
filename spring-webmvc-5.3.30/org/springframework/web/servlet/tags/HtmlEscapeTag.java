/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.jsp.JspException
 */
package org.springframework.web.servlet.tags;

import javax.servlet.jsp.JspException;
import org.springframework.web.servlet.tags.RequestContextAwareTag;

public class HtmlEscapeTag
extends RequestContextAwareTag {
    private boolean defaultHtmlEscape;

    public void setDefaultHtmlEscape(boolean defaultHtmlEscape) {
        this.defaultHtmlEscape = defaultHtmlEscape;
    }

    @Override
    protected int doStartTagInternal() throws JspException {
        this.getRequestContext().setDefaultHtmlEscape(this.defaultHtmlEscape);
        return 1;
    }
}

