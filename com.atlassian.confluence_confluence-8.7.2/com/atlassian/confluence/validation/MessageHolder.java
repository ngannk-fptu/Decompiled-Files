/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.validation;

import com.atlassian.confluence.util.i18n.Message;
import java.util.List;
import java.util.Map;

public interface MessageHolder {
    public boolean hasErrors();

    public void addFieldError(String var1, String var2);

    public void addActionError(String var1);

    public void addActionError(String var1, Object ... var2);

    public void addActionWarning(String var1, Object ... var2);

    public void addActionInfo(String var1, Object ... var2);

    public void addActionSuccess(String var1, Object ... var2);

    public List<Message> getActionErrors();

    public List<Message> getActionWarnings();

    public List<Message> getActionInfos();

    public List<Message> getActionSuccesses();

    public Map<String, List<Message>> getFieldErrors();
}

