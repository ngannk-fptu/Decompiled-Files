/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.xwork2.ActionInvocation
 *  com.opensymphony.xwork2.interceptor.PreResultListener
 */
package com.atlassian.confluence.validation;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.util.i18n.Message;
import com.atlassian.confluence.validation.MessageHolder;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.PreResultListener;
import java.util.List;
import java.util.Map;

public class MessageLoadingPreResultListener
implements PreResultListener {
    private static final PreResultListener INSTANCE = new MessageLoadingPreResultListener();

    private MessageLoadingPreResultListener() {
    }

    public static PreResultListener getInstance() {
        return INSTANCE;
    }

    public void beforeResult(ActionInvocation invocation, String resultCode) {
        ConfluenceActionSupport action = (ConfluenceActionSupport)invocation.getAction();
        MessageHolder messageHolder = action.getMessageHolder();
        for (Message error : messageHolder.getActionErrors()) {
            action.addActionError(error.getKey(), error.getArguments());
        }
        for (Message success : messageHolder.getActionSuccesses()) {
            action.addActionMessage(success.getKey(), success.getArguments());
        }
        Map<String, List<Message>> fieldErrors = messageHolder.getFieldErrors();
        fieldErrors.forEach((field, errors) -> {
            for (Message message : errors) {
                action.addFieldError((String)field, message.getKey(), message.getArguments());
            }
        });
    }
}

