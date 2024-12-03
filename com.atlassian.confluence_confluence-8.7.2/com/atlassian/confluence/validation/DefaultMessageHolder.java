/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.validation;

import com.atlassian.confluence.util.i18n.Message;
import com.atlassian.confluence.validation.MessageHolder;
import com.atlassian.confluence.validation.MessageLevel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultMessageHolder
implements MessageHolder {
    private Map<MessageLevel, List<Message>> actionMessages = new HashMap<MessageLevel, List<Message>>();
    private Map<String, List<Message>> fieldErrors = new HashMap<String, List<Message>>();

    DefaultMessageHolder() {
    }

    @Override
    public boolean hasErrors() {
        return !this.get(MessageLevel.ERROR).isEmpty() || !this.fieldErrors.isEmpty();
    }

    @Override
    public void addFieldError(String field, String messageKey) {
        List<Message> fieldErrorList = this.fieldErrors.get(field);
        if (fieldErrorList == null) {
            fieldErrorList = new ArrayList<Message>();
            this.fieldErrors.put(field, fieldErrorList);
        }
        fieldErrorList.add(Message.getInstance(messageKey));
    }

    @Override
    public void addActionError(String messageKey) {
        this.add(MessageLevel.ERROR, Message.getInstance(messageKey));
    }

    @Override
    public void addActionError(String messageKey, Object ... args) {
        this.add(MessageLevel.ERROR, Message.getInstance(messageKey, args));
    }

    @Override
    public void addActionWarning(String messageKey, Object ... args) {
        this.add(MessageLevel.WARNING, Message.getInstance(messageKey, args));
    }

    @Override
    public void addActionInfo(String messageKey, Object ... args) {
        this.add(MessageLevel.INFO, Message.getInstance(messageKey, args));
    }

    @Override
    public void addActionSuccess(String messageKey, Object ... args) {
        this.add(MessageLevel.SUCCESS, Message.getInstance(messageKey, args));
    }

    @Override
    public List<Message> getActionErrors() {
        return this.getActionMessages(MessageLevel.ERROR);
    }

    @Override
    public List<Message> getActionWarnings() {
        return this.getActionMessages(MessageLevel.WARNING);
    }

    @Override
    public List<Message> getActionInfos() {
        return this.getActionMessages(MessageLevel.INFO);
    }

    @Override
    public List<Message> getActionSuccesses() {
        return this.getActionMessages(MessageLevel.SUCCESS);
    }

    public List<Message> getActionMessages(MessageLevel level) {
        if (!this.actionMessages.containsKey((Object)level)) {
            return new ArrayList<Message>();
        }
        return new ArrayList<Message>(this.get(level));
    }

    @Override
    public Map<String, List<Message>> getFieldErrors() {
        return new HashMap<String, List<Message>>(this.fieldErrors);
    }

    private void add(MessageLevel level, Message message) {
        List<Message> levelMessages = this.actionMessages.get((Object)level);
        if (levelMessages == null) {
            levelMessages = new ArrayList<Message>();
            this.actionMessages.put(level, levelMessages);
        }
        levelMessages.add(message);
    }

    private List<Message> get(MessageLevel level) {
        if (!this.actionMessages.containsKey((Object)level)) {
            return new ArrayList<Message>();
        }
        return this.actionMessages.get((Object)level);
    }
}

