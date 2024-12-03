/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.sal.api.message.Message
 *  com.google.common.base.Function
 */
package com.atlassian.confluence.extra.calendar3.rest.resources;

import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.sal.api.message.Message;
import com.google.common.base.Function;
import java.util.Arrays;
import java.util.Collections;

public class MessageToStringTransformerFunction
implements Function<Message, String> {
    private final I18NBean i18NBean;

    public MessageToStringTransformerFunction(I18NBean i18NBean) {
        this.i18NBean = i18NBean;
    }

    public String apply(Message message) {
        return this.i18NBean.getText(message.getKey(), null == message.getArguments() ? Collections.emptyList() : Arrays.asList(message.getArguments()));
    }
}

