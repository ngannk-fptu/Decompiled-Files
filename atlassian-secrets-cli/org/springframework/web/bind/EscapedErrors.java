/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.bind;

import java.util.ArrayList;
import java.util.List;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.util.HtmlUtils;

public class EscapedErrors
implements Errors {
    private final Errors source;

    public EscapedErrors(Errors source) {
        Assert.notNull((Object)source, "Errors source must not be null");
        this.source = source;
    }

    public Errors getSource() {
        return this.source;
    }

    @Override
    public String getObjectName() {
        return this.source.getObjectName();
    }

    @Override
    public void setNestedPath(String nestedPath) {
        this.source.setNestedPath(nestedPath);
    }

    @Override
    public String getNestedPath() {
        return this.source.getNestedPath();
    }

    @Override
    public void pushNestedPath(String subPath) {
        this.source.pushNestedPath(subPath);
    }

    @Override
    public void popNestedPath() throws IllegalStateException {
        this.source.popNestedPath();
    }

    @Override
    public void reject(String errorCode) {
        this.source.reject(errorCode);
    }

    @Override
    public void reject(String errorCode, String defaultMessage) {
        this.source.reject(errorCode, defaultMessage);
    }

    @Override
    public void reject(String errorCode, @Nullable Object[] errorArgs, @Nullable String defaultMessage) {
        this.source.reject(errorCode, errorArgs, defaultMessage);
    }

    @Override
    public void rejectValue(@Nullable String field, String errorCode) {
        this.source.rejectValue(field, errorCode);
    }

    @Override
    public void rejectValue(@Nullable String field, String errorCode, String defaultMessage) {
        this.source.rejectValue(field, errorCode, defaultMessage);
    }

    @Override
    public void rejectValue(@Nullable String field, String errorCode, @Nullable Object[] errorArgs, @Nullable String defaultMessage) {
        this.source.rejectValue(field, errorCode, errorArgs, defaultMessage);
    }

    @Override
    public void addAllErrors(Errors errors) {
        this.source.addAllErrors(errors);
    }

    @Override
    public boolean hasErrors() {
        return this.source.hasErrors();
    }

    @Override
    public int getErrorCount() {
        return this.source.getErrorCount();
    }

    @Override
    public List<ObjectError> getAllErrors() {
        return this.escapeObjectErrors(this.source.getAllErrors());
    }

    @Override
    public boolean hasGlobalErrors() {
        return this.source.hasGlobalErrors();
    }

    @Override
    public int getGlobalErrorCount() {
        return this.source.getGlobalErrorCount();
    }

    @Override
    public List<ObjectError> getGlobalErrors() {
        return this.escapeObjectErrors(this.source.getGlobalErrors());
    }

    @Override
    @Nullable
    public ObjectError getGlobalError() {
        return this.escapeObjectError(this.source.getGlobalError());
    }

    @Override
    public boolean hasFieldErrors() {
        return this.source.hasFieldErrors();
    }

    @Override
    public int getFieldErrorCount() {
        return this.source.getFieldErrorCount();
    }

    @Override
    public List<FieldError> getFieldErrors() {
        return this.source.getFieldErrors();
    }

    @Override
    @Nullable
    public FieldError getFieldError() {
        return this.source.getFieldError();
    }

    @Override
    public boolean hasFieldErrors(String field) {
        return this.source.hasFieldErrors(field);
    }

    @Override
    public int getFieldErrorCount(String field) {
        return this.source.getFieldErrorCount(field);
    }

    @Override
    public List<FieldError> getFieldErrors(String field) {
        return this.escapeObjectErrors(this.source.getFieldErrors(field));
    }

    @Override
    @Nullable
    public FieldError getFieldError(String field) {
        return this.escapeObjectError(this.source.getFieldError(field));
    }

    @Override
    @Nullable
    public Object getFieldValue(String field) {
        Object value = this.source.getFieldValue(field);
        return value instanceof String ? HtmlUtils.htmlEscape((String)value) : value;
    }

    @Override
    @Nullable
    public Class<?> getFieldType(String field) {
        return this.source.getFieldType(field);
    }

    @Nullable
    private <T extends ObjectError> T escapeObjectError(@Nullable T source) {
        if (source == null) {
            return null;
        }
        String defaultMessage = source.getDefaultMessage();
        if (defaultMessage != null) {
            defaultMessage = HtmlUtils.htmlEscape(defaultMessage);
        }
        if (source instanceof FieldError) {
            FieldError fieldError = (FieldError)source;
            Object value = fieldError.getRejectedValue();
            if (value instanceof String) {
                value = HtmlUtils.htmlEscape((String)value);
            }
            return (T)new FieldError(fieldError.getObjectName(), fieldError.getField(), value, fieldError.isBindingFailure(), fieldError.getCodes(), fieldError.getArguments(), defaultMessage);
        }
        return (T)new ObjectError(source.getObjectName(), source.getCodes(), source.getArguments(), defaultMessage);
    }

    private <T extends ObjectError> List<T> escapeObjectErrors(List<T> source) {
        ArrayList<ObjectError> escaped = new ArrayList<ObjectError>(source.size());
        for (ObjectError objectError : source) {
            escaped.add(this.escapeObjectError(objectError));
        }
        return escaped;
    }
}

