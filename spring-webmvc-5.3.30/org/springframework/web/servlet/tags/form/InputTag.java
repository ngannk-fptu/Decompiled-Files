/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.jsp.JspException
 *  org.springframework.lang.Nullable
 */
package org.springframework.web.servlet.tags.form;

import java.util.Map;
import javax.servlet.jsp.JspException;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.tags.form.AbstractHtmlInputElementTag;
import org.springframework.web.servlet.tags.form.TagWriter;

public class InputTag
extends AbstractHtmlInputElementTag {
    public static final String SIZE_ATTRIBUTE = "size";
    public static final String MAXLENGTH_ATTRIBUTE = "maxlength";
    public static final String ALT_ATTRIBUTE = "alt";
    public static final String ONSELECT_ATTRIBUTE = "onselect";
    public static final String AUTOCOMPLETE_ATTRIBUTE = "autocomplete";
    @Nullable
    private String size;
    @Nullable
    private String maxlength;
    @Nullable
    private String alt;
    @Nullable
    private String onselect;
    @Nullable
    private String autocomplete;

    public void setSize(String size) {
        this.size = size;
    }

    @Nullable
    protected String getSize() {
        return this.size;
    }

    public void setMaxlength(String maxlength) {
        this.maxlength = maxlength;
    }

    @Nullable
    protected String getMaxlength() {
        return this.maxlength;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }

    @Nullable
    protected String getAlt() {
        return this.alt;
    }

    public void setOnselect(String onselect) {
        this.onselect = onselect;
    }

    @Nullable
    protected String getOnselect() {
        return this.onselect;
    }

    public void setAutocomplete(String autocomplete) {
        this.autocomplete = autocomplete;
    }

    @Nullable
    protected String getAutocomplete() {
        return this.autocomplete;
    }

    @Override
    protected int writeTagContent(TagWriter tagWriter) throws JspException {
        tagWriter.startTag("input");
        this.writeDefaultAttributes(tagWriter);
        Map<String, Object> attributes = this.getDynamicAttributes();
        if (attributes == null || !attributes.containsKey("type")) {
            tagWriter.writeAttribute("type", this.getType());
        }
        this.writeValue(tagWriter);
        this.writeOptionalAttribute(tagWriter, SIZE_ATTRIBUTE, this.getSize());
        this.writeOptionalAttribute(tagWriter, MAXLENGTH_ATTRIBUTE, this.getMaxlength());
        this.writeOptionalAttribute(tagWriter, ALT_ATTRIBUTE, this.getAlt());
        this.writeOptionalAttribute(tagWriter, ONSELECT_ATTRIBUTE, this.getOnselect());
        this.writeOptionalAttribute(tagWriter, AUTOCOMPLETE_ATTRIBUTE, this.getAutocomplete());
        tagWriter.endTag();
        return 0;
    }

    protected void writeValue(TagWriter tagWriter) throws JspException {
        String value = this.getDisplayString(this.getBoundValue(), this.getPropertyEditor());
        String type = null;
        Map<String, Object> attributes = this.getDynamicAttributes();
        if (attributes != null) {
            type = (String)attributes.get("type");
        }
        if (type == null) {
            type = this.getType();
        }
        tagWriter.writeAttribute("value", this.processFieldValue(this.getName(), value, type));
    }

    @Override
    protected boolean isValidDynamicAttribute(String localName, Object value) {
        return !"type".equals(localName) || !"checkbox".equals(value) && !"radio".equals(value);
    }

    protected String getType() {
        return "text";
    }
}

