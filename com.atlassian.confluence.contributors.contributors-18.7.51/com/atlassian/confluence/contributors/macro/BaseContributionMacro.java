/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.DefaultConversionContext
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.macro.Macro$BodyType
 *  com.atlassian.confluence.macro.Macro$OutputType
 *  com.atlassian.confluence.macro.MacroExecutionException
 *  com.atlassian.confluence.macro.StreamableMacro
 *  com.atlassian.confluence.macro.StreamableMacroAdapter
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.TokenType
 *  com.atlassian.renderer.v2.RenderMode
 *  com.atlassian.renderer.v2.macro.BaseMacro
 *  com.atlassian.renderer.v2.macro.MacroException
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.contributors.macro;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.macro.StreamableMacro;
import com.atlassian.confluence.macro.StreamableMacroAdapter;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.TokenType;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.BaseMacro;
import com.atlassian.renderer.v2.macro.MacroException;
import com.atlassian.user.User;
import java.util.List;
import java.util.Map;

public abstract class BaseContributionMacro
extends BaseMacro
implements StreamableMacro {
    private final LocaleManager localeManager;
    private final I18NBeanFactory i18NBeanFactory;

    protected BaseContributionMacro(LocaleManager localeManager, I18NBeanFactory i18NBeanFactory) {
        this.localeManager = localeManager;
        this.i18NBeanFactory = i18NBeanFactory;
    }

    public TokenType getTokenType(Map parameters, String body, RenderContext context) {
        return TokenType.BLOCK;
    }

    public boolean hasBody() {
        return false;
    }

    public RenderMode getBodyRenderMode() {
        return RenderMode.NO_RENDER;
    }

    public String execute(Map parameters, String body, RenderContext renderContext) throws MacroException {
        try {
            return this.execute((Map<String, String>)parameters, body, (ConversionContext)new DefaultConversionContext(renderContext));
        }
        catch (MacroExecutionException e) {
            throw new MacroException((Throwable)e);
        }
    }

    public String execute(Map<String, String> parameters, String body, ConversionContext conversionContext) throws MacroExecutionException {
        return StreamableMacroAdapter.executeFromStream((StreamableMacro)this, parameters, (String)body, (ConversionContext)conversionContext);
    }

    public Macro.BodyType getBodyType() {
        return Macro.BodyType.NONE;
    }

    public Macro.OutputType getOutputType() {
        return Macro.OutputType.BLOCK;
    }

    private I18NBean getI18NBean() {
        return this.i18NBeanFactory.getI18NBean(this.localeManager.getLocale((User)AuthenticatedUserThreadLocal.get()));
    }

    protected String getText(String key, List<?> substitutions) {
        return this.getI18NBean().getText(key, substitutions);
    }

    protected String getText(String key) {
        return this.getI18NBean().getText(key);
    }
}

