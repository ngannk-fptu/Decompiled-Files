/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.BeanUtils
 *  org.springframework.beans.ConfigurablePropertyAccessor
 *  org.springframework.beans.PropertyAccessorUtils
 *  org.springframework.beans.PropertyEditorRegistry
 *  org.springframework.core.convert.ConversionService
 *  org.springframework.core.convert.TypeDescriptor
 *  org.springframework.core.convert.support.ConvertingPropertyEditorAdapter
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.validation;

import java.beans.PropertyEditor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.ConfigurablePropertyAccessor;
import org.springframework.beans.PropertyAccessorUtils;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.support.ConvertingPropertyEditorAdapter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.validation.AbstractBindingResult;

public abstract class AbstractPropertyBindingResult
extends AbstractBindingResult {
    @Nullable
    private transient ConversionService conversionService;

    protected AbstractPropertyBindingResult(String objectName) {
        super(objectName);
    }

    public void initConversion(ConversionService conversionService) {
        Assert.notNull((Object)conversionService, (String)"ConversionService must not be null");
        this.conversionService = conversionService;
        if (this.getTarget() != null) {
            this.getPropertyAccessor().setConversionService(conversionService);
        }
    }

    @Override
    public PropertyEditorRegistry getPropertyEditorRegistry() {
        return this.getTarget() != null ? this.getPropertyAccessor() : null;
    }

    @Override
    protected String canonicalFieldName(String field) {
        return PropertyAccessorUtils.canonicalPropertyName((String)field);
    }

    @Override
    @Nullable
    public Class<?> getFieldType(@Nullable String field) {
        return this.getTarget() != null ? this.getPropertyAccessor().getPropertyType(this.fixedField(field)) : super.getFieldType(field);
    }

    @Override
    @Nullable
    protected Object getActualFieldValue(String field) {
        return this.getPropertyAccessor().getPropertyValue(field);
    }

    @Override
    protected Object formatFieldValue(String field, @Nullable Object value) {
        String fixedField = this.fixedField(field);
        PropertyEditor customEditor = this.getCustomEditor(fixedField);
        if (customEditor != null) {
            customEditor.setValue(value);
            String textValue = customEditor.getAsText();
            if (textValue != null) {
                return textValue;
            }
        }
        if (this.conversionService != null) {
            TypeDescriptor fieldDesc = this.getPropertyAccessor().getPropertyTypeDescriptor(fixedField);
            TypeDescriptor strDesc = TypeDescriptor.valueOf(String.class);
            if (fieldDesc != null && this.conversionService.canConvert(fieldDesc, strDesc)) {
                return this.conversionService.convert(value, fieldDesc, strDesc);
            }
        }
        return value;
    }

    @Nullable
    protected PropertyEditor getCustomEditor(String fixedField) {
        Class targetType = this.getPropertyAccessor().getPropertyType(fixedField);
        PropertyEditor editor = this.getPropertyAccessor().findCustomEditor(targetType, fixedField);
        if (editor == null) {
            editor = BeanUtils.findEditorByConvention((Class)targetType);
        }
        return editor;
    }

    @Override
    @Nullable
    public PropertyEditor findEditor(@Nullable String field, @Nullable Class<?> valueType) {
        PropertyEditor editor;
        Class<?> valueTypeForLookup = valueType;
        if (valueTypeForLookup == null) {
            valueTypeForLookup = this.getFieldType(field);
        }
        if ((editor = super.findEditor(field, valueTypeForLookup)) == null && this.conversionService != null) {
            TypeDescriptor ptd;
            TypeDescriptor td = null;
            if (field != null && this.getTarget() != null && (ptd = this.getPropertyAccessor().getPropertyTypeDescriptor(this.fixedField(field))) != null && (valueType == null || valueType.isAssignableFrom(ptd.getType()))) {
                td = ptd;
            }
            if (td == null) {
                td = TypeDescriptor.valueOf(valueTypeForLookup);
            }
            if (this.conversionService.canConvert(TypeDescriptor.valueOf(String.class), td)) {
                editor = new ConvertingPropertyEditorAdapter(this.conversionService, td);
            }
        }
        return editor;
    }

    public abstract ConfigurablePropertyAccessor getPropertyAccessor();
}

