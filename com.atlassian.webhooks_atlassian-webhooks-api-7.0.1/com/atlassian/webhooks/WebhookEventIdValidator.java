/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.ConstraintValidator
 *  javax.validation.ConstraintValidatorContext
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.webhooks;

import com.atlassian.webhooks.WebhookEventId;
import com.atlassian.webhooks.WebhookService;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebhookEventIdValidator
implements ConstraintValidator<WebhookEventId, String> {
    private static final Logger log = LoggerFactory.getLogger(WebhookEventIdValidator.class);
    private static WebhookService webhookService;
    private String message;

    public void initialize(WebhookEventId constraintAnnotation) {
        this.message = constraintAnnotation.message();
    }

    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (webhookService == null) {
            log.warn("Cannot validate whether '{}' is a valid webhook event because the validator has not (yet) been initialized. Assuming it's fine.", (Object)value);
            return true;
        }
        if (!webhookService.getEvent(value).isPresent()) {
            context.buildConstraintViolationWithTemplate(this.message).addConstraintViolation();
            return false;
        }
        return true;
    }

    protected static void setWebhookService(WebhookService value) {
        webhookService = value;
    }
}

