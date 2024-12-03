/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.jsp.JspException
 */
package org.springframework.web.servlet.tags.form;

import java.beans.PropertyEditor;
import javax.servlet.jsp.JspException;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.tags.HtmlEscapingAwareTag;
import org.springframework.web.servlet.tags.form.TagWriter;
import org.springframework.web.servlet.tags.form.ValueFormatter;

public abstract class AbstractFormTag
extends HtmlEscapingAwareTag {
    @Nullable
    protected Object evaluate(String attributeName, @Nullable Object value) throws JspException {
        return value;
    }

    protected final void writeOptionalAttribute(TagWriter tagWriter, String attributeName, @Nullable String value) throws JspException {
        if (value != null) {
            tagWriter.writeOptionalAttributeValue(attributeName, this.getDisplayString(this.evaluate(attributeName, value)));
        }
    }

    protected TagWriter createTagWriter() {
        return new TagWriter(this.pageContext);
    }

    @Override
    protected final int doStartTagInternal() throws Exception {
        return this.writeTagContent(this.createTagWriter());
    }

    protected String getDisplayString(@Nullable Object value) {
        return ValueFormatter.getDisplayString(value, this.isHtmlEscape());
    }

    protected String getDisplayString(@Nullable Object value, @Nullable PropertyEditor propertyEditor) {
        return ValueFormatter.getDisplayString(value, propertyEditor, this.isHtmlEscape());
    }

    @Override
    protected boolean isDefaultHtmlEscape() {
        Boolean defaultHtmlEscape = this.getRequestContext().getDefaultHtmlEscape();
        return defaultHtmlEscape == null || defaultHtmlEscape != false;
    }

    protected abstract int writeTagContent(TagWriter var1) throws JspException;
}

