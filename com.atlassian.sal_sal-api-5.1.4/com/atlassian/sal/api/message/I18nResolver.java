/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.sal.api.message;

import com.atlassian.sal.api.message.Message;
import com.atlassian.sal.api.message.MessageCollection;
import java.io.Serializable;
import java.util.Locale;
import java.util.Map;

public interface I18nResolver {
    public String getRawText(String var1);

    public String getRawText(Locale var1, String var2);

    public String getText(String var1, Serializable ... var2);

    public String getText(Locale var1, String var2, Serializable ... var3);

    public String getText(String var1);

    public String getText(Locale var1, String var2);

    public String getText(Message var1);

    public String getText(Locale var1, Message var2);

    public Message createMessage(String var1, Serializable ... var2);

    public MessageCollection createMessageCollection();

    public Map<String, String> getAllTranslationsForPrefix(String var1);

    public Map<String, String> getAllTranslationsForPrefix(String var1, Locale var2);
}

