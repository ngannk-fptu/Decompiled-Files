/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.jsp.JspException
 *  javax.servlet.jsp.tagext.TagSupport
 *  javax.servlet.jsp.tagext.TryCatchFinally
 *  org.springframework.lang.Nullable
 */
package org.springframework.web.servlet.tags;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.jsp.tagext.TryCatchFinally;
import org.springframework.lang.Nullable;

public class NestedPathTag
extends TagSupport
implements TryCatchFinally {
    public static final String NESTED_PATH_VARIABLE_NAME = "nestedPath";
    @Nullable
    private String path;
    @Nullable
    private String previousNestedPath;

    public void setPath(@Nullable String path) {
        if (path == null) {
            path = "";
        }
        if (path.length() > 0 && !path.endsWith(".")) {
            path = path + ".";
        }
        this.path = path;
    }

    @Nullable
    public String getPath() {
        return this.path;
    }

    public int doStartTag() throws JspException {
        this.previousNestedPath = (String)this.pageContext.getAttribute(NESTED_PATH_VARIABLE_NAME, 2);
        String nestedPath = this.previousNestedPath != null ? this.previousNestedPath + this.getPath() : this.getPath();
        this.pageContext.setAttribute(NESTED_PATH_VARIABLE_NAME, (Object)nestedPath, 2);
        return 1;
    }

    public int doEndTag() {
        if (this.previousNestedPath != null) {
            this.pageContext.setAttribute(NESTED_PATH_VARIABLE_NAME, (Object)this.previousNestedPath, 2);
        } else {
            this.pageContext.removeAttribute(NESTED_PATH_VARIABLE_NAME, 2);
        }
        return 6;
    }

    public void doCatch(Throwable throwable) throws Throwable {
        throw throwable;
    }

    public void doFinally() {
        this.previousNestedPath = null;
    }
}

