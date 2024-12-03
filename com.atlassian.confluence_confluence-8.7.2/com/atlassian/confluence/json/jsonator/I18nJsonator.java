/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.json.jsonator;

import com.atlassian.confluence.json.json.Json;
import com.atlassian.confluence.json.json.JsonString;
import com.atlassian.confluence.json.jsonator.Jsonator;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.Message;

public class I18nJsonator
implements Jsonator<Message> {
    private final I18NBean i18NBean;

    public I18nJsonator(I18NBean i18NBean) {
        this.i18NBean = i18NBean;
    }

    @Override
    public Json convert(Message m) {
        String value = this.i18NBean.getText(m);
        return new JsonString(value);
    }
}

