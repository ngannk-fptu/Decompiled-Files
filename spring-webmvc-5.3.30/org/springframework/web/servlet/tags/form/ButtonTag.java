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
import org.springframework.web.servlet.tags.form.AbstractHtmlElementTag;
import org.springframework.web.servlet.tags.form.TagWriter;

public class ButtonTag
extends AbstractHtmlElementTag {
    public static final String DISABLED_ATTRIBUTE = "disabled";
    @Nullable
    private TagWriter tagWriter;
    @Nullable
    private String name;
    @Nullable
    private String value;
    private boolean disabled;

    public void setName(String name) {
        this.name = name;
    }

    @Override
    @Nullable
    public String getName() {
        return this.name;
    }

    public void setValue(@Nullable String value) {
        this.value = value;
    }

    @Nullable
    public String getValue() {
        return this.value;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public boolean isDisabled() {
        return this.disabled;
    }

    @Override
    protected int writeTagContent(TagWriter tagWriter) throws JspException {
        tagWriter.startTag("button");
        this.writeDefaultAttributes(tagWriter);
        tagWriter.writeAttribute("type", this.getType());
        this.writeValue(tagWriter);
        if (this.isDisabled()) {
            tagWriter.writeAttribute(DISABLED_ATTRIBUTE, DISABLED_ATTRIBUTE);
        }
        tagWriter.forceBlock();
        this.tagWriter = tagWriter;
        return 1;
    }

    protected void writeValue(TagWriter tagWriter) throws JspException {
        String valueToUse = this.getValue() != null ? this.getValue() : this.getDefaultValue();
        tagWriter.writeAttribute("value", this.processFieldValue(this.getName(), valueToUse, this.getType()));
    }

    protected String getDefaultValue() {
        return "Submit";
    }

    protected String getType() {
        return "submit";
    }

    public int doEndTag() throws JspException {
        Assert.state((this.tagWriter != null ? 1 : 0) != 0, (String)"No TagWriter set");
        this.tagWriter.endTag();
        return 6;
    }
}

