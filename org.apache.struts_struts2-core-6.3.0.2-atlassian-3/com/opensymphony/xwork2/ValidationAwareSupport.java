/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2;

import com.opensymphony.xwork2.interceptor.ValidationAware;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ValidationAwareSupport
implements ValidationAware,
Serializable {
    private Collection<String> actionErrors;
    private Collection<String> actionMessages;
    private Map<String, List<String>> fieldErrors;

    @Override
    public synchronized void setActionErrors(Collection<String> errorMessages) {
        this.actionErrors = errorMessages;
    }

    @Override
    public synchronized Collection<String> getActionErrors() {
        return new LinkedList<String>(this.internalGetActionErrors());
    }

    @Override
    public synchronized void setActionMessages(Collection<String> messages) {
        this.actionMessages = messages;
    }

    @Override
    public synchronized Collection<String> getActionMessages() {
        return new LinkedList<String>(this.internalGetActionMessages());
    }

    @Override
    public synchronized void setFieldErrors(Map<String, List<String>> errorMap) {
        this.fieldErrors = errorMap;
    }

    @Override
    public synchronized Map<String, List<String>> getFieldErrors() {
        return new LinkedHashMap<String, List<String>>(this.internalGetFieldErrors());
    }

    @Override
    public synchronized void addActionError(String anErrorMessage) {
        this.internalGetActionErrors().add(anErrorMessage);
    }

    @Override
    public synchronized void addActionMessage(String aMessage) {
        this.internalGetActionMessages().add(aMessage);
    }

    @Override
    public synchronized void addFieldError(String fieldName, String errorMessage) {
        Map<String, List<String>> errors = this.internalGetFieldErrors();
        List<String> thisFieldErrors = errors.get(fieldName);
        if (thisFieldErrors == null) {
            thisFieldErrors = new ArrayList<String>();
            errors.put(fieldName, thisFieldErrors);
        }
        thisFieldErrors.add(errorMessage);
    }

    @Override
    public synchronized boolean hasActionErrors() {
        return this.actionErrors != null && !this.actionErrors.isEmpty();
    }

    @Override
    public synchronized boolean hasActionMessages() {
        return this.actionMessages != null && !this.actionMessages.isEmpty();
    }

    @Override
    public synchronized boolean hasErrors() {
        return this.hasActionErrors() || this.hasFieldErrors();
    }

    @Override
    public synchronized boolean hasFieldErrors() {
        return this.fieldErrors != null && !this.fieldErrors.isEmpty();
    }

    private Collection<String> internalGetActionErrors() {
        if (this.actionErrors == null) {
            this.actionErrors = new ArrayList<String>();
        }
        return this.actionErrors;
    }

    private Collection<String> internalGetActionMessages() {
        if (this.actionMessages == null) {
            this.actionMessages = new ArrayList<String>();
        }
        return this.actionMessages;
    }

    private Map<String, List<String>> internalGetFieldErrors() {
        if (this.fieldErrors == null) {
            this.fieldErrors = new LinkedHashMap<String, List<String>>();
        }
        return this.fieldErrors;
    }

    public synchronized void clearFieldErrors() {
        this.internalGetFieldErrors().clear();
    }

    public synchronized void clearActionErrors() {
        this.internalGetActionErrors().clear();
    }

    public synchronized void clearMessages() {
        this.internalGetActionMessages().clear();
    }

    public synchronized void clearErrors() {
        this.internalGetFieldErrors().clear();
        this.internalGetActionErrors().clear();
    }

    public synchronized void clearErrorsAndMessages() {
        this.internalGetFieldErrors().clear();
        this.internalGetActionErrors().clear();
        this.internalGetActionMessages().clear();
    }
}

