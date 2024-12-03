/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugin.webresource.ConfluenceWebResourceManager
 *  com.atlassian.confluence.plugins.soy.VelocityFriendlySoyTemplateRenderer
 *  com.atlassian.confluence.renderer.template.TemplateRenderer
 *  com.atlassian.confluence.setup.settings.DarkFeaturesManager
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.confluence.util.GeneralUtil
 *  com.atlassian.confluence.util.HtmlUtil
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.plugin.web.WebInterfaceManager
 */
package com.atlassian.confluence.plugins.mobile.velocity;

import com.atlassian.confluence.plugin.webresource.ConfluenceWebResourceManager;
import com.atlassian.confluence.plugins.mobile.velocity.VelocityContextFactory;
import com.atlassian.confluence.plugins.mobile.webresource.WebResourceSupplier;
import com.atlassian.confluence.plugins.soy.VelocityFriendlySoyTemplateRenderer;
import com.atlassian.confluence.renderer.template.TemplateRenderer;
import com.atlassian.confluence.setup.settings.DarkFeaturesManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.plugin.web.WebInterfaceManager;
import java.util.HashMap;
import java.util.Map;

public class MobileVelocityContextFactory
implements VelocityContextFactory {
    private static VelocityContextFactory instance;
    private static final GeneralUtil GENERAL_UTIL_INSTANCE;
    private static final HtmlUtil HTML_UTIL_INSTANCE;
    private final ConfluenceWebResourceManager webResourceManager;
    private final WebResourceSupplier mobileWebResourceSupplier;
    private final VelocityFriendlySoyTemplateRenderer templateRenderer;
    private final I18NBeanFactory i18nBeanFactory;
    private final WebInterfaceManager webInterfaceManager;
    private final SettingsManager settingsManager;
    private final DarkFeaturesManager darkFeaturesManager;

    public static VelocityContextFactory getInstance() {
        return instance;
    }

    public MobileVelocityContextFactory(ConfluenceWebResourceManager webResourceManager, WebResourceSupplier mobileWebResourceSupplier, TemplateRenderer templateRenderer, I18NBeanFactory i18NBeanFactory, WebInterfaceManager webInterfaceManager, SettingsManager settingsManager, DarkFeaturesManager darkFeaturesManager) {
        this.webResourceManager = webResourceManager;
        this.mobileWebResourceSupplier = mobileWebResourceSupplier;
        this.darkFeaturesManager = darkFeaturesManager;
        instance = this;
        this.templateRenderer = new VelocityFriendlySoyTemplateRenderer(templateRenderer);
        this.i18nBeanFactory = i18NBeanFactory;
        this.webInterfaceManager = webInterfaceManager;
        this.settingsManager = settingsManager;
    }

    @Override
    public Map<String, Object> createContext() {
        HashMap<String, Object> context = new HashMap<String, Object>();
        context.put("webResourceManager", this.webResourceManager);
        context.put("webResourceSupplier", this.mobileWebResourceSupplier);
        context.put("generalUtil", GENERAL_UTIL_INSTANCE);
        context.put("htmlUtil", HTML_UTIL_INSTANCE);
        context.put("templateRenderer", this.templateRenderer);
        context.put("i18n", this.i18nBeanFactory.getI18NBean());
        context.put("webInterfaceManager", this.webInterfaceManager);
        context.put("settingsManager", this.settingsManager);
        context.put("darkFeatures", this.darkFeaturesManager.getDarkFeatures());
        return context;
    }

    static {
        GENERAL_UTIL_INSTANCE = new GeneralUtil();
        HTML_UTIL_INSTANCE = new HtmlUtil();
    }
}

