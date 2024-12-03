/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.jsp.JspException
 */
package org.springframework.web.servlet.tags.form;

import javax.servlet.jsp.JspException;
import org.springframework.web.servlet.tags.form.AbstractSingleCheckedElementTag;
import org.springframework.web.servlet.tags.form.TagWriter;

public class CheckboxTag
extends AbstractSingleCheckedElementTag {
    @Override
    protected int writeTagContent(TagWriter tagWriter) throws JspException {
        super.writeTagContent(tagWriter);
        if (!this.isDisabled()) {
            tagWriter.startTag("input");
            tagWriter.writeAttribute("type", "hidden");
            String name = "_" + this.getName();
            tagWriter.writeAttribute("name", name);
            tagWriter.writeAttribute("value", this.processFieldValue(name, "on", "hidden"));
            tagWriter.endTag();
        }
        return 0;
    }

    @Override
    protected void writeTagDetails(TagWriter tagWriter) throws JspException {
        tagWriter.writeAttribute("type", this.getInputType());
        Object boundValue = this.getBoundValue();
        Class<?> valueType = this.getBindStatus().getValueType();
        if (Boolean.class == valueType || Boolean.TYPE == valueType) {
            if (boundValue instanceof String) {
                boundValue = Boolean.valueOf((String)boundValue);
            }
            Boolean booleanValue = boundValue != null ? (Boolean)boundValue : Boolean.FALSE;
            this.renderFromBoolean(booleanValue, tagWriter);
        } else {
            Object value = this.getValue();
            if (value == null) {
                throw new IllegalArgumentException("Attribute 'value' is required when binding to non-boolean values");
            }
            Object resolvedValue = value instanceof String ? this.evaluate("value", value) : value;
            this.renderFromValue(resolvedValue, tagWriter);
        }
    }

    @Override
    protected String getInputType() {
        return "checkbox";
    }
}

