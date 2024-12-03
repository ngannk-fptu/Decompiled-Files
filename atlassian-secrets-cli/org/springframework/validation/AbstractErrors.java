/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.validation;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

public abstract class AbstractErrors
implements Errors,
Serializable {
    private String nestedPath = "";
    private final Deque<String> nestedPathStack = new ArrayDeque<String>();

    @Override
    public void setNestedPath(@Nullable String nestedPath) {
        this.doSetNestedPath(nestedPath);
        this.nestedPathStack.clear();
    }

    @Override
    public String getNestedPath() {
        return this.nestedPath;
    }

    @Override
    public void pushNestedPath(String subPath) {
        this.nestedPathStack.push(this.getNestedPath());
        this.doSetNestedPath(this.getNestedPath() + subPath);
    }

    @Override
    public void popNestedPath() throws IllegalStateException {
        try {
            String formerNestedPath = this.nestedPathStack.pop();
            this.doSetNestedPath(formerNestedPath);
        }
        catch (NoSuchElementException ex) {
            throw new IllegalStateException("Cannot pop nested path: no nested path on stack");
        }
    }

    protected void doSetNestedPath(@Nullable String nestedPath) {
        if (nestedPath == null) {
            nestedPath = "";
        }
        if ((nestedPath = this.canonicalFieldName(nestedPath)).length() > 0 && !nestedPath.endsWith(".")) {
            nestedPath = nestedPath + ".";
        }
        this.nestedPath = nestedPath;
    }

    protected String fixedField(@Nullable String field) {
        if (StringUtils.hasLength(field)) {
            return this.getNestedPath() + this.canonicalFieldName(field);
        }
        String path = this.getNestedPath();
        return path.endsWith(".") ? path.substring(0, path.length() - ".".length()) : path;
    }

    protected String canonicalFieldName(String field) {
        return field;
    }

    @Override
    public void reject(String errorCode) {
        this.reject(errorCode, null, null);
    }

    @Override
    public void reject(String errorCode, String defaultMessage) {
        this.reject(errorCode, null, defaultMessage);
    }

    @Override
    public void rejectValue(@Nullable String field, String errorCode) {
        this.rejectValue(field, errorCode, null, null);
    }

    @Override
    public void rejectValue(@Nullable String field, String errorCode, String defaultMessage) {
        this.rejectValue(field, errorCode, null, defaultMessage);
    }

    @Override
    public boolean hasErrors() {
        return !this.getAllErrors().isEmpty();
    }

    @Override
    public int getErrorCount() {
        return this.getAllErrors().size();
    }

    @Override
    public List<ObjectError> getAllErrors() {
        LinkedList<ObjectError> result = new LinkedList<ObjectError>();
        result.addAll(this.getGlobalErrors());
        result.addAll(this.getFieldErrors());
        return Collections.unmodifiableList(result);
    }

    @Override
    public boolean hasGlobalErrors() {
        return this.getGlobalErrorCount() > 0;
    }

    @Override
    public int getGlobalErrorCount() {
        return this.getGlobalErrors().size();
    }

    @Override
    @Nullable
    public ObjectError getGlobalError() {
        List<ObjectError> globalErrors = this.getGlobalErrors();
        return !globalErrors.isEmpty() ? globalErrors.get(0) : null;
    }

    @Override
    public boolean hasFieldErrors() {
        return this.getFieldErrorCount() > 0;
    }

    @Override
    public int getFieldErrorCount() {
        return this.getFieldErrors().size();
    }

    @Override
    @Nullable
    public FieldError getFieldError() {
        List<FieldError> fieldErrors = this.getFieldErrors();
        return !fieldErrors.isEmpty() ? fieldErrors.get(0) : null;
    }

    @Override
    public boolean hasFieldErrors(String field) {
        return this.getFieldErrorCount(field) > 0;
    }

    @Override
    public int getFieldErrorCount(String field) {
        return this.getFieldErrors(field).size();
    }

    @Override
    public List<FieldError> getFieldErrors(String field) {
        List<FieldError> fieldErrors = this.getFieldErrors();
        LinkedList<FieldError> result = new LinkedList<FieldError>();
        String fixedField = this.fixedField(field);
        for (FieldError error : fieldErrors) {
            if (!this.isMatchingFieldError(fixedField, error)) continue;
            result.add(error);
        }
        return Collections.unmodifiableList(result);
    }

    @Override
    @Nullable
    public FieldError getFieldError(String field) {
        List<FieldError> fieldErrors = this.getFieldErrors(field);
        return !fieldErrors.isEmpty() ? fieldErrors.get(0) : null;
    }

    @Override
    @Nullable
    public Class<?> getFieldType(String field) {
        Object value = this.getFieldValue(field);
        return value != null ? value.getClass() : null;
    }

    protected boolean isMatchingFieldError(String field, FieldError fieldError) {
        if (field.equals(fieldError.getField())) {
            return true;
        }
        int endIndex = field.length() - 1;
        return endIndex >= 0 && field.charAt(endIndex) == '*' && (endIndex == 0 || field.regionMatches(0, fieldError.getField(), 0, endIndex));
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(this.getClass().getName());
        sb.append(": ").append(this.getErrorCount()).append(" errors");
        for (ObjectError error : this.getAllErrors()) {
            sb.append('\n').append(error);
        }
        return sb.toString();
    }
}

