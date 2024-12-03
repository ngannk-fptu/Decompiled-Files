/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.jsp.JspException
 *  org.springframework.lang.Nullable
 */
package org.springframework.web.servlet.tags.form;

import javax.servlet.jsp.JspException;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.tags.form.AbstractHtmlElementTag;
import org.springframework.web.servlet.tags.form.TagWriter;

public abstract class AbstractHtmlInputElementTag
extends AbstractHtmlElementTag {
    public static final String ONFOCUS_ATTRIBUTE = "onfocus";
    public static final String ONBLUR_ATTRIBUTE = "onblur";
    public static final String ONCHANGE_ATTRIBUTE = "onchange";
    public static final String ACCESSKEY_ATTRIBUTE = "accesskey";
    public static final String DISABLED_ATTRIBUTE = "disabled";
    public static final String READONLY_ATTRIBUTE = "readonly";
    @Nullable
    private String onfocus;
    @Nullable
    private String onblur;
    @Nullable
    private String onchange;
    @Nullable
    private String accesskey;
    private boolean disabled;
    private boolean readonly;

    public void setOnfocus(String onfocus) {
        this.onfocus = onfocus;
    }

    @Nullable
    protected String getOnfocus() {
        return this.onfocus;
    }

    public void setOnblur(String onblur) {
        this.onblur = onblur;
    }

    @Nullable
    protected String getOnblur() {
        return this.onblur;
    }

    public void setOnchange(String onchange) {
        this.onchange = onchange;
    }

    @Nullable
    protected String getOnchange() {
        return this.onchange;
    }

    public void setAccesskey(String accesskey) {
        this.accesskey = accesskey;
    }

    @Nullable
    protected String getAccesskey() {
        return this.accesskey;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    protected boolean isDisabled() {
        return this.disabled;
    }

    public void setReadonly(boolean readonly) {
        this.readonly = readonly;
    }

    protected boolean isReadonly() {
        return this.readonly;
    }

    @Override
    protected void writeOptionalAttributes(TagWriter tagWriter) throws JspException {
        super.writeOptionalAttributes(tagWriter);
        this.writeOptionalAttribute(tagWriter, ONFOCUS_ATTRIBUTE, this.getOnfocus());
        this.writeOptionalAttribute(tagWriter, ONBLUR_ATTRIBUTE, this.getOnblur());
        this.writeOptionalAttribute(tagWriter, ONCHANGE_ATTRIBUTE, this.getOnchange());
        this.writeOptionalAttribute(tagWriter, ACCESSKEY_ATTRIBUTE, this.getAccesskey());
        if (this.isDisabled()) {
            tagWriter.writeAttribute(DISABLED_ATTRIBUTE, DISABLED_ATTRIBUTE);
        }
        if (this.isReadonly()) {
            this.writeOptionalAttribute(tagWriter, READONLY_ATTRIBUTE, READONLY_ATTRIBUTE);
        }
    }
}

