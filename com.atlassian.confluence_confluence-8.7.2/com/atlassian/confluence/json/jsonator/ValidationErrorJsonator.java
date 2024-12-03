/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.json.jsonator;

import com.atlassian.confluence.core.service.ValidationError;
import com.atlassian.confluence.json.json.Json;
import com.atlassian.confluence.json.json.JsonString;
import com.atlassian.confluence.json.jsonator.Jsonator;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;

public class ValidationErrorJsonator
implements Jsonator<ValidationError> {
    private I18NBeanFactory i18NBeanFactory;

    public ValidationErrorJsonator(I18NBeanFactory i18NBeanFactory) {
        this.i18NBeanFactory = i18NBeanFactory;
    }

    @Override
    public Json convert(ValidationError validationError) {
        String errorText = this.i18NBeanFactory.getI18NBean().getText(validationError.getMessageKey(), validationError.getArgs());
        return new JsonString(errorText);
    }
}

