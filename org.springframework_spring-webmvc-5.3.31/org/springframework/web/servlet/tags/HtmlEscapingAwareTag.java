/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.jsp.JspException
 *  org.springframework.lang.Nullable
 *  org.springframework.web.util.HtmlUtils
 */
package org.springframework.web.servlet.tags;

import javax.servlet.jsp.JspException;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.tags.RequestContextAwareTag;
import org.springframework.web.util.HtmlUtils;

public abstract class HtmlEscapingAwareTag
extends RequestContextAwareTag {
    @Nullable
    private Boolean htmlEscape;

    public void setHtmlEscape(boolean htmlEscape) throws JspException {
        this.htmlEscape = htmlEscape;
    }

    protected boolean isHtmlEscape() {
        if (this.htmlEscape != null) {
            return this.htmlEscape;
        }
        return this.isDefaultHtmlEscape();
    }

    protected boolean isDefaultHtmlEscape() {
        return this.getRequestContext().isDefaultHtmlEscape();
    }

    protected boolean isResponseEncodedHtmlEscape() {
        return this.getRequestContext().isResponseEncodedHtmlEscape();
    }

    protected String htmlEscape(String content) {
        String out = content;
        if (this.isHtmlEscape()) {
            out = this.isResponseEncodedHtmlEscape() ? HtmlUtils.htmlEscape((String)content, (String)this.pageContext.getResponse().getCharacterEncoding()) : HtmlUtils.htmlEscape((String)content);
        }
        return out;
    }
}

