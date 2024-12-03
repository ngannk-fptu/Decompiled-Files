/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.I18nResolver
 */
package com.atlassian.business.insights.core.rest.exception.mapper;

import com.atlassian.business.insights.core.rest.validation.DiagnosticDescription;
import com.atlassian.business.insights.core.rest.validation.ValidationError;
import com.atlassian.sal.api.message.I18nResolver;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public class DiagnosticDescriptionTranslator {
    private final I18nResolver i18nResolver;

    public DiagnosticDescriptionTranslator(I18nResolver i18nResolver) {
        this.i18nResolver = i18nResolver;
    }

    public List<DiagnosticDescription> translateDescriptions(List<DiagnosticDescription> descriptions) {
        return descriptions.stream().map(this::translateDescription).collect(Collectors.toList());
    }

    public List<DiagnosticDescription> translateValidationErrors(List<ValidationError> validationErrors) {
        return validationErrors.stream().map(this::translateValidationError).collect(Collectors.toList());
    }

    private DiagnosticDescription translateDescription(DiagnosticDescription description) {
        String translatedMessage = this.i18nResolver.getText(description.getKey());
        if (translatedMessage.equals(description.getKey())) {
            return description;
        }
        return new DiagnosticDescription(description.getKey(), translatedMessage);
    }

    private DiagnosticDescription translateValidationError(ValidationError validationError) {
        String[] errorParameters = validationError.getErrorMessageParameters().toArray(new String[0]);
        String translatedMessage = this.i18nResolver.getText(validationError.getKey(), (Serializable[])errorParameters);
        return new DiagnosticDescription(validationError.getKey(), translatedMessage);
    }
}

