/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.jsp.JspException
 */
package org.springframework.web.servlet.tags.form;

import javax.servlet.jsp.JspException;
import org.springframework.web.servlet.tags.form.AbstractHtmlElementTag;
import org.springframework.web.servlet.tags.form.TagWriter;

public class HiddenInputTag
extends AbstractHtmlElementTag {
    public static final String DISABLED_ATTRIBUTE = "disabled";
    private boolean disabled;

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public boolean isDisabled() {
        return this.disabled;
    }

    @Override
    protected boolean isValidDynamicAttribute(String localName, Object value) {
        return !"type".equals(localName);
    }

    @Override
    protected int writeTagContent(TagWriter tagWriter) throws JspException {
        tagWriter.startTag("input");
        this.writeDefaultAttributes(tagWriter);
        tagWriter.writeAttribute("type", "hidden");
        if (this.isDisabled()) {
            tagWriter.writeAttribute(DISABLED_ATTRIBUTE, DISABLED_ATTRIBUTE);
        }
        String value = this.getDisplayString(this.getBoundValue(), this.getPropertyEditor());
        tagWriter.writeAttribute("value", this.processFieldValue(this.getName(), value, "hidden"));
        tagWriter.endTag();
        return 0;
    }
}

