/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.AbstractLabelableEntityObject
 *  com.atlassian.confluence.core.service.ServiceCommand
 *  com.atlassian.confluence.core.service.ValidationError
 *  com.atlassian.confluence.labels.Labelable
 *  com.atlassian.confluence.labels.PermittedLabelView
 *  com.atlassian.confluence.legacyapi.model.content.Label
 *  com.atlassian.confluence.legacyapi.model.content.Label$Prefix
 *  com.atlassian.confluence.util.I18NSupport
 *  com.atlassian.user.User
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.ui.rest.service.content;

import com.atlassian.confluence.core.AbstractLabelableEntityObject;
import com.atlassian.confluence.core.service.ServiceCommand;
import com.atlassian.confluence.core.service.ValidationError;
import com.atlassian.confluence.labels.Labelable;
import com.atlassian.confluence.labels.PermittedLabelView;
import com.atlassian.confluence.legacyapi.model.content.Label;
import com.atlassian.confluence.util.I18NSupport;
import com.atlassian.user.User;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

@Deprecated
public class LegacyLabelHelper {
    public static Iterable<Label> extractViewableLabels(AbstractLabelableEntityObject labelable, Collection<Label.Prefix> prefixes, User user) {
        List labels = new PermittedLabelView((Labelable)labelable, user, false).getLabels();
        return labels.stream().filter(input -> prefixes.contains(Label.Prefix.valueOf((String)input.getNamespace().getPrefix()))).collect(Collectors.toList()).stream().map(input -> new Label(input.getNamespace().getPrefix(), input.getName(), String.valueOf(input.getId()))).collect(Collectors.toList());
    }

    public static String concatentateLabels(Iterable<Label> labels) {
        StringBuilder fullLabel = new StringBuilder();
        for (Label label : labels) {
            String prefix = StringUtils.isEmpty((CharSequence)label.getPrefix()) ? "" : label.getPrefix() + ":";
            fullLabel.append(prefix).append(label.getLabel()).append(" ");
        }
        return fullLabel.toString().trim();
    }

    public static void validateLabelsCommand(ServiceCommand command) throws IllegalArgumentException {
        if (!command.isValid()) {
            ValidationError error = (ValidationError)command.getValidationErrors().iterator().next();
            String errorMessage = I18NSupport.getText((String)error.getMessageKey(), (Object[])error.getArgs());
            throw new IllegalArgumentException(errorMessage);
        }
    }
}

