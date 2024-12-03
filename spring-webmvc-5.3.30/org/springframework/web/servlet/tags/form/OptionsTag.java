/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.jsp.JspException
 *  javax.servlet.jsp.tagext.Tag
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ObjectUtils
 *  org.springframework.util.StringUtils
 *  org.springframework.web.util.TagUtils
 */
package org.springframework.web.servlet.tags.form;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.support.BindStatus;
import org.springframework.web.servlet.tags.form.AbstractHtmlElementTag;
import org.springframework.web.servlet.tags.form.OptionWriter;
import org.springframework.web.servlet.tags.form.SelectTag;
import org.springframework.web.servlet.tags.form.TagIdGenerator;
import org.springframework.web.servlet.tags.form.TagWriter;
import org.springframework.web.util.TagUtils;

public class OptionsTag
extends AbstractHtmlElementTag {
    @Nullable
    private Object items;
    @Nullable
    private String itemValue;
    @Nullable
    private String itemLabel;
    private boolean disabled;

    public void setItems(Object items) {
        this.items = items;
    }

    @Nullable
    protected Object getItems() {
        return this.items;
    }

    public void setItemValue(String itemValue) {
        Assert.hasText((String)itemValue, (String)"'itemValue' must not be empty");
        this.itemValue = itemValue;
    }

    @Nullable
    protected String getItemValue() {
        return this.itemValue;
    }

    public void setItemLabel(String itemLabel) {
        Assert.hasText((String)itemLabel, (String)"'itemLabel' must not be empty");
        this.itemLabel = itemLabel;
    }

    @Nullable
    protected String getItemLabel() {
        return this.itemLabel;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    protected boolean isDisabled() {
        return this.disabled;
    }

    @Override
    protected int writeTagContent(TagWriter tagWriter) throws JspException {
        SelectTag selectTag = this.getSelectTag();
        ?[] items = this.getItems();
        ?[] itemsObject = null;
        if (items != null) {
            itemsObject = items instanceof String ? this.evaluate("items", items) : items;
        } else {
            Class<?> selectTagBoundType = selectTag.getBindStatus().getValueType();
            if (selectTagBoundType != null && selectTagBoundType.isEnum()) {
                itemsObject = selectTagBoundType.getEnumConstants();
            }
        }
        if (itemsObject != null) {
            String selectName = selectTag.getName();
            String itemValue = this.getItemValue();
            String itemLabel = this.getItemLabel();
            String valueProperty = itemValue != null ? ObjectUtils.getDisplayString((Object)this.evaluate("itemValue", itemValue)) : null;
            String labelProperty = itemLabel != null ? ObjectUtils.getDisplayString((Object)this.evaluate("itemLabel", itemLabel)) : null;
            OptionsWriter optionWriter = new OptionsWriter(selectName, itemsObject, valueProperty, labelProperty);
            optionWriter.writeOptions(tagWriter);
        }
        return 0;
    }

    @Override
    protected String resolveId() throws JspException {
        Object id = this.evaluate("id", this.getId());
        if (id != null) {
            String idString = id.toString();
            return StringUtils.hasText((String)idString) ? TagIdGenerator.nextId(idString, this.pageContext) : null;
        }
        return null;
    }

    private SelectTag getSelectTag() {
        TagUtils.assertHasAncestorOfType((Tag)this, SelectTag.class, (String)"options", (String)"select");
        return (SelectTag)OptionsTag.findAncestorWithClass((Tag)this, SelectTag.class);
    }

    @Override
    protected BindStatus getBindStatus() {
        return (BindStatus)this.pageContext.getAttribute("org.springframework.web.servlet.tags.form.SelectTag.listValue");
    }

    private class OptionsWriter
    extends OptionWriter {
        @Nullable
        private final String selectName;

        public OptionsWriter(String selectName, @Nullable Object optionSource, @Nullable String valueProperty, String labelProperty) {
            super(optionSource, OptionsTag.this.getBindStatus(), valueProperty, labelProperty, OptionsTag.this.isHtmlEscape());
            this.selectName = selectName;
        }

        @Override
        protected boolean isOptionDisabled() throws JspException {
            return OptionsTag.this.isDisabled();
        }

        @Override
        protected void writeCommonAttributes(TagWriter tagWriter) throws JspException {
            OptionsTag.this.writeOptionalAttribute(tagWriter, "id", OptionsTag.this.resolveId());
            OptionsTag.this.writeOptionalAttributes(tagWriter);
        }

        @Override
        protected String processOptionValue(String value) {
            return OptionsTag.this.processFieldValue(this.selectName, value, "option");
        }
    }
}

