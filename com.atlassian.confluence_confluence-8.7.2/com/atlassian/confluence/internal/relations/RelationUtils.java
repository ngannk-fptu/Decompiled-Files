/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.Space
 *  com.atlassian.confluence.api.model.messages.Message
 *  com.atlassian.confluence.api.model.people.User
 *  com.atlassian.confluence.api.model.relations.RelationDescriptor
 *  com.atlassian.confluence.api.model.validation.SimpleValidationResult
 *  com.atlassian.confluence.api.model.validation.SimpleValidationResult$Builder
 *  com.atlassian.confluence.api.model.validation.ValidationError
 *  com.atlassian.confluence.api.model.validation.ValidationResult
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.internal.relations;

import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.Space;
import com.atlassian.confluence.api.model.messages.Message;
import com.atlassian.confluence.api.model.people.User;
import com.atlassian.confluence.api.model.relations.RelationDescriptor;
import com.atlassian.confluence.api.model.validation.SimpleValidationResult;
import com.atlassian.confluence.api.model.validation.ValidationError;
import com.atlassian.confluence.api.model.validation.ValidationResult;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.internal.relations.RelatableEntity;
import com.atlassian.confluence.spaces.SpaceDescription;
import com.atlassian.confluence.user.ConfluenceUser;
import org.apache.commons.lang3.StringUtils;

public class RelationUtils {
    public static ValidationResult validateAgainstApiModel(RelatableEntity source, RelatableEntity target, RelationDescriptor relationDescriptor) {
        Class hibernateSourceClass = RelationUtils.getCorrespondingHibernateClass(relationDescriptor.getSourceClass());
        Class hibernateTargetClass = RelationUtils.getCorrespondingHibernateClass(relationDescriptor.getTargetClass());
        SimpleValidationResult.Builder validationResultBuilder = SimpleValidationResult.builder().authorized(true);
        if (hibernateSourceClass == null || hibernateTargetClass == null) {
            validationResultBuilder.addError("Not able to find corresponding hibernate objects", new Object[0]);
        } else if (!hibernateSourceClass.isAssignableFrom(source.getClass())) {
            validationResultBuilder.addError(String.format("Invalid source type: %s expected, but was %s", hibernateSourceClass, source.getClass()), new Object[0]);
        } else if (!hibernateTargetClass.isAssignableFrom(target.getClass())) {
            validationResultBuilder.addError(String.format("Invalid target type: %s expected, but was %s", hibernateTargetClass, target.getClass()), new Object[0]);
        }
        return validationResultBuilder.build();
    }

    private static Class getCorrespondingHibernateClass(Class sourceClass) {
        if (sourceClass != null) {
            if (User.class.isAssignableFrom(sourceClass)) {
                return ConfluenceUser.class;
            }
            if (Space.class.isAssignableFrom(sourceClass)) {
                return SpaceDescription.class;
            }
            if (Content.class.isAssignableFrom(sourceClass)) {
                return ContentEntityObject.class;
            }
        }
        return null;
    }

    public static String extractError(ValidationResult validationResult, String defaultMessage) {
        String resultStr = defaultMessage;
        if (validationResult.getErrors() != null) {
            StringBuilder sb = new StringBuilder();
            for (ValidationError validationError : validationResult.getErrors()) {
                if (validationError.getMessage() == null) continue;
                Message message = validationError.getMessage();
                if (StringUtils.isNotBlank((CharSequence)message.getTranslation())) {
                    sb.append(message.getTranslation()).append('\n');
                    continue;
                }
                if (!StringUtils.isNotBlank((CharSequence)message.getKey())) continue;
                sb.append(message.getKey()).append('\n');
            }
            if (sb.length() != 0) {
                resultStr = sb.toString();
            }
        }
        return resultStr;
    }
}

