/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletRequest
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.jsp.JspException
 */
package org.springframework.web.servlet.tags.form;

import java.beans.PropertyEditor;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.support.BindStatus;
import org.springframework.web.servlet.support.RequestDataValueProcessor;
import org.springframework.web.servlet.tags.EditorAwareTag;
import org.springframework.web.servlet.tags.form.AbstractFormTag;
import org.springframework.web.servlet.tags.form.TagWriter;

public abstract class AbstractDataBoundFormElementTag
extends AbstractFormTag
implements EditorAwareTag {
    protected static final String NESTED_PATH_VARIABLE_NAME = "nestedPath";
    @Nullable
    private String path;
    @Nullable
    private String id;
    @Nullable
    private BindStatus bindStatus;

    public void setPath(String path) {
        this.path = path;
    }

    protected final String getPath() throws JspException {
        String resolvedPath = (String)this.evaluate("path", this.path);
        return resolvedPath != null ? resolvedPath : "";
    }

    public void setId(@Nullable String id) {
        this.id = id;
    }

    @Nullable
    public String getId() {
        return this.id;
    }

    protected void writeDefaultAttributes(TagWriter tagWriter) throws JspException {
        this.writeOptionalAttribute(tagWriter, "id", this.resolveId());
        this.writeOptionalAttribute(tagWriter, "name", this.getName());
    }

    @Nullable
    protected String resolveId() throws JspException {
        Object id = this.evaluate("id", this.getId());
        if (id != null) {
            String idString = id.toString();
            return StringUtils.hasText(idString) ? idString : null;
        }
        return this.autogenerateId();
    }

    @Nullable
    protected String autogenerateId() throws JspException {
        String name = this.getName();
        return name != null ? StringUtils.deleteAny(name, "[]") : null;
    }

    @Nullable
    protected String getName() throws JspException {
        return this.getPropertyPath();
    }

    protected BindStatus getBindStatus() throws JspException {
        if (this.bindStatus == null) {
            String pathToUse;
            String nestedPath = this.getNestedPath();
            String string = pathToUse = nestedPath != null ? nestedPath + this.getPath() : this.getPath();
            if (pathToUse.endsWith(".")) {
                pathToUse = pathToUse.substring(0, pathToUse.length() - 1);
            }
            this.bindStatus = new BindStatus(this.getRequestContext(), pathToUse, false);
        }
        return this.bindStatus;
    }

    @Nullable
    protected String getNestedPath() {
        return (String)this.pageContext.getAttribute(NESTED_PATH_VARIABLE_NAME, 2);
    }

    protected String getPropertyPath() throws JspException {
        String expression = this.getBindStatus().getExpression();
        return expression != null ? expression : "";
    }

    @Nullable
    protected final Object getBoundValue() throws JspException {
        return this.getBindStatus().getValue();
    }

    @Nullable
    protected PropertyEditor getPropertyEditor() throws JspException {
        return this.getBindStatus().getEditor();
    }

    @Override
    @Nullable
    public final PropertyEditor getEditor() throws JspException {
        return this.getPropertyEditor();
    }

    protected String convertToDisplayString(@Nullable Object value) throws JspException {
        PropertyEditor editor = value != null ? this.getBindStatus().findEditor(value.getClass()) : null;
        return this.getDisplayString(value, editor);
    }

    protected final String processFieldValue(@Nullable String name, String value, String type) {
        RequestDataValueProcessor processor = this.getRequestContext().getRequestDataValueProcessor();
        ServletRequest request = this.pageContext.getRequest();
        if (processor != null && request instanceof HttpServletRequest) {
            value = processor.processFormFieldValue((HttpServletRequest)request, name, value, type);
        }
        return value;
    }

    @Override
    public void doFinally() {
        super.doFinally();
        this.bindStatus = null;
    }
}

