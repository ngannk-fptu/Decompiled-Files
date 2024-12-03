/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.velocity.htmlsafe.HtmlSafe
 *  com.atlassian.fugue.Pair
 *  com.atlassian.plugin.webresource.UrlMode
 *  com.atlassian.plugin.webresource.WebResourceUrlProvider
 *  com.google.common.base.Supplier
 *  com.google.common.base.Throwables
 */
package com.atlassian.confluence.content.service;

import com.atlassian.confluence.content.render.xhtml.FormatConverter;
import com.atlassian.confluence.content.service.WelcomeMessageService;
import com.atlassian.confluence.pages.templates.variables.StringVariable;
import com.atlassian.confluence.plugin.webresource.WebResourceDependenciesRecorder;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.spaces.SystemTemplateManager;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.confluence.velocity.htmlsafe.HtmlSafe;
import com.atlassian.fugue.Pair;
import com.atlassian.plugin.webresource.UrlMode;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;
import com.google.common.base.Supplier;
import com.google.common.base.Throwables;
import java.util.Collections;

public class DefaultWelcomeMessageService
implements WelcomeMessageService {
    private static final String PLUGIN_MODULE_KEY = "com.atlassian.confluence.plugins.system-templates:system-template-resources";
    private static final String WELCOME_IMAGE_FILE = "assets/images/welcome.png";
    private static final String WELCOME_IMAGE_I18N_KEY = "welcome.image.alt";
    private final SystemTemplateManager systemTemplateManager;
    private final FormatConverter formatConverter;
    private final WebResourceUrlProvider webResourceUrlProvider;
    private final Supplier<WebResourceDependenciesRecorder> webResourceDependenciesRecorder;
    private final I18NBeanFactory i18NBeanFactory;

    public DefaultWelcomeMessageService(SystemTemplateManager systemTemplateManager, FormatConverter formatConverter, WebResourceUrlProvider webResourceUrlProvider, Supplier<WebResourceDependenciesRecorder> webResourceDependenciesRecorder, I18NBeanFactory i18NBeanFactory) {
        this.systemTemplateManager = systemTemplateManager;
        this.formatConverter = formatConverter;
        this.webResourceUrlProvider = webResourceUrlProvider;
        this.webResourceDependenciesRecorder = webResourceDependenciesRecorder;
        this.i18NBeanFactory = i18NBeanFactory;
    }

    @Override
    @HtmlSafe
    public String getWelcomeMessage() {
        String resource = this.webResourceUrlProvider.getBaseUrl(UrlMode.ABSOLUTE) + this.webResourceUrlProvider.getResourceUrl(PLUGIN_MODULE_KEY, WELCOME_IMAGE_FILE);
        I18NBean i18nBean = this.i18NBeanFactory.getI18NBean();
        String welcomeImageAlt = i18nBean.getText(WELCOME_IMAGE_I18N_KEY);
        String imageMarkup = "<ac:image ac:align=\"center\" ac:width=\"100\" ac:alt=\"" + welcomeImageAlt + "\"><ri:url ri:value=\"" + resource + "\" /></ac:image>";
        String content = this.systemTemplateManager.getTemplate("com.atlassian.confluence.plugins.system-templates:welcome-message", Collections.singletonList(new StringVariable("welcome.message.image", imageMarkup)));
        return this.formatConverter.convertToViewFormat(content, new PageContext());
    }

    @Override
    @Deprecated
    @HtmlSafe
    public Pair<String, WebResourceDependenciesRecorder.RecordedResources> getWelcomeMessageResource() {
        try {
            return ((WebResourceDependenciesRecorder)this.webResourceDependenciesRecorder.get()).record(this::getWelcomeMessage);
        }
        catch (Exception e) {
            throw Throwables.propagate((Throwable)e);
        }
    }

    @Override
    public void saveWelcomeMessage(String content) {
        this.systemTemplateManager.saveTemplate("Default Welcome Message", "com.atlassian.confluence.plugins.system-templates:welcome-message", content);
    }
}

