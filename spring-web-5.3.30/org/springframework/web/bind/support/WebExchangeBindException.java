/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.PropertyEditorRegistry
 *  org.springframework.core.MethodParameter
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.validation.BindingResult
 *  org.springframework.validation.Errors
 *  org.springframework.validation.FieldError
 *  org.springframework.validation.ObjectError
 */
package org.springframework.web.bind.support;

import java.beans.PropertyEditor;
import java.util.List;
import java.util.Map;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.server.ServerWebInputException;

public class WebExchangeBindException
extends ServerWebInputException
implements BindingResult {
    private final BindingResult bindingResult;

    public WebExchangeBindException(MethodParameter parameter, BindingResult bindingResult) {
        super("Validation failure", parameter);
        this.bindingResult = bindingResult;
    }

    public final BindingResult getBindingResult() {
        return this.bindingResult;
    }

    public String getObjectName() {
        return this.bindingResult.getObjectName();
    }

    public void setNestedPath(String nestedPath) {
        this.bindingResult.setNestedPath(nestedPath);
    }

    public String getNestedPath() {
        return this.bindingResult.getNestedPath();
    }

    public void pushNestedPath(String subPath) {
        this.bindingResult.pushNestedPath(subPath);
    }

    public void popNestedPath() throws IllegalStateException {
        this.bindingResult.popNestedPath();
    }

    public void reject(String errorCode) {
        this.bindingResult.reject(errorCode);
    }

    public void reject(String errorCode, String defaultMessage) {
        this.bindingResult.reject(errorCode, defaultMessage);
    }

    public void reject(String errorCode, @Nullable Object[] errorArgs, @Nullable String defaultMessage) {
        this.bindingResult.reject(errorCode, errorArgs, defaultMessage);
    }

    public void rejectValue(@Nullable String field, String errorCode) {
        this.bindingResult.rejectValue(field, errorCode);
    }

    public void rejectValue(@Nullable String field, String errorCode, String defaultMessage) {
        this.bindingResult.rejectValue(field, errorCode, defaultMessage);
    }

    public void rejectValue(@Nullable String field, String errorCode, @Nullable Object[] errorArgs, @Nullable String defaultMessage) {
        this.bindingResult.rejectValue(field, errorCode, errorArgs, defaultMessage);
    }

    public void addAllErrors(Errors errors) {
        this.bindingResult.addAllErrors(errors);
    }

    public boolean hasErrors() {
        return this.bindingResult.hasErrors();
    }

    public int getErrorCount() {
        return this.bindingResult.getErrorCount();
    }

    public List<ObjectError> getAllErrors() {
        return this.bindingResult.getAllErrors();
    }

    public boolean hasGlobalErrors() {
        return this.bindingResult.hasGlobalErrors();
    }

    public int getGlobalErrorCount() {
        return this.bindingResult.getGlobalErrorCount();
    }

    public List<ObjectError> getGlobalErrors() {
        return this.bindingResult.getGlobalErrors();
    }

    @Nullable
    public ObjectError getGlobalError() {
        return this.bindingResult.getGlobalError();
    }

    public boolean hasFieldErrors() {
        return this.bindingResult.hasFieldErrors();
    }

    public int getFieldErrorCount() {
        return this.bindingResult.getFieldErrorCount();
    }

    public List<FieldError> getFieldErrors() {
        return this.bindingResult.getFieldErrors();
    }

    @Nullable
    public FieldError getFieldError() {
        return this.bindingResult.getFieldError();
    }

    public boolean hasFieldErrors(String field) {
        return this.bindingResult.hasFieldErrors(field);
    }

    public int getFieldErrorCount(String field) {
        return this.bindingResult.getFieldErrorCount(field);
    }

    public List<FieldError> getFieldErrors(String field) {
        return this.bindingResult.getFieldErrors(field);
    }

    @Nullable
    public FieldError getFieldError(String field) {
        return this.bindingResult.getFieldError(field);
    }

    @Nullable
    public Object getFieldValue(String field) {
        return this.bindingResult.getFieldValue(field);
    }

    @Nullable
    public Class<?> getFieldType(String field) {
        return this.bindingResult.getFieldType(field);
    }

    @Nullable
    public Object getTarget() {
        return this.bindingResult.getTarget();
    }

    public Map<String, Object> getModel() {
        return this.bindingResult.getModel();
    }

    @Nullable
    public Object getRawFieldValue(String field) {
        return this.bindingResult.getRawFieldValue(field);
    }

    @Nullable
    public PropertyEditor findEditor(@Nullable String field, @Nullable Class valueType) {
        return this.bindingResult.findEditor(field, valueType);
    }

    @Nullable
    public PropertyEditorRegistry getPropertyEditorRegistry() {
        return this.bindingResult.getPropertyEditorRegistry();
    }

    public String[] resolveMessageCodes(String errorCode) {
        return this.bindingResult.resolveMessageCodes(errorCode);
    }

    public String[] resolveMessageCodes(String errorCode, String field) {
        return this.bindingResult.resolveMessageCodes(errorCode, field);
    }

    public void addError(ObjectError error) {
        this.bindingResult.addError(error);
    }

    public void recordFieldValue(String field, Class<?> type, @Nullable Object value) {
        this.bindingResult.recordFieldValue(field, type, value);
    }

    public void recordSuppressedField(String field) {
        this.bindingResult.recordSuppressedField(field);
    }

    public String[] getSuppressedFields() {
        return this.bindingResult.getSuppressedFields();
    }

    @Override
    public String getMessage() {
        MethodParameter parameter = this.getMethodParameter();
        Assert.state((parameter != null ? 1 : 0) != 0, (String)"No MethodParameter");
        StringBuilder sb = new StringBuilder("Validation failed for argument at index ").append(parameter.getParameterIndex()).append(" in method: ").append(parameter.getExecutable().toGenericString()).append(", with ").append(this.getErrorCount()).append(" error(s): ");
        for (ObjectError error : this.getAllErrors()) {
            sb.append('[').append(error).append("] ");
        }
        return sb.toString();
    }

    public boolean equals(@Nullable Object other) {
        return this == other || this.bindingResult.equals(other);
    }

    public int hashCode() {
        return this.bindingResult.hashCode();
    }
}

