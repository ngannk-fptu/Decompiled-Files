/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.dialog.wizard.api.DialogWizard
 *  com.atlassian.confluence.plugins.dialog.wizard.api.DialogWizardModuleDescriptor
 *  com.atlassian.confluence.util.i18n.DocumentationBeanFactory
 *  com.atlassian.plugin.ModuleCompleteKey
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.module.ModuleFactory
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.google.common.collect.Lists
 *  javax.annotation.Nonnull
 *  org.apache.commons.lang3.StringUtils
 *  org.dom4j.Element
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.createcontent.extensions;

import com.atlassian.confluence.plugins.dialog.wizard.api.DialogWizard;
import com.atlassian.confluence.plugins.dialog.wizard.api.DialogWizardModuleDescriptor;
import com.atlassian.confluence.util.i18n.DocumentationBeanFactory;
import com.atlassian.plugin.ModuleCompleteKey;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpaceBlueprintModuleDescriptor
extends AbstractModuleDescriptor<Void> {
    private static final Logger log = LoggerFactory.getLogger(SpaceBlueprintModuleDescriptor.class);
    private final DocumentationBeanFactory documentationBeanFactory;
    private ContentTemplateRefNode contentTemplateRefNode;
    private Element tempWizardElement;
    private DialogWizard dialogWizard;
    private List<ModuleCompleteKey> promotedBlueprintKeys = Lists.newArrayList();
    private String category;

    public SpaceBlueprintModuleDescriptor(@ComponentImport ModuleFactory moduleFactory, @ComponentImport DocumentationBeanFactory documentationBeanFactory) {
        super(moduleFactory);
        this.documentationBeanFactory = documentationBeanFactory;
    }

    public void init(@Nonnull Plugin plugin, @Nonnull Element element) throws PluginParseException {
        Element homePageElement;
        super.init(plugin, element);
        if (StringUtils.isBlank((CharSequence)this.getKey())) {
            throw new PluginParseException("key is a required attribute of <space-blueprint>.");
        }
        if (StringUtils.isBlank((CharSequence)this.getI18nNameKey())) {
            log.warn("i18n-name-key is a required attribute of <space-blueprint> for module: " + this.getCompleteKey());
        }
        if ((homePageElement = element.element("content-template")) != null) {
            this.contentTemplateRefNode = this.makeNode(homePageElement);
        }
        this.tempWizardElement = element.element("dialog-wizard");
        this.category = element.attributeValue("category");
        Element promotedBlueprint = element.element("promoted-blueprints");
        if (promotedBlueprint != null) {
            List blueprintElements = promotedBlueprint.elements("blueprint");
            for (Element keyElement : blueprintElements) {
                this.promotedBlueprintKeys.add(new ModuleCompleteKey(keyElement.attributeValue("ref")));
            }
        }
    }

    private ContentTemplateRefNode makeNode(Element element) {
        ContentTemplateRefNode result = new ContentTemplateRefNode(new ModuleCompleteKey(this.plugin.getKey(), element.attributeValue("ref")));
        List children = element.elements("content-template");
        if (children != null) {
            result.children = Lists.newArrayList();
            for (Element child : children) {
                result.children.add(this.makeNode(child));
            }
        }
        return result;
    }

    public Void getModule() {
        return null;
    }

    public ContentTemplateRefNode getContentTemplateRefNode() {
        return this.contentTemplateRefNode;
    }

    public DialogWizard getDialogWizard() {
        if (this.dialogWizard == null && this.tempWizardElement != null) {
            DialogWizardModuleDescriptor moduleDescriptor = new DialogWizardModuleDescriptor(this.moduleFactory, this.documentationBeanFactory);
            moduleDescriptor.init(this.plugin, this.tempWizardElement);
            this.dialogWizard = moduleDescriptor.getModule();
            this.tempWizardElement = null;
        }
        return this.dialogWizard;
    }

    public List<ModuleCompleteKey> getPromotedBlueprintKeys() {
        return this.promotedBlueprintKeys;
    }

    public String getCategory() {
        return this.category;
    }

    public static class ContentTemplateRefNode {
        public ModuleCompleteKey ref;
        public List<ContentTemplateRefNode> children;

        public ContentTemplateRefNode(ModuleCompleteKey ref) {
            this.ref = ref;
        }
    }
}

