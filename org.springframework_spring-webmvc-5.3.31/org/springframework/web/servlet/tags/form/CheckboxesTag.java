/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.jsp.JspException
 */
package org.springframework.web.servlet.tags.form;

import javax.servlet.jsp.JspException;
import org.springframework.web.servlet.tags.form.AbstractMultiCheckedElementTag;
import org.springframework.web.servlet.tags.form.TagWriter;

public class CheckboxesTag
extends AbstractMultiCheckedElementTag {
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
    protected String getInputType() {
        return "checkbox";
    }
}

