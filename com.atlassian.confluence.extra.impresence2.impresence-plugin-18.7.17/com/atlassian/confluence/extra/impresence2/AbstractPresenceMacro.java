/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.DefaultConversionContext
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.macro.Macro
 *  com.atlassian.confluence.macro.Macro$BodyType
 *  com.atlassian.confluence.macro.Macro$OutputType
 *  com.atlassian.confluence.macro.MacroExecutionException
 *  com.atlassian.confluence.plugin.services.VelocityHelperService
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.util.GeneralUtil
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.TokenType
 *  com.atlassian.renderer.v2.RenderMode
 *  com.atlassian.renderer.v2.macro.MacroException
 *  com.atlassian.user.User
 *  org.apache.commons.lang.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.impresence2;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.extra.impresence2.LocaleAwareMacro;
import com.atlassian.confluence.extra.impresence2.PresenceManager;
import com.atlassian.confluence.extra.impresence2.reporter.PresenceException;
import com.atlassian.confluence.extra.impresence2.reporter.PresenceReporter;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.plugin.services.VelocityHelperService;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.TokenType;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.MacroException;
import com.atlassian.user.User;
import java.io.IOException;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractPresenceMacro
extends LocaleAwareMacro
implements Macro {
    private static final Logger logger = LoggerFactory.getLogger(AbstractPresenceMacro.class);
    private final PresenceManager presenceManager;
    private final PermissionManager permissionManager;
    private final VelocityHelperService velocityHelperService;

    protected AbstractPresenceMacro(LocaleManager localeManager, I18NBeanFactory i18NBeanFactory, PresenceManager presenceManager, PermissionManager permissionManager, VelocityHelperService velocityHelperService) {
        super(localeManager, i18NBeanFactory);
        this.presenceManager = presenceManager;
        this.permissionManager = permissionManager;
        this.velocityHelperService = velocityHelperService;
    }

    public String execute(Map<String, String> parameters, String body, ConversionContext conversionContext) throws MacroExecutionException {
        String service = this.getImService(parameters);
        if (service == null) {
            throw new MacroExecutionException(this.getText("error.macro.noserviceidprovided"));
        }
        PresenceReporter reporter = this.getReporter(service);
        if (reporter == null) {
            throw new MacroExecutionException(this.getText("error.macro.unsupportedservice", new Object[]{service}));
        }
        if (reporter.requiresConfig()) {
            return this.getRenderedHtml(reporter);
        }
        try {
            return reporter.getPresenceXHTML(GeneralUtil.htmlEncode((String)this.getImId(parameters)), this.shouldShowId(parameters));
        }
        catch (IOException e) {
            logger.error("IO error while getting presence.", (Throwable)e);
            throw new MacroExecutionException((Throwable)e);
        }
        catch (PresenceException e) {
            logger.error("Error getting presence", (Throwable)e);
            throw new MacroExecutionException((Throwable)e);
        }
    }

    public String execute(Map parameters, String bodyText, RenderContext renderContext) throws MacroException {
        try {
            return this.execute((Map<String, String>)parameters, bodyText, (ConversionContext)new DefaultConversionContext(renderContext));
        }
        catch (MacroExecutionException macroError) {
            throw new MacroException((Throwable)macroError);
        }
    }

    protected String getRenderedHtml(PresenceReporter reporter) {
        Map velocityContext = this.velocityHelperService.createDefaultVelocityContext();
        velocityContext.put("reporter", reporter);
        velocityContext.put("isAdmin", this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.ADMINISTER, PermissionManager.TARGET_APPLICATION));
        return this.velocityHelperService.getRenderedTemplate("templates/extra/impresence2/reporter-not-configured.vm", velocityContext);
    }

    protected PresenceReporter getReporter(String service) {
        return this.presenceManager.getReporter(service);
    }

    public TokenType getTokenType(Map parameters, String body, RenderContext context) {
        return TokenType.INLINE;
    }

    public boolean hasBody() {
        return false;
    }

    public RenderMode getBodyRenderMode() {
        return RenderMode.NO_RENDER;
    }

    public Macro.OutputType getOutputType() {
        return Macro.OutputType.INLINE;
    }

    public Macro.BodyType getBodyType() {
        return Macro.BodyType.NONE;
    }

    protected String getImId(Map<String, String> parameters) {
        return parameters.get("0");
    }

    protected abstract String getImService(Map<String, String> var1);

    protected boolean shouldShowId(Map<String, String> parameters) {
        return !StringUtils.equalsIgnoreCase((String)parameters.get("showid"), (String)Boolean.FALSE.toString());
    }
}

