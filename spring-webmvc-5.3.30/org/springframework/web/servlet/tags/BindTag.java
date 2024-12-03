/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.jsp.JspTagException
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.validation.Errors
 */
package org.springframework.web.servlet.tags;

import java.beans.PropertyEditor;
import javax.servlet.jsp.JspTagException;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.support.BindStatus;
import org.springframework.web.servlet.tags.EditorAwareTag;
import org.springframework.web.servlet.tags.HtmlEscapingAwareTag;

public class BindTag
extends HtmlEscapingAwareTag
implements EditorAwareTag {
    public static final String STATUS_VARIABLE_NAME = "status";
    private String path = "";
    private boolean ignoreNestedPath = false;
    @Nullable
    private BindStatus status;
    @Nullable
    private Object previousPageStatus;
    @Nullable
    private Object previousRequestStatus;

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return this.path;
    }

    public void setIgnoreNestedPath(boolean ignoreNestedPath) {
        this.ignoreNestedPath = ignoreNestedPath;
    }

    public boolean isIgnoreNestedPath() {
        return this.ignoreNestedPath;
    }

    @Override
    protected final int doStartTagInternal() throws Exception {
        String nestedPath;
        String resolvedPath = this.getPath();
        if (!(this.isIgnoreNestedPath() || (nestedPath = (String)this.pageContext.getAttribute("nestedPath", 2)) == null || resolvedPath.startsWith(nestedPath) || resolvedPath.equals(nestedPath.substring(0, nestedPath.length() - 1)))) {
            resolvedPath = nestedPath + resolvedPath;
        }
        try {
            this.status = new BindStatus(this.getRequestContext(), resolvedPath, this.isHtmlEscape());
        }
        catch (IllegalStateException ex) {
            throw new JspTagException(ex.getMessage());
        }
        this.previousPageStatus = this.pageContext.getAttribute(STATUS_VARIABLE_NAME, 1);
        this.previousRequestStatus = this.pageContext.getAttribute(STATUS_VARIABLE_NAME, 2);
        this.pageContext.removeAttribute(STATUS_VARIABLE_NAME, 1);
        this.pageContext.setAttribute(STATUS_VARIABLE_NAME, (Object)this.status, 2);
        return 1;
    }

    public int doEndTag() {
        if (this.previousPageStatus != null) {
            this.pageContext.setAttribute(STATUS_VARIABLE_NAME, this.previousPageStatus, 1);
        }
        if (this.previousRequestStatus != null) {
            this.pageContext.setAttribute(STATUS_VARIABLE_NAME, this.previousRequestStatus, 2);
        } else {
            this.pageContext.removeAttribute(STATUS_VARIABLE_NAME, 2);
        }
        return 6;
    }

    private BindStatus getStatus() {
        Assert.state((this.status != null ? 1 : 0) != 0, (String)"No current BindStatus");
        return this.status;
    }

    @Nullable
    public final String getProperty() {
        return this.getStatus().getExpression();
    }

    @Nullable
    public final Errors getErrors() {
        return this.getStatus().getErrors();
    }

    @Override
    @Nullable
    public final PropertyEditor getEditor() {
        return this.getStatus().getEditor();
    }

    @Override
    public void doFinally() {
        super.doFinally();
        this.status = null;
        this.previousPageStatus = null;
        this.previousRequestStatus = null;
    }
}

