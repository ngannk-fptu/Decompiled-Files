/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.DefaultConversionContext
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.macro.EditorImagePlaceholder
 *  com.atlassian.confluence.macro.ImagePlaceholder
 *  com.atlassian.confluence.macro.Macro
 *  com.atlassian.confluence.macro.Macro$BodyType
 *  com.atlassian.confluence.macro.Macro$OutputType
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.TokenType
 *  com.atlassian.renderer.v2.RenderMode
 *  com.atlassian.renderer.v2.RenderUtils
 *  com.atlassian.renderer.v2.macro.BaseMacro
 *  com.atlassian.user.User
 *  org.apache.commons.lang3.StringUtils
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.confluence.extra.widgetconnector;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.extra.widgetconnector.RenderManager;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.macro.EditorImagePlaceholder;
import com.atlassian.confluence.macro.ImagePlaceholder;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.TokenType;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.RenderUtils;
import com.atlassian.renderer.v2.macro.BaseMacro;
import com.atlassian.user.User;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

public class WidgetMacro
extends BaseMacro
implements Macro,
EditorImagePlaceholder {
    public static final String URL = "url";
    private static final Set<String> sanitizeFields = Collections.singleton("_template");
    private final RenderManager renderManager;
    private final LocaleManager localeManager;
    private final I18NBeanFactory i18NBeanFactory;

    @Autowired
    public WidgetMacro(RenderManager renderManager, LocaleManager localeManager, I18NBeanFactory i18NBeanFactory) {
        this.renderManager = renderManager;
        this.localeManager = localeManager;
        this.i18NBeanFactory = i18NBeanFactory;
    }

    public TokenType getTokenType(Map parameters, String body, RenderContext context) {
        return TokenType.BLOCK;
    }

    public boolean hasBody() {
        return true;
    }

    public RenderMode getBodyRenderMode() {
        return RenderMode.NO_RENDER;
    }

    public String execute(Map parameters, String body, RenderContext renderContext) {
        return this.execute((Map<String, String>)parameters, body, (ConversionContext)new DefaultConversionContext(renderContext));
    }

    public String execute(Map<String, String> parameters, String body, ConversionContext conversionContext) {
        String url = RenderUtils.getParameter(parameters, (String)URL, (int)0);
        if (StringUtils.isEmpty((CharSequence)url)) {
            url = StringUtils.strip((String)body);
        }
        if (StringUtils.isEmpty((CharSequence)url)) {
            return RenderUtils.blockError((String)this.getText("macro.error.urlnotspecified"), (String)"");
        }
        this.doSanitizeParameters(parameters);
        return this.renderManager.getEmbeddedHtml(url, parameters);
    }

    private void doSanitizeParameters(Map<String, String> parameters) {
        Objects.requireNonNull(parameters);
        for (String sanitizedParameter : sanitizeFields) {
            parameters.remove(sanitizedParameter);
        }
    }

    public Macro.BodyType getBodyType() {
        return Macro.BodyType.NONE;
    }

    public Macro.OutputType getOutputType() {
        return Macro.OutputType.BLOCK;
    }

    public String getText(String i18nKey) {
        return this.getI18NBean().getText(i18nKey);
    }

    private I18NBean getI18NBean() {
        return this.i18NBeanFactory.getI18NBean(this.localeManager.getLocale((User)AuthenticatedUserThreadLocal.get()));
    }

    public ImagePlaceholder getImagePlaceholder(Map<String, String> parameters, ConversionContext conversionContext) {
        String url = RenderUtils.getParameter(parameters, (String)URL, (int)0);
        return this.renderManager.getImagePlaceholder(url, parameters);
    }
}

