/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.jsp.JspException
 */
package org.springframework.web.servlet.tags.form;

import java.util.Collection;
import java.util.Map;
import javax.servlet.jsp.JspException;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;
import org.springframework.web.servlet.support.BindStatus;
import org.springframework.web.servlet.tags.form.AbstractHtmlInputElementTag;
import org.springframework.web.servlet.tags.form.OptionWriter;
import org.springframework.web.servlet.tags.form.TagWriter;

public class SelectTag
extends AbstractHtmlInputElementTag {
    public static final String LIST_VALUE_PAGE_ATTRIBUTE = "org.springframework.web.servlet.tags.form.SelectTag.listValue";
    private static final Object EMPTY = new Object();
    @Nullable
    private Object items;
    @Nullable
    private String itemValue;
    @Nullable
    private String itemLabel;
    @Nullable
    private String size;
    @Nullable
    private Object multiple;
    @Nullable
    private TagWriter tagWriter;

    public void setItems(@Nullable Object items) {
        this.items = items != null ? items : EMPTY;
    }

    @Nullable
    protected Object getItems() {
        return this.items;
    }

    public void setItemValue(String itemValue) {
        this.itemValue = itemValue;
    }

    @Nullable
    protected String getItemValue() {
        return this.itemValue;
    }

    public void setItemLabel(String itemLabel) {
        this.itemLabel = itemLabel;
    }

    @Nullable
    protected String getItemLabel() {
        return this.itemLabel;
    }

    public void setSize(String size) {
        this.size = size;
    }

    @Nullable
    protected String getSize() {
        return this.size;
    }

    public void setMultiple(Object multiple) {
        this.multiple = multiple;
    }

    @Nullable
    protected Object getMultiple() {
        return this.multiple;
    }

    @Override
    protected int writeTagContent(TagWriter tagWriter) throws JspException {
        tagWriter.startTag("select");
        this.writeDefaultAttributes(tagWriter);
        if (this.isMultiple()) {
            tagWriter.writeAttribute("multiple", "multiple");
        }
        tagWriter.writeOptionalAttributeValue("size", this.getDisplayString(this.evaluate("size", this.getSize())));
        Object items = this.getItems();
        if (items != null) {
            Object itemsObject;
            if (items != EMPTY && (itemsObject = this.evaluate("items", items)) != null) {
                final String selectName = this.getName();
                String valueProperty = this.getItemValue() != null ? ObjectUtils.getDisplayString(this.evaluate("itemValue", this.getItemValue())) : null;
                String labelProperty = this.getItemLabel() != null ? ObjectUtils.getDisplayString(this.evaluate("itemLabel", this.getItemLabel())) : null;
                OptionWriter optionWriter = new OptionWriter(itemsObject, this.getBindStatus(), valueProperty, labelProperty, this.isHtmlEscape()){

                    @Override
                    protected String processOptionValue(String resolvedValue) {
                        return SelectTag.this.processFieldValue(selectName, resolvedValue, "option");
                    }
                };
                optionWriter.writeOptions(tagWriter);
            }
            tagWriter.endTag(true);
            this.writeHiddenTagIfNecessary(tagWriter);
            return 0;
        }
        tagWriter.forceBlock();
        this.tagWriter = tagWriter;
        this.pageContext.setAttribute(LIST_VALUE_PAGE_ATTRIBUTE, (Object)this.getBindStatus());
        return 1;
    }

    private void writeHiddenTagIfNecessary(TagWriter tagWriter) throws JspException {
        if (this.isMultiple()) {
            tagWriter.startTag("input");
            tagWriter.writeAttribute("type", "hidden");
            String name = "_" + this.getName();
            tagWriter.writeAttribute("name", name);
            tagWriter.writeAttribute("value", this.processFieldValue(name, "1", "hidden"));
            tagWriter.endTag();
        }
    }

    private boolean isMultiple() throws JspException {
        Object multiple = this.getMultiple();
        if (multiple != null) {
            String stringValue = multiple.toString();
            return "multiple".equalsIgnoreCase(stringValue) || Boolean.parseBoolean(stringValue);
        }
        return this.forceMultiple();
    }

    private boolean forceMultiple() throws JspException {
        Object editorValue;
        BindStatus bindStatus = this.getBindStatus();
        Class<?> valueType = bindStatus.getValueType();
        if (valueType != null && SelectTag.typeRequiresMultiple(valueType)) {
            return true;
        }
        return bindStatus.getEditor() != null && (editorValue = bindStatus.getEditor().getValue()) != null && SelectTag.typeRequiresMultiple(editorValue.getClass());
    }

    private static boolean typeRequiresMultiple(Class<?> type) {
        return type.isArray() || Collection.class.isAssignableFrom(type) || Map.class.isAssignableFrom(type);
    }

    public int doEndTag() throws JspException {
        if (this.tagWriter != null) {
            this.tagWriter.endTag();
            this.writeHiddenTagIfNecessary(this.tagWriter);
        }
        return 6;
    }

    @Override
    public void doFinally() {
        super.doFinally();
        this.tagWriter = null;
        this.pageContext.removeAttribute(LIST_VALUE_PAGE_ATTRIBUTE);
    }
}

