/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.notifications.spi;

import com.atlassian.plugin.notifications.api.template.TemplateDefinition;
import java.io.Writer;
import java.util.Map;
import java.util.Optional;

public interface NotificationRenderer {
    public void render(TemplateDefinition var1, Map<String, Object> var2, Writer var3);

    default public void render(TemplateDefinition template, Map<String, Object> context, Optional<String> outputMimeType, Writer out) {
        this.render(template, context, out);
    }
}

