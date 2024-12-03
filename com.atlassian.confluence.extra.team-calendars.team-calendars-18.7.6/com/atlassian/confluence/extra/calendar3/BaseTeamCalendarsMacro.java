/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.macro.Macro
 *  com.atlassian.confluence.macro.Macro$BodyType
 *  com.atlassian.confluence.macro.Macro$OutputType
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.TokenType
 *  com.atlassian.renderer.v2.RenderMode
 *  com.atlassian.renderer.v2.macro.BaseMacro
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.extra.calendar3;

import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.TokenType;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.BaseMacro;
import com.atlassian.user.User;
import java.util.List;
import java.util.Map;

public abstract class BaseTeamCalendarsMacro
extends BaseMacro
implements Macro {
    private final I18NBeanFactory i18NBeanFactory;
    private final LocaleManager localeManager;

    public BaseTeamCalendarsMacro(I18NBeanFactory i18NBeanFactory, LocaleManager localeManager) {
        this.i18NBeanFactory = i18NBeanFactory;
        this.localeManager = localeManager;
    }

    public boolean hasBody() {
        return false;
    }

    public RenderMode getBodyRenderMode() {
        return RenderMode.NO_RENDER;
    }

    public TokenType getTokenType(Map params, String body, RenderContext renderContext) {
        return TokenType.BLOCK;
    }

    public Macro.BodyType getBodyType() {
        return Macro.BodyType.NONE;
    }

    public Macro.OutputType getOutputType() {
        return Macro.OutputType.BLOCK;
    }

    protected String getText(String i18nKey) {
        return this.getI18NBean().getText(i18nKey);
    }

    protected String getText(String i18nKey, List substitutions) {
        return this.getI18NBean().getText(i18nKey, substitutions);
    }

    private I18NBean getI18NBean() {
        return this.i18NBeanFactory.getI18NBean(this.localeManager.getLocale((User)AuthenticatedUserThreadLocal.get()));
    }
}

