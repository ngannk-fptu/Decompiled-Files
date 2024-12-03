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
 *  com.google.common.base.Objects
 *  com.google.common.collect.Lists
 *  org.apache.commons.lang3.StringUtils
 *  org.dom4j.Element
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.createcontent.extensions;

import com.atlassian.confluence.plugins.createcontent.extensions.BlueprintDescriptor;
import com.atlassian.confluence.plugins.dialog.wizard.api.DialogWizard;
import com.atlassian.confluence.plugins.dialog.wizard.api.DialogWizardModuleDescriptor;
import com.atlassian.confluence.util.i18n.DocumentationBeanFactory;
import com.atlassian.plugin.ModuleCompleteKey;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlueprintModuleDescriptor
extends AbstractModuleDescriptor<Void>
implements BlueprintDescriptor {
    private static final Logger log = LoggerFactory.getLogger(BlueprintModuleDescriptor.class);
    private final DocumentationBeanFactory documentationBeanFactory;
    private ModuleCompleteKey indexTemplateKey;
    private ModuleCompleteKey blueprintKey;
    private String indexKey;
    private boolean indexDisabled;
    private String createResult;
    private String howToUseTemplate;
    private String indexTitleI18nKey;
    private DialogWizard dialogWizard;
    private List<ModuleCompleteKey> contentTemplateKeys;
    private Element tempWizardElement;

    public BlueprintModuleDescriptor(@ComponentImport ModuleFactory moduleFactory, @ComponentImport DocumentationBeanFactory documentationBeanFactory) {
        super(moduleFactory);
        this.documentationBeanFactory = documentationBeanFactory;
    }

    public void init(Plugin plugin, Element element) throws PluginParseException {
        super.init(plugin, element);
        if (StringUtils.isBlank((CharSequence)this.getKey())) {
            throw new PluginParseException("key is a required attribute of <blueprint>.");
        }
        if (StringUtils.isBlank((CharSequence)this.getI18nNameKey())) {
            log.warn("i18n-name-key is a required attribute of <blueprint> for module: " + this.getCompleteKey());
        }
        this.blueprintKey = new ModuleCompleteKey(this.getCompleteKey());
        this.contentTemplateKeys = Lists.newArrayList();
        String attrContentTemplateKey = element.attributeValue("content-template-key");
        if (StringUtils.isNotBlank((CharSequence)attrContentTemplateKey)) {
            this.contentTemplateKeys.add(new ModuleCompleteKey(plugin.getKey(), attrContentTemplateKey));
        } else {
            List templateElements = element.elements("content-template");
            for (Element keyElement : templateElements) {
                this.contentTemplateKeys.add(new ModuleCompleteKey(plugin.getKey(), keyElement.attributeValue("ref")));
            }
        }
        this.indexDisabled = Boolean.parseBoolean(element.attributeValue("disable-index-page"));
        this.indexTemplateKey = null;
        this.indexTemplateKey = StringUtils.isNotBlank((CharSequence)element.attributeValue("index-template-key")) ? new ModuleCompleteKey(plugin.getKey(), element.attributeValue("index-template-key")) : new ModuleCompleteKey("com.atlassian.confluence.plugins.confluence-create-content-plugin", "default-index-template");
        this.indexKey = element.attributeValue("index-key");
        this.indexTitleI18nKey = element.attributeValue("i18n-index-title-key");
        this.createResult = element.attributeValue("create-result");
        this.howToUseTemplate = element.attributeValue("how-to-use-template");
        this.tempWizardElement = element.element("dialog-wizard");
    }

    @Override
    @Deprecated
    public ModuleCompleteKey getContentTemplateKey() {
        return this.getFirstContentTemplateKey();
    }

    @Override
    public ModuleCompleteKey getFirstContentTemplateKey() {
        return this.contentTemplateKeys.size() > 0 ? this.contentTemplateKeys.get(0) : null;
    }

    @Override
    public ModuleCompleteKey getContentTemplateKey(String contentTemplateModuleKey) {
        return new ModuleCompleteKey(this.getPluginKey(), contentTemplateModuleKey);
    }

    @Override
    public List<ModuleCompleteKey> getContentTemplates() {
        return this.contentTemplateKeys;
    }

    @Override
    public ModuleCompleteKey getIndexTemplate() {
        return this.indexTemplateKey;
    }

    @Override
    public ModuleCompleteKey getBlueprintKey() {
        return this.blueprintKey;
    }

    @Override
    public String getIndexKey() {
        return this.indexKey;
    }

    @Override
    public String getCreateResult() {
        return this.createResult;
    }

    @Override
    public String getIndexTitleI18nKey() {
        return this.indexTitleI18nKey;
    }

    public Void getModule() {
        return null;
    }

    @Override
    public String getHowToUseTemplate() {
        return this.howToUseTemplate;
    }

    @Override
    public DialogWizard getDialogWizard() {
        if (this.dialogWizard == null && this.tempWizardElement != null) {
            DialogWizardModuleDescriptor moduleDescriptor = new DialogWizardModuleDescriptor(this.moduleFactory, this.documentationBeanFactory);
            moduleDescriptor.init(this.plugin, this.tempWizardElement);
            this.dialogWizard = moduleDescriptor.getModule();
            this.tempWizardElement = null;
        }
        return this.dialogWizard;
    }

    @Override
    public boolean isIndexDisabled() {
        return this.indexDisabled;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof BlueprintModuleDescriptor)) {
            return false;
        }
        BlueprintModuleDescriptor that = (BlueprintModuleDescriptor)obj;
        return Objects.equal((Object)this.blueprintKey, (Object)that.blueprintKey);
    }

    public int hashCode() {
        return Objects.hashCode((Object[])new Object[]{this.blueprintKey});
    }
}

