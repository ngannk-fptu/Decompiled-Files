/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.jsp.JspException
 *  org.springframework.beans.BeanWrapper
 *  org.springframework.beans.PropertyAccessorFactory
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ObjectUtils
 *  org.springframework.util.StringUtils
 */
package org.springframework.web.servlet.tags.form;

import java.util.Collection;
import java.util.Map;
import javax.servlet.jsp.JspException;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.tags.form.AbstractCheckedElementTag;
import org.springframework.web.servlet.tags.form.TagIdGenerator;
import org.springframework.web.servlet.tags.form.TagWriter;

public abstract class AbstractMultiCheckedElementTag
extends AbstractCheckedElementTag {
    private static final String SPAN_TAG = "span";
    @Nullable
    private Object items;
    @Nullable
    private String itemValue;
    @Nullable
    private String itemLabel;
    private String element = "span";
    @Nullable
    private String delimiter;

    public void setItems(Object items) {
        Assert.notNull((Object)items, (String)"'items' must not be null");
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

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    @Nullable
    public String getDelimiter() {
        return this.delimiter;
    }

    public void setElement(String element) {
        Assert.hasText((String)element, (String)"'element' cannot be null or blank");
        this.element = element;
    }

    public String getElement() {
        return this.element;
    }

    @Override
    protected String resolveId() throws JspException {
        Object id = this.evaluate("id", this.getId());
        if (id != null) {
            String idString = id.toString();
            return StringUtils.hasText((String)idString) ? TagIdGenerator.nextId(idString, this.pageContext) : null;
        }
        return this.autogenerateId();
    }

    @Override
    protected int writeTagContent(TagWriter tagWriter) throws JspException {
        ?[] items = this.getItems();
        ?[] itemsObject = items instanceof String ? this.evaluate("items", items) : items;
        String itemValue = this.getItemValue();
        String itemLabel = this.getItemLabel();
        String valueProperty = itemValue != null ? ObjectUtils.getDisplayString((Object)this.evaluate("itemValue", itemValue)) : null;
        String labelProperty = itemLabel != null ? ObjectUtils.getDisplayString((Object)this.evaluate("itemLabel", itemLabel)) : null;
        Class<?> boundType = this.getBindStatus().getValueType();
        if (itemsObject == null && boundType != null && boundType.isEnum()) {
            itemsObject = boundType.getEnumConstants();
        }
        if (itemsObject == null) {
            throw new IllegalArgumentException("Attribute 'items' is required and must be a Collection, an Array or a Map");
        }
        if (itemsObject.getClass().isArray()) {
            ?[] itemsArray = itemsObject;
            for (int i2 = 0; i2 < itemsArray.length; ++i2) {
                Object item = itemsArray[i2];
                this.writeObjectEntry(tagWriter, valueProperty, labelProperty, item, i2);
            }
        } else if (itemsObject instanceof Collection) {
            Collection optionCollection = (Collection)itemsObject;
            int itemIndex = 0;
            for (Object item : optionCollection) {
                this.writeObjectEntry(tagWriter, valueProperty, labelProperty, item, itemIndex);
                ++itemIndex;
            }
        } else if (itemsObject instanceof Map) {
            Map optionMap = (Map)itemsObject;
            int itemIndex = 0;
            for (Map.Entry entry : optionMap.entrySet()) {
                this.writeMapEntry(tagWriter, valueProperty, labelProperty, entry, itemIndex);
                ++itemIndex;
            }
        } else {
            throw new IllegalArgumentException("Attribute 'items' must be an array, a Collection or a Map");
        }
        return 0;
    }

    private void writeObjectEntry(TagWriter tagWriter, @Nullable String valueProperty, @Nullable String labelProperty, Object item, int itemIndex) throws JspException {
        BeanWrapper wrapper = PropertyAccessorFactory.forBeanPropertyAccess((Object)item);
        Object renderValue = valueProperty != null ? wrapper.getPropertyValue(valueProperty) : (item instanceof Enum ? ((Enum)item).name() : item);
        Object renderLabel = labelProperty != null ? wrapper.getPropertyValue(labelProperty) : item;
        this.writeElementTag(tagWriter, item, renderValue, renderLabel, itemIndex);
    }

    private void writeMapEntry(TagWriter tagWriter, @Nullable String valueProperty, @Nullable String labelProperty, Map.Entry<?, ?> entry, int itemIndex) throws JspException {
        Object mapKey = entry.getKey();
        Object mapValue = entry.getValue();
        BeanWrapper mapKeyWrapper = PropertyAccessorFactory.forBeanPropertyAccess(mapKey);
        BeanWrapper mapValueWrapper = PropertyAccessorFactory.forBeanPropertyAccess(mapValue);
        Object renderValue = valueProperty != null ? mapKeyWrapper.getPropertyValue(valueProperty) : mapKey.toString();
        Object renderLabel = labelProperty != null ? mapValueWrapper.getPropertyValue(labelProperty) : mapValue.toString();
        this.writeElementTag(tagWriter, mapKey, renderValue, renderLabel, itemIndex);
    }

    private void writeElementTag(TagWriter tagWriter, Object item, @Nullable Object value, @Nullable Object label, int itemIndex) throws JspException {
        Object resolvedDelimiter;
        tagWriter.startTag(this.getElement());
        if (itemIndex > 0 && (resolvedDelimiter = this.evaluate("delimiter", this.getDelimiter())) != null) {
            tagWriter.appendValue(resolvedDelimiter.toString());
        }
        tagWriter.startTag("input");
        String id = this.resolveId();
        Assert.state((id != null ? 1 : 0) != 0, (String)"Attribute 'id' is required");
        this.writeOptionalAttribute(tagWriter, "id", id);
        this.writeOptionalAttribute(tagWriter, "name", this.getName());
        this.writeOptionalAttributes(tagWriter);
        tagWriter.writeAttribute("type", this.getInputType());
        this.renderFromValue(item, value, tagWriter);
        tagWriter.endTag();
        tagWriter.startTag("label");
        tagWriter.writeAttribute("for", id);
        tagWriter.appendValue(this.convertToDisplayString(label));
        tagWriter.endTag();
        tagWriter.endTag();
    }
}

