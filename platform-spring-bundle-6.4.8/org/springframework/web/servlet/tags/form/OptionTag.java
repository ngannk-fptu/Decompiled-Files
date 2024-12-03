/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.jsp.JspException
 *  javax.servlet.jsp.tagext.BodyContent
 *  javax.servlet.jsp.tagext.BodyTag
 *  javax.servlet.jsp.tagext.Tag
 */
package org.springframework.web.servlet.tags.form;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTag;
import javax.servlet.jsp.tagext.Tag;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.support.BindStatus;
import org.springframework.web.servlet.tags.form.AbstractHtmlElementBodyTag;
import org.springframework.web.servlet.tags.form.SelectTag;
import org.springframework.web.servlet.tags.form.SelectedValueComparator;
import org.springframework.web.servlet.tags.form.TagWriter;
import org.springframework.web.util.TagUtils;

public class OptionTag
extends AbstractHtmlElementBodyTag
implements BodyTag {
    public static final String VALUE_VARIABLE_NAME = "value";
    public static final String DISPLAY_VALUE_VARIABLE_NAME = "displayValue";
    private static final String SELECTED_ATTRIBUTE = "selected";
    private static final String VALUE_ATTRIBUTE = "value";
    private static final String DISABLED_ATTRIBUTE = "disabled";
    @Nullable
    private Object value;
    @Nullable
    private String label;
    @Nullable
    private Object oldValue;
    @Nullable
    private Object oldDisplayValue;
    private boolean disabled;

    public void setValue(Object value) {
        this.value = value;
    }

    @Nullable
    protected Object getValue() {
        return this.value;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    protected boolean isDisabled() {
        return this.disabled;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Nullable
    protected String getLabel() {
        return this.label;
    }

    @Override
    protected void renderDefaultContent(TagWriter tagWriter) throws JspException {
        Object value = this.pageContext.getAttribute("value");
        String label = this.getLabelValue(value);
        this.renderOption(value, label, tagWriter);
    }

    @Override
    protected void renderFromBodyContent(BodyContent bodyContent, TagWriter tagWriter) throws JspException {
        Object value = this.pageContext.getAttribute("value");
        String label = bodyContent.getString();
        this.renderOption(value, label, tagWriter);
    }

    @Override
    protected void onWriteTagContent() {
        this.assertUnderSelectTag();
    }

    @Override
    protected void exposeAttributes() throws JspException {
        Object value = this.resolveValue();
        this.oldValue = this.pageContext.getAttribute("value");
        this.pageContext.setAttribute("value", value);
        this.oldDisplayValue = this.pageContext.getAttribute(DISPLAY_VALUE_VARIABLE_NAME);
        this.pageContext.setAttribute(DISPLAY_VALUE_VARIABLE_NAME, (Object)this.getDisplayString(value, this.getBindStatus().getEditor()));
    }

    @Override
    protected BindStatus getBindStatus() {
        return (BindStatus)this.pageContext.getAttribute("org.springframework.web.servlet.tags.form.SelectTag.listValue");
    }

    @Override
    protected void removeAttributes() {
        if (this.oldValue != null) {
            this.pageContext.setAttribute("value", this.oldValue);
            this.oldValue = null;
        } else {
            this.pageContext.removeAttribute("value");
        }
        if (this.oldDisplayValue != null) {
            this.pageContext.setAttribute(DISPLAY_VALUE_VARIABLE_NAME, this.oldDisplayValue);
            this.oldDisplayValue = null;
        } else {
            this.pageContext.removeAttribute(DISPLAY_VALUE_VARIABLE_NAME);
        }
    }

    private void renderOption(Object value, String label, TagWriter tagWriter) throws JspException {
        tagWriter.startTag("option");
        this.writeOptionalAttribute(tagWriter, "id", this.resolveId());
        this.writeOptionalAttributes(tagWriter);
        String renderedValue = this.getDisplayString(value, this.getBindStatus().getEditor());
        renderedValue = this.processFieldValue(this.getSelectTag().getName(), renderedValue, "option");
        tagWriter.writeAttribute("value", renderedValue);
        if (this.isSelected(value)) {
            tagWriter.writeAttribute(SELECTED_ATTRIBUTE, SELECTED_ATTRIBUTE);
        }
        if (this.isDisabled()) {
            tagWriter.writeAttribute(DISABLED_ATTRIBUTE, DISABLED_ATTRIBUTE);
        }
        tagWriter.appendValue(label);
        tagWriter.endTag();
    }

    @Override
    protected String autogenerateId() throws JspException {
        return null;
    }

    private String getLabelValue(Object resolvedValue) throws JspException {
        String label = this.getLabel();
        Object labelObj = label == null ? resolvedValue : this.evaluate("label", label);
        return this.getDisplayString(labelObj, this.getBindStatus().getEditor());
    }

    private void assertUnderSelectTag() {
        TagUtils.assertHasAncestorOfType((Tag)this, SelectTag.class, "option", "select");
    }

    private SelectTag getSelectTag() {
        return (SelectTag)OptionTag.findAncestorWithClass((Tag)this, SelectTag.class);
    }

    private boolean isSelected(Object resolvedValue) {
        return SelectedValueComparator.isSelected(this.getBindStatus(), resolvedValue);
    }

    @Nullable
    private Object resolveValue() throws JspException {
        return this.evaluate("value", this.getValue());
    }
}

