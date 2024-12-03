/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.plugin.ModuleCompleteKey
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.web.Condition
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.ImmutableSet$Builder
 */
package com.atlassian.confluence.notifications;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.plugin.ModuleCompleteKey;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.Condition;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@ExperimentalApi
public class NotificationKeyCondition
implements Condition {
    private static final String NOTIFICATION_KEY = "notificationKey";
    private Set<ModuleCompleteKey> notificationKeys = Collections.EMPTY_SET;

    public static Map<String, Object> copyWithNotificationKey(Map<String, Object> context, ModuleCompleteKey notificationKey) {
        HashMap<String, Object> newContext = new HashMap<String, Object>();
        newContext.putAll(context);
        newContext.put(NOTIFICATION_KEY, notificationKey);
        return newContext;
    }

    public void init(Map<String, String> params) throws PluginParseException {
        Preconditions.checkArgument((!params.isEmpty() ? 1 : 0) != 0, (String)"Condition [%s] needs to be initialised with a 1..n notification keys as parameter keys.", (Object)NotificationKeyCondition.class.getName());
        ImmutableSet.Builder notificationKeysBuilder = ImmutableSet.builder();
        for (String notificationKey : params.keySet()) {
            try {
                notificationKeysBuilder.add((Object)new ModuleCompleteKey(notificationKey));
            }
            catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(String.format("Given parameter key [%s] for condition [%s] is expected to be a moduleCompleteKey denoting a notification to be whitelisted.", notificationKey, NotificationKeyCondition.class.getName()));
            }
        }
        this.notificationKeys = notificationKeysBuilder.build();
    }

    public boolean shouldDisplay(Map<String, Object> context) {
        ModuleCompleteKey notificationKey = (ModuleCompleteKey)context.get(NOTIFICATION_KEY);
        if (notificationKey == null) {
            return false;
        }
        return this.notificationKeys.contains(notificationKey);
    }
}

