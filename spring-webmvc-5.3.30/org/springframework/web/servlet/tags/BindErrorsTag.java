/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletException
 *  javax.servlet.jsp.JspException
 *  org.springframework.lang.Nullable
 *  org.springframework.validation.Errors
 */
package org.springframework.web.servlet.tags;

import javax.servlet.ServletException;
import javax.servlet.jsp.JspException;
import org.springframework.lang.Nullable;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.tags.HtmlEscapingAwareTag;

public class BindErrorsTag
extends HtmlEscapingAwareTag {
    public static final String ERRORS_VARIABLE_NAME = "errors";
    private String name = "";
    @Nullable
    private Errors errors;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    @Override
    protected final int doStartTagInternal() throws ServletException, JspException {
        this.errors = this.getRequestContext().getErrors(this.name, this.isHtmlEscape());
        if (this.errors != null && this.errors.hasErrors()) {
            this.pageContext.setAttribute(ERRORS_VARIABLE_NAME, (Object)this.errors, 2);
            return 1;
        }
        return 0;
    }

    public int doEndTag() {
        this.pageContext.removeAttribute(ERRORS_VARIABLE_NAME, 2);
        return 6;
    }

    @Nullable
    public final Errors getErrors() {
        return this.errors;
    }

    @Override
    public void doFinally() {
        super.doFinally();
        this.errors = null;
    }
}

