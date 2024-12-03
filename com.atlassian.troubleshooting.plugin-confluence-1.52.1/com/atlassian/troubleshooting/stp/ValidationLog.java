/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.troubleshooting.stp;

import com.atlassian.troubleshooting.stp.action.DefaultMessage;
import com.atlassian.troubleshooting.stp.action.Message;
import com.atlassian.troubleshooting.stp.salext.SupportApplicationInfo;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ValidationLog {
    private final SupportApplicationInfo appInfo;
    private final List<Message> errors = new ArrayList<Message>();
    private final Map<String, List<Message>> fieldErrors = new HashMap<String, List<Message>>();
    private final List<Message> warnings = new ArrayList<Message>();
    private final Map<String, List<Message>> fieldWarnings = new HashMap<String, List<Message>>();
    private final List<Message> feedback = new ArrayList<Message>();

    public ValidationLog(SupportApplicationInfo appInfo) {
        this.appInfo = Objects.requireNonNull(appInfo);
    }

    public void addError(Message error) {
        this.errors.add(error);
    }

    public void addError(String i18nKey, Serializable ... arguments) {
        this.addLocalizedError(this.appInfo.getText(i18nKey, arguments));
    }

    public void addFeedback(Message feedback) {
        this.feedback.add(feedback);
    }

    public void addFeedback(String i18nKey, Serializable ... arguments) {
        this.addLocalizedFeedback(this.appInfo.getText(i18nKey, arguments));
    }

    public void addFieldError(String fieldName, Message error) {
        this.addItem(this.fieldErrors, fieldName, error);
        this.addError(error);
    }

    public void addFieldError(String fieldName, String body) {
        String localizedText = this.appInfo.getText(body);
        DefaultMessage error = new DefaultMessage(localizedText, localizedText);
        this.addItem(this.fieldErrors, fieldName, error);
        this.addError(error);
    }

    public void addFieldError(String fieldName, String i18nKey, Serializable ... i18nParameters) {
        String localizedText = this.appInfo.getText(i18nKey, i18nParameters);
        DefaultMessage error = new DefaultMessage(localizedText, localizedText);
        this.addItem(this.fieldErrors, fieldName, error);
        this.addError(error);
    }

    public void addFieldWarning(String fieldName, Message warning) {
        this.addItem(this.fieldWarnings, fieldName, warning);
        this.addWarning(warning);
    }

    public void addFieldWarning(String fieldName, String body) {
        String localizedText = this.appInfo.getText(body);
        DefaultMessage warning = new DefaultMessage(localizedText, localizedText);
        this.addItem(this.fieldWarnings, fieldName, warning);
        this.addWarning(warning);
    }

    public void addFieldWarning(String fieldName, String i18nKey, Serializable ... i18nParameters) {
        String localizedText = this.appInfo.getText(i18nKey, i18nParameters);
        DefaultMessage warning = new DefaultMessage(localizedText, localizedText);
        this.addItem(this.fieldWarnings, fieldName, warning);
        this.addWarning(warning);
    }

    private <T extends Message> void addItem(Map<String, List<T>> map, String fieldName, T message) {
        List list = map.computeIfAbsent(fieldName, k -> new ArrayList());
        list.add(message);
    }

    public void addLocalizedError(String errorText) {
        this.addError(new DefaultMessage(errorText, errorText));
    }

    private void addLocalizedFeedback(String feedbackText) {
        this.addFeedback(new DefaultMessage(feedbackText, feedbackText));
    }

    public void addLocalizedWarning(String warningText) {
        this.addWarning(new DefaultMessage(warningText, warningText));
    }

    public void addWarning(Message warning) {
        this.warnings.add(warning);
    }

    public void addWarning(String i18nKey, Serializable ... arguments) {
        this.addLocalizedWarning(this.appInfo.getText(i18nKey, arguments));
    }

    public List<Message> getErrors() {
        return this.errors;
    }

    public List<Message> getFeedback() {
        return this.feedback;
    }

    public List<Message> getFieldErrors(String fieldName) {
        return this.getFieldMessages(fieldName, this.fieldErrors);
    }

    public <T extends Message> List<T> getFieldMessages(String fieldName, Map<String, List<T>> messages) {
        List<T> list = messages.get(fieldName);
        if (list != null) {
            return list;
        }
        return Collections.emptyList();
    }

    public List<Message> getFieldWarnings(String fieldName) {
        return this.getFieldMessages(fieldName, this.fieldWarnings);
    }

    public List<Message> getWarnings() {
        return this.warnings;
    }

    public boolean hasErrors() {
        return !this.errors.isEmpty() || !this.fieldErrors.isEmpty();
    }

    public boolean hasFeedback() {
        return !this.feedback.isEmpty();
    }

    public boolean hasFieldErrors(String fieldName) {
        return this.fieldErrors.get(fieldName) != null && !this.fieldErrors.get(fieldName).isEmpty();
    }

    public boolean hasFieldWarnings(String fieldName) {
        return this.fieldWarnings.get(fieldName) != null && !this.fieldWarnings.get(fieldName).isEmpty();
    }

    public boolean hasWarnings() {
        return !this.warnings.isEmpty() || !this.fieldWarnings.isEmpty();
    }
}

