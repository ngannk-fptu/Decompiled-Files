/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.notifications.spi;

import com.atlassian.plugin.notifications.api.template.TemplateDefinition;
import com.atlassian.plugin.notifications.spi.TemplateParams;

public interface TemplateLocator {
    public TemplateDefinition getTemplate(TemplateParams var1);
}

