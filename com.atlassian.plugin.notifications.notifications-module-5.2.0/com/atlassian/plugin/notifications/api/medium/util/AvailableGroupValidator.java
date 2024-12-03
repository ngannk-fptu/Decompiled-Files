/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.google.common.base.Predicate
 *  com.google.common.collect.Iterables
 *  javax.annotation.Nullable
 */
package com.atlassian.plugin.notifications.api.medium.util;

import com.atlassian.plugin.notifications.api.ErrorCollection;
import com.atlassian.plugin.notifications.api.medium.Group;
import com.atlassian.plugin.notifications.api.medium.Server;
import com.atlassian.plugin.notifications.api.medium.ServerConnectionException;
import com.atlassian.sal.api.message.I18nResolver;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import java.io.Serializable;
import java.util.List;
import javax.annotation.Nullable;

public class AvailableGroupValidator {
    public static ErrorCollection isValid(Server server, I18nResolver i18n, final String groupId) {
        ErrorCollection errors = new ErrorCollection();
        try {
            List<Group> availableGroups = server.getAvailableGroups(null);
            boolean groupExists = Iterables.any(availableGroups, (Predicate)new Predicate<Group>(){

                public boolean apply(@Nullable Group input) {
                    return input != null && input.getId().equals(groupId);
                }
            });
            if (!groupExists) {
                errors.addErrorMessage(i18n.getText("notifications.plugin.server.group.invalid", new Serializable[]{groupId}));
            }
        }
        catch (ServerConnectionException e) {
            errors.addErrorMessage(i18n.getText("notifications.plugin.server.group.error"));
        }
        return errors;
    }
}

