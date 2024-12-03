/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.notifications.api.template;

import com.atlassian.plugin.notifications.api.template.TemplateType;

public class TemplateDefinition {
    private TemplateType type;
    private String template;
    private String templatePackage;

    private TemplateDefinition(TemplateType type, String template, String templatePackage) {
        this.type = type;
        this.template = template;
        this.templatePackage = templatePackage;
    }

    public TemplateType getType() {
        return this.type;
    }

    public String getTemplate() {
        return this.template;
    }

    public String getTemplatePackage() {
        return this.templatePackage;
    }

    public static TemplateDefinition soyTemplate(String templatePackage, String template) {
        return new TemplateDefinition(TemplateType.SOY, template, templatePackage);
    }

    public static TemplateDefinition vmTemplate(String template) {
        return new TemplateDefinition(TemplateType.VM, template, null);
    }
}

