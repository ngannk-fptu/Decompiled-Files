/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.i18n.DocumentationBeanFactory
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.module.ModuleFactory
 *  javax.annotation.Nonnull
 *  org.apache.commons.lang3.StringUtils
 *  org.dom4j.Element
 */
package com.atlassian.confluence.plugins.dialog.wizard.api;

import com.atlassian.confluence.plugins.dialog.wizard.api.DialogWizard;
import com.atlassian.confluence.plugins.dialog.wizard.api.DialogWizardPage;
import com.atlassian.confluence.util.i18n.DocumentationBeanFactory;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;

public class DialogWizardModuleDescriptor
extends AbstractModuleDescriptor<DialogWizard> {
    private final DocumentationBeanFactory documentationBeanFactory;
    private DialogWizard dialogWizard;

    public DialogWizardModuleDescriptor(ModuleFactory moduleFactory, DocumentationBeanFactory documentationBeanFactory) {
        super(moduleFactory);
        this.documentationBeanFactory = documentationBeanFactory;
    }

    public void init(@Nonnull Plugin plugin, @Nonnull Element element) throws PluginParseException {
        DialogWizardPage lastPage;
        super.init(plugin, element);
        ArrayList<DialogWizardPage> wizardPages = new ArrayList<DialogWizardPage>();
        List elements = element.elements("dialog-page");
        for (Element child : elements) {
            String descriptionHeaderLinkKey = child.attributeValue("description-header-link-key");
            String descriptionHeaderLink = child.attributeValue("description-header-link");
            if (StringUtils.isNotBlank((CharSequence)descriptionHeaderLinkKey)) {
                descriptionHeaderLink = this.documentationBeanFactory.getDocumentationBean().getLink(descriptionHeaderLinkKey);
            }
            DialogWizardPage page = new DialogWizardPage(child.attributeValue("id"), child.attributeValue("template-key"), child.attributeValue("title-key"), descriptionHeaderLink, child.attributeValue("description-header-key"), child.attributeValue("description-content-key"), child.attributeValue("last"));
            wizardPages.add(page);
        }
        if (!wizardPages.isEmpty() && (lastPage = (DialogWizardPage)wizardPages.get(wizardPages.size() - 1)).getLast() == null) {
            lastPage.setLast(true);
        }
        this.dialogWizard = new DialogWizard(this.getKey(), this.getI18nNameKey(), wizardPages);
    }

    public DialogWizard getModule() {
        return this.dialogWizard;
    }
}

