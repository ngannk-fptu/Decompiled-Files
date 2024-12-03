/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.jsp.JspException
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.web.servlet.tags.form;

import javax.servlet.jsp.JspException;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.servlet.tags.form.AbstractCheckedElementTag;
import org.springframework.web.servlet.tags.form.TagWriter;

public abstract class AbstractSingleCheckedElementTag
extends AbstractCheckedElementTag {
    @Nullable
    private Object value;
    @Nullable
    private Object label;

    public void setValue(Object value) {
        this.value = value;
    }

    @Nullable
    protected Object getValue() {
        return this.value;
    }

    public void setLabel(Object label) {
        this.label = label;
    }

    @Nullable
    protected Object getLabel() {
        return this.label;
    }

    @Override
    protected int writeTagContent(TagWriter tagWriter) throws JspException {
        tagWriter.startTag("input");
        String id = this.resolveId();
        this.writeOptionalAttribute(tagWriter, "id", id);
        this.writeOptionalAttribute(tagWriter, "name", this.getName());
        this.writeOptionalAttributes(tagWriter);
        this.writeTagDetails(tagWriter);
        tagWriter.endTag();
        Object resolvedLabel = this.evaluate("label", this.getLabel());
        if (resolvedLabel != null) {
            Assert.state((id != null ? 1 : 0) != 0, (String)"Label id is required");
            tagWriter.startTag("label");
            tagWriter.writeAttribute("for", id);
            tagWriter.appendValue(this.convertToDisplayString(resolvedLabel));
            tagWriter.endTag();
        }
        return 0;
    }

    protected abstract void writeTagDetails(TagWriter var1) throws JspException;
}

