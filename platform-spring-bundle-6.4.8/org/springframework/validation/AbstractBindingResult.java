/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.validation;

import java.beans.PropertyEditor;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.AbstractErrors;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DefaultMessageCodesResolver;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.validation.ObjectError;

public abstract class AbstractBindingResult
extends AbstractErrors
implements BindingResult,
Serializable {
    private final String objectName;
    private MessageCodesResolver messageCodesResolver = new DefaultMessageCodesResolver();
    private final List<ObjectError> errors = new ArrayList<ObjectError>();
    private final Map<String, Class<?>> fieldTypes = new HashMap();
    private final Map<String, Object> fieldValues = new HashMap<String, Object>();
    private final Set<String> suppressedFields = new HashSet<String>();

    protected AbstractBindingResult(String objectName) {
        this.objectName = objectName;
    }

    public void setMessageCodesResolver(MessageCodesResolver messageCodesResolver) {
        Assert.notNull((Object)messageCodesResolver, "MessageCodesResolver must not be null");
        this.messageCodesResolver = messageCodesResolver;
    }

    public MessageCodesResolver getMessageCodesResolver() {
        return this.messageCodesResolver;
    }

    @Override
    public String getObjectName() {
        return this.objectName;
    }

    @Override
    public void reject(String errorCode, @Nullable Object[] errorArgs, @Nullable String defaultMessage) {
        this.addError(new ObjectError(this.getObjectName(), this.resolveMessageCodes(errorCode), errorArgs, defaultMessage));
    }

    @Override
    public void rejectValue(@Nullable String field, String errorCode, @Nullable Object[] errorArgs, @Nullable String defaultMessage) {
        if (!StringUtils.hasLength(this.getNestedPath()) && !StringUtils.hasLength(field)) {
            this.reject(errorCode, errorArgs, defaultMessage);
            return;
        }
        String fixedField = this.fixedField(field);
        Object newVal = this.getActualFieldValue(fixedField);
        FieldError fe = new FieldError(this.getObjectName(), fixedField, newVal, false, this.resolveMessageCodes(errorCode, field), errorArgs, defaultMessage);
        this.addError(fe);
    }

    @Override
    public void addAllErrors(Errors errors) {
        if (!errors.getObjectName().equals(this.getObjectName())) {
            throw new IllegalArgumentException("Errors object needs to have same object name");
        }
        this.errors.addAll(errors.getAllErrors());
    }

    @Override
    public boolean hasErrors() {
        return !this.errors.isEmpty();
    }

    @Override
    public int getErrorCount() {
        return this.errors.size();
    }

    @Override
    public List<ObjectError> getAllErrors() {
        return Collections.unmodifiableList(this.errors);
    }

    @Override
    public List<ObjectError> getGlobalErrors() {
        ArrayList<ObjectError> result = new ArrayList<ObjectError>();
        for (ObjectError objectError : this.errors) {
            if (objectError instanceof FieldError) continue;
            result.add(objectError);
        }
        return Collections.unmodifiableList(result);
    }

    @Override
    @Nullable
    public ObjectError getGlobalError() {
        for (ObjectError objectError : this.errors) {
            if (objectError instanceof FieldError) continue;
            return objectError;
        }
        return null;
    }

    @Override
    public List<FieldError> getFieldErrors() {
        ArrayList<FieldError> result = new ArrayList<FieldError>();
        for (ObjectError objectError : this.errors) {
            if (!(objectError instanceof FieldError)) continue;
            result.add((FieldError)objectError);
        }
        return Collections.unmodifiableList(result);
    }

    @Override
    @Nullable
    public FieldError getFieldError() {
        for (ObjectError objectError : this.errors) {
            if (!(objectError instanceof FieldError)) continue;
            return (FieldError)objectError;
        }
        return null;
    }

    @Override
    public List<FieldError> getFieldErrors(String field) {
        ArrayList<FieldError> result = new ArrayList<FieldError>();
        String fixedField = this.fixedField(field);
        for (ObjectError objectError : this.errors) {
            if (!(objectError instanceof FieldError) || !this.isMatchingFieldError(fixedField, (FieldError)objectError)) continue;
            result.add((FieldError)objectError);
        }
        return Collections.unmodifiableList(result);
    }

    @Override
    @Nullable
    public FieldError getFieldError(String field) {
        String fixedField = this.fixedField(field);
        for (ObjectError objectError : this.errors) {
            FieldError fieldError;
            if (!(objectError instanceof FieldError) || !this.isMatchingFieldError(fixedField, fieldError = (FieldError)objectError)) continue;
            return fieldError;
        }
        return null;
    }

    @Override
    @Nullable
    public Object getFieldValue(String field) {
        FieldError fieldError = this.getFieldError(field);
        if (fieldError != null) {
            Object value = fieldError.getRejectedValue();
            return fieldError.isBindingFailure() || this.getTarget() == null ? value : this.formatFieldValue(field, value);
        }
        if (this.getTarget() != null) {
            Object value = this.getActualFieldValue(this.fixedField(field));
            return this.formatFieldValue(field, value);
        }
        return this.fieldValues.get(field);
    }

    @Override
    @Nullable
    public Class<?> getFieldType(@Nullable String field) {
        Object value;
        if (this.getTarget() != null && (value = this.getActualFieldValue(this.fixedField(field))) != null) {
            return value.getClass();
        }
        return this.fieldTypes.get(field);
    }

    @Override
    public Map<String, Object> getModel() {
        LinkedHashMap<String, Object> model = new LinkedHashMap<String, Object>(2);
        model.put(this.getObjectName(), this.getTarget());
        model.put(MODEL_KEY_PREFIX + this.getObjectName(), this);
        return model;
    }

    @Override
    @Nullable
    public Object getRawFieldValue(String field) {
        return this.getTarget() != null ? this.getActualFieldValue(this.fixedField(field)) : null;
    }

    @Override
    @Nullable
    public PropertyEditor findEditor(@Nullable String field, @Nullable Class<?> valueType) {
        PropertyEditorRegistry editorRegistry = this.getPropertyEditorRegistry();
        if (editorRegistry != null) {
            Class<?> valueTypeToUse = valueType;
            if (valueTypeToUse == null) {
                valueTypeToUse = this.getFieldType(field);
            }
            return editorRegistry.findCustomEditor(valueTypeToUse, this.fixedField(field));
        }
        return null;
    }

    @Override
    @Nullable
    public PropertyEditorRegistry getPropertyEditorRegistry() {
        return null;
    }

    @Override
    public String[] resolveMessageCodes(String errorCode) {
        return this.getMessageCodesResolver().resolveMessageCodes(errorCode, this.getObjectName());
    }

    @Override
    public String[] resolveMessageCodes(String errorCode, @Nullable String field) {
        return this.getMessageCodesResolver().resolveMessageCodes(errorCode, this.getObjectName(), this.fixedField(field), this.getFieldType(field));
    }

    @Override
    public void addError(ObjectError error) {
        this.errors.add(error);
    }

    @Override
    public void recordFieldValue(String field, Class<?> type, @Nullable Object value) {
        this.fieldTypes.put(field, type);
        this.fieldValues.put(field, value);
    }

    @Override
    public void recordSuppressedField(String field) {
        this.suppressedFields.add(field);
    }

    @Override
    public String[] getSuppressedFields() {
        return StringUtils.toStringArray(this.suppressedFields);
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof BindingResult)) {
            return false;
        }
        BindingResult otherResult = (BindingResult)other;
        return this.getObjectName().equals(otherResult.getObjectName()) && ObjectUtils.nullSafeEquals(this.getTarget(), otherResult.getTarget()) && this.getAllErrors().equals(otherResult.getAllErrors());
    }

    public int hashCode() {
        return this.getObjectName().hashCode();
    }

    @Override
    @Nullable
    public abstract Object getTarget();

    @Nullable
    protected abstract Object getActualFieldValue(String var1);

    @Nullable
    protected Object formatFieldValue(String field, @Nullable Object value) {
        return value;
    }
}

