/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.Constraint
 *  javax.validation.Payload
 *  javax.validation.ReportAsSingleViolation
 */
package com.atlassian.webhooks;

import com.atlassian.webhooks.WebhookEventIdValidator;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;

@Documented
@Constraint(validatedBy={WebhookEventIdValidator.class})
@Target(value={ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER})
@Retention(value=RetentionPolicy.RUNTIME)
@ReportAsSingleViolation
public @interface WebhookEventId {
    public String message() default "{webhooks.unknown.event}";

    public Class<?>[] groups() default {};

    public Class<? extends Payload>[] payload() default {};
}

