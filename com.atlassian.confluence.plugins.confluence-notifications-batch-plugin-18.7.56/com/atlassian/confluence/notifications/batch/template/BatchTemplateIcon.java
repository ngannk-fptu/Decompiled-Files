/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleCompleteKey
 */
package com.atlassian.confluence.notifications.batch.template;

import com.atlassian.confluence.notifications.batch.template.BatchTemplateElement;
import com.atlassian.plugin.ModuleCompleteKey;

@Deprecated
public class BatchTemplateIcon
implements BatchTemplateElement {
    public static final String TEMPLATE_NAME = "icon";
    private final ModuleCompleteKey moduleCompleteKey;
    private final String id;

    public BatchTemplateIcon(ModuleCompleteKey moduleCompleteKey, String id) {
        this.moduleCompleteKey = moduleCompleteKey;
        this.id = id;
    }

    public ModuleCompleteKey getModuleCompleteKey() {
        return this.moduleCompleteKey;
    }

    public String getId() {
        return this.id;
    }

    @Override
    public String getTemplateName() {
        return TEMPLATE_NAME;
    }
}

