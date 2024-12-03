/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.plugin.notifications.dispatcher.util;

import com.atlassian.plugin.notifications.dispatcher.NotificationHandlerModuleDescriptor;
import com.google.common.base.Predicate;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;

public class MatchingHandlerPredicate
implements Predicate<NotificationHandlerModuleDescriptor> {
    private final Object event;

    public MatchingHandlerPredicate(Object event) {
        this.event = event;
    }

    public boolean apply(@Nullable NotificationHandlerModuleDescriptor input) {
        String canonicalName = this.event.getClass().getCanonicalName();
        return input != null && StringUtils.equals((CharSequence)input.getNotificationClass(), (CharSequence)canonicalName);
    }
}

