/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.DefaultConversionContext
 *  com.atlassian.confluence.content.render.xhtml.Renderer
 *  com.atlassian.confluence.macro.MacroExecutionException
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.util.ExcerptHelper
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.TokenType
 *  com.atlassian.renderer.links.LinkResolver
 *  com.atlassian.renderer.v2.RenderMode
 *  com.atlassian.renderer.v2.macro.BaseMacro
 *  com.atlassian.renderer.v2.macro.MacroException
 */
package com.atlassian.confluence.plugins.macros.advanced;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.Renderer;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.plugins.macros.advanced.PageProvider;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.util.ExcerptHelper;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.TokenType;
import com.atlassian.renderer.links.LinkResolver;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.BaseMacro;
import com.atlassian.renderer.v2.macro.MacroException;
import java.util.Map;

public class ExcerptIncludeMacro
extends BaseMacro {
    private com.atlassian.confluence.plugins.macros.advanced.xhtml.ExcerptIncludeMacro xhtmlMacro = new com.atlassian.confluence.plugins.macros.advanced.xhtml.ExcerptIncludeMacro();

    public TokenType getTokenType(Map parameters, String body, RenderContext context) {
        return TokenType.INLINE_BLOCK;
    }

    public boolean isInline() {
        return true;
    }

    public boolean hasBody() {
        return false;
    }

    public RenderMode getBodyRenderMode() {
        return RenderMode.ALL;
    }

    public void setLinkResolver(LinkResolver linkResolver) {
        this.xhtmlMacro.setLinkResolver(linkResolver);
    }

    public void setPermissionManager(PermissionManager permissionManager) {
        this.xhtmlMacro.setPermissionManager(permissionManager);
    }

    public String execute(Map parameters, String body, RenderContext renderContext) throws MacroException {
        try {
            return this.xhtmlMacro.execute(parameters, body, (ConversionContext)new DefaultConversionContext(renderContext));
        }
        catch (MacroExecutionException e) {
            throw new MacroException((Throwable)e);
        }
    }

    public boolean suppressMacroRenderingDuringWysiwyg() {
        return true;
    }

    public void setExcerptHelper(ExcerptHelper excerptHelper) {
        this.xhtmlMacro.setExcerptHelper(excerptHelper);
    }

    public void setViewRenderer(Renderer viewRenderer) {
        this.xhtmlMacro.setViewRenderer(viewRenderer);
    }

    public void setUserI18NBeanFactory(I18NBeanFactory i18NBeanFactory) {
        this.xhtmlMacro.setUserI18NBeanFactory(i18NBeanFactory);
    }

    public void setPageProvider(PageProvider pageProvider) {
        this.xhtmlMacro.setPageProvider(pageProvider);
    }
}

