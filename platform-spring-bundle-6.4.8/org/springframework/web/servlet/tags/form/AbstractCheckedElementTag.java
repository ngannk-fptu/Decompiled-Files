/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.jsp.JspException
 */
package org.springframework.web.servlet.tags.form;

import javax.servlet.jsp.JspException;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.tags.form.AbstractHtmlInputElementTag;
import org.springframework.web.servlet.tags.form.SelectedValueComparator;
import org.springframework.web.servlet.tags.form.TagIdGenerator;
import org.springframework.web.servlet.tags.form.TagWriter;

public abstract class AbstractCheckedElementTag
extends AbstractHtmlInputElementTag {
    protected void renderFromValue(@Nullable Object value, TagWriter tagWriter) throws JspException {
        this.renderFromValue(value, value, tagWriter);
    }

    protected void renderFromValue(@Nullable Object item, @Nullable Object value, TagWriter tagWriter) throws JspException {
        String displayValue = this.convertToDisplayString(value);
        tagWriter.writeAttribute("value", this.processFieldValue(this.getName(), displayValue, this.getInputType()));
        if (this.isOptionSelected(value) || value != item && this.isOptionSelected(item)) {
            tagWriter.writeAttribute("checked", "checked");
        }
    }

    private boolean isOptionSelected(@Nullable Object value) throws JspException {
        return SelectedValueComparator.isSelected(this.getBindStatus(), value);
    }

    protected void renderFromBoolean(Boolean boundValue, TagWriter tagWriter) throws JspException {
        tagWriter.writeAttribute("value", this.processFieldValue(this.getName(), "true", this.getInputType()));
        if (boundValue.booleanValue()) {
            tagWriter.writeAttribute("checked", "checked");
        }
    }

    @Override
    @Nullable
    protected String autogenerateId() throws JspException {
        String id = super.autogenerateId();
        return id != null ? TagIdGenerator.nextId(id, this.pageContext) : null;
    }

    @Override
    protected abstract int writeTagContent(TagWriter var1) throws JspException;

    @Override
    protected boolean isValidDynamicAttribute(String localName, Object value) {
        return !"type".equals(localName);
    }

    protected abstract String getInputType();
}

