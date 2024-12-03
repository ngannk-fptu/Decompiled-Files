/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.user.User
 *  org.apache.commons.lang3.StringUtils
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.widgetconnector.video;

import com.atlassian.confluence.extra.widgetconnector.AbstractWidgetRenderer;
import com.atlassian.confluence.extra.widgetconnector.WidgetRenderer;
import com.atlassian.confluence.extra.widgetconnector.services.VelocityRenderService;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.User;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ExportAsService(value={WidgetRenderer.class})
public class MySpaceVideoRenderer
extends AbstractWidgetRenderer {
    private static final Pattern OLD_MATCH_PATTERN = Pattern.compile("https?://vids.myspace\\.com.*$", 2);
    private static final Pattern PATTERN = Pattern.compile("https?://(www.)?myspace.com(/.+)?/video/(.+?)(/.+)?/(\\d+)", 2);
    private static final String VELOCITY_TEMPLATE = "com/atlassian/confluence/extra/widgetconnector/templates/iframe.vm";
    private static final String SERVICE_NAME = "MySpaceVideo";
    private final VelocityRenderService velocityRenderService;
    private final I18NBeanFactory i18NBeanFactory;
    private final LocaleManager localeManager;

    @Autowired
    public MySpaceVideoRenderer(VelocityRenderService velocityRenderService, @ComponentImport I18NBeanFactory i18NBeanFactory, @ComponentImport LocaleManager localeManager) {
        this.velocityRenderService = velocityRenderService;
        this.i18NBeanFactory = i18NBeanFactory;
        this.localeManager = localeManager;
    }

    public String getEmbedUrl(String url) {
        Matcher urlMatcher = PATTERN.matcher(url);
        Object videoName = "";
        String videoId = "";
        if (urlMatcher.matches()) {
            videoName = urlMatcher.group(4);
            if (videoName == null || ((String)videoName).length() == 0) {
                videoName = "/" + urlMatcher.group(3);
            }
            videoId = urlMatcher.group(5);
            if (!((String)videoName).endsWith("-")) {
                videoName = (String)videoName + "-";
            }
        }
        return "//myspace.com/play/video" + (String)videoName + videoId;
    }

    @Override
    public String getEmbeddedHtml(String url, Map<String, String> params) {
        if (OLD_MATCH_PATTERN.matcher(url).matches()) {
            params.put("baseUrlHtml", this.getText("com.atlassian.confluence.extra.widgetconnector.myspace.outdated.url"));
            params.put("_template", "com/atlassian/confluence/extra/widgetconnector/templates/error.vm");
            return this.velocityRenderService.render(url, params);
        }
        String protocol = StringUtils.startsWith((CharSequence)url, (CharSequence)"https://") ? "https" : "http";
        Object target = StringUtils.startsWith((CharSequence)url, (CharSequence)"/") ? protocol + "://myspace.com" + url : url;
        params.put("_template", VELOCITY_TEMPLATE);
        return this.velocityRenderService.render(this.getEmbedUrl((String)target), params);
    }

    @Override
    public String getServiceName() {
        return SERVICE_NAME;
    }

    public String getText(String i18nKey) {
        return this.getI18NBean().getText(i18nKey);
    }

    private I18NBean getI18NBean() {
        return this.i18NBeanFactory.getI18NBean(this.localeManager.getLocale((User)AuthenticatedUserThreadLocal.get()));
    }
}

