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

public class RadioButtonTag
extends AbstractSingleCheckedElementTag {
    @Override
    protected void writeTagDetails(TagWriter tagWriter) throws JspException {
        tagWriter.writeAttribute("type", this.getInputType());
        Object resolvedValue = this.evaluate("value", this.getValue());
        this.renderFromValue(resolvedValue, tagWriter);
    }

    @Override
    protected String getInputType() {
        return "radio";
    }
}

