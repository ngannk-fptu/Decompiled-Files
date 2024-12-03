/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.notifications.spi;

import com.atlassian.plugin.notifications.spi.NotificationRenderer;
import com.atlassian.plugin.notifications.spi.OptionalService;

public class NotificationRendererServiceFactory
extends OptionalService<NotificationRenderer> {
    public NotificationRendererServiceFactory() {
        super(NotificationRenderer.class);
    }
}

