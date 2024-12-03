/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.plugin.ModuleCompleteKey
 *  com.atlassian.plugin.web.descriptors.WebItemModuleDescriptor
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.createcontent.contextproviders;

import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.plugins.createcontent.TemplateRendererHelper;
import com.atlassian.confluence.plugins.createcontent.api.contextproviders.AbstractBlueprintContextProvider;
import com.atlassian.confluence.plugins.createcontent.api.contextproviders.BlueprintContext;
import com.atlassian.confluence.plugins.createcontent.impl.ContentBlueprint;
import com.atlassian.confluence.plugins.createcontent.impl.DefaultBlueprintResolver;
import com.atlassian.confluence.plugins.createcontent.model.BlueprintIdBundle;
import com.atlassian.confluence.plugins.createcontent.services.BlueprintResolver;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.plugin.ModuleCompleteKey;
import com.atlassian.plugin.web.descriptors.WebItemModuleDescriptor;
import org.apache.commons.lang3.StringUtils;

public class DefaultIndexPageContextProvider
extends AbstractBlueprintContextProvider {
    private final I18NBeanFactory i18NBeanFactory;
    private final LocaleManager localeManager;
    private final BlueprintResolver blueprintResolver;

    public DefaultIndexPageContextProvider(I18NBeanFactory i18NBeanFactory, LocaleManager localeManager, DefaultBlueprintResolver blueprintResolver, TemplateRendererHelper templateRendererHelper) {
        super(templateRendererHelper);
        this.i18NBeanFactory = i18NBeanFactory;
        this.localeManager = localeManager;
        this.blueprintResolver = blueprintResolver;
    }

    @Override
    protected BlueprintContext updateBlueprintContext(BlueprintContext context) {
        WebItemModuleDescriptor blueprintItem;
        ModuleCompleteKey blueprintModuleKey = context.getBlueprintModuleCompleteKey();
        String blueprintModuleCompleteKey = blueprintModuleKey.getCompleteKey();
        String spaceKey = context.getSpaceKey();
        BlueprintIdBundle idBundle = new BlueprintIdBundle(null, blueprintModuleKey, spaceKey);
        ContentBlueprint pluginBlueprint = this.blueprintResolver.getContentBlueprint(null, blueprintModuleCompleteKey, spaceKey);
        if (pluginBlueprint == null) {
            throw new IllegalStateException("No blueprint found for id-bundle: " + idBundle);
        }
        String contentBlueprintId = pluginBlueprint.getId().toString();
        String analyticsKey = context.getAnalyticsKey();
        String templateLabel = context.getTemplateLabel();
        String createResult = context.getCreateResult();
        I18NBean i18nBean = this.getI18nBean();
        String i18nPrefix = blueprintModuleCompleteKey.replace(':', '.');
        String blankTitleKey = i18nPrefix + ".blank-title";
        String blankTitle = i18nBean.getText(blankTitleKey);
        String blankDescriptionKey = i18nPrefix + ".blank-description";
        String blankDescription = i18nBean.getText(blankDescriptionKey);
        String createButtonLabelKey = i18nPrefix + ".create-button-label";
        String createButtonLabel = i18nBean.getText(createButtonLabelKey);
        if ((blankTitle.equals(blankTitleKey) || blankDescription.equals(blankDescriptionKey) || createButtonLabel.equals(createButtonLabelKey)) && (blueprintItem = this.blueprintResolver.getWebItemMatchingBlueprint(blueprintModuleCompleteKey)) != null) {
            String blueprintName = i18nBean.getText(blueprintItem.getI18nNameKey());
            blankTitle = blankTitle.equals(blankTitleKey) ? blueprintName : blankTitle;
            blankDescription = blankDescription.equals(blankDescriptionKey) ? i18nBean.getText(blueprintItem.getDescriptionKey()) : blankDescription;
            String string = createButtonLabel = createButtonLabel.equals(createButtonLabelKey) ? i18nBean.getText("create.content.plugin.default.create-button-label", (Object[])new String[]{blueprintName}) : createButtonLabel;
        }
        if (StringUtils.isBlank((CharSequence)createButtonLabel)) {
            createButtonLabel = context.getCreateFromTemplateLabel();
        }
        context.put("createFromTemplateMacro", (Object)this.renderCreateFromTemplateMacro(contentBlueprintId, createButtonLabel, "", blueprintModuleCompleteKey));
        context.put("contentReportTableMacro", (Object)this.renderContentReportTableMacro(templateLabel, analyticsKey, spaceKey, blankTitle, blankDescription, createButtonLabel, contentBlueprintId, blueprintModuleCompleteKey));
        return context;
    }

    public I18NBean getI18nBean() {
        return this.i18NBeanFactory.getI18NBean(this.localeManager.getSiteDefaultLocale());
    }
}

