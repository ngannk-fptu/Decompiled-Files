/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ReadOnlyApplicationLink
 *  com.atlassian.applinks.api.ReadOnlyApplicationLinkService
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.macro.Macro
 *  com.atlassian.confluence.macro.Macro$BodyType
 *  com.atlassian.confluence.macro.Macro$OutputType
 *  com.atlassian.confluence.macro.MacroExecutionException
 *  com.atlassian.confluence.plugin.services.VelocityHelperService
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.plugins.whitelist.OutboundWhitelist
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.TokenType
 *  com.atlassian.renderer.v2.RenderMode
 *  com.atlassian.renderer.v2.RenderUtils
 *  com.atlassian.renderer.v2.macro.BaseMacro
 *  com.atlassian.renderer.v2.macro.MacroException
 *  com.atlassian.sal.api.net.NonMarshallingRequestFactory
 *  com.atlassian.sal.api.net.Request
 *  com.atlassian.sal.api.net.Request$MethodType
 *  com.atlassian.sal.api.net.Response
 *  com.atlassian.sal.api.net.ResponseException
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.user.User
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.util.Assert
 */
package com.atlassian.confluence.plugins.macros.html;

import com.atlassian.applinks.api.ReadOnlyApplicationLink;
import com.atlassian.applinks.api.ReadOnlyApplicationLinkService;
import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.plugin.services.VelocityHelperService;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.plugins.whitelist.OutboundWhitelist;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.TokenType;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.RenderUtils;
import com.atlassian.renderer.v2.macro.BaseMacro;
import com.atlassian.renderer.v2.macro.MacroException;
import com.atlassian.sal.api.net.NonMarshallingRequestFactory;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.Response;
import com.atlassian.sal.api.net.ResponseException;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.user.User;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

abstract class WhitelistedHttpRetrievalMacro
extends BaseMacro
implements Macro {
    private static final Logger log = LoggerFactory.getLogger(WhitelistedHttpRetrievalMacro.class);
    private static final String WHITELIST_ERROR_TEMPLATE = "com/atlassian/confluence/plugins/macros/html/whitelist-error.vm";
    private final LocaleManager localeManager;
    private final I18NBeanFactory i18NBeanFactory;
    private final NonMarshallingRequestFactory<Request<?, Response>> requestFactory;
    private final ReadOnlyApplicationLinkService applicationLinkService;
    private final OutboundWhitelist whitelist;
    private final UserManager userManager;
    private final VelocityHelperService velocityHelperService;

    protected WhitelistedHttpRetrievalMacro(LocaleManager localeManager, I18NBeanFactory i18NBeanFactory, NonMarshallingRequestFactory<Request<?, Response>> requestFactory, ReadOnlyApplicationLinkService applicationLinkService, OutboundWhitelist whitelist, UserManager userManager, VelocityHelperService velocityHelperService) {
        this.localeManager = localeManager;
        this.i18NBeanFactory = i18NBeanFactory;
        this.requestFactory = requestFactory;
        this.applicationLinkService = applicationLinkService;
        this.whitelist = whitelist;
        this.userManager = userManager;
        this.velocityHelperService = velocityHelperService;
    }

    protected String getText(String i18nKey, List<String> substitution) {
        return this.getI18nBean().getText(i18nKey, substitution);
    }

    private I18NBean getI18nBean() {
        return this.i18NBeanFactory.getI18NBean(this.localeManager.getLocale((User)AuthenticatedUserThreadLocal.get()));
    }

    protected String getText(String i18nKey) {
        return this.getI18nBean().getText(i18nKey);
    }

    public TokenType getTokenType(Map parameters, String body, RenderContext context) {
        return TokenType.BLOCK;
    }

    public final boolean hasBody() {
        return false;
    }

    public final RenderMode getBodyRenderMode() {
        return RenderMode.NO_RENDER;
    }

    public Macro.BodyType getBodyType() {
        return Macro.BodyType.NONE;
    }

    public Macro.OutputType getOutputType() {
        return Macro.OutputType.BLOCK;
    }

    private static String cleanupUrl(String url) {
        if (url.indexOf(40) > 0) {
            url = url.replaceAll("\\(", "%28");
        }
        if (url.indexOf(41) > 0) {
            url = url.replaceAll("\\)", "%29");
        }
        if (url.indexOf("&amp;") > 0) {
            url = url.replaceAll("&amp;", "&");
        }
        return url;
    }

    private String renderDeniedByWhiteListConfiguration(String url) throws MacroExecutionException {
        Map contextMap = this.velocityHelperService.createDefaultVelocityContext();
        contextMap.put("invalidURL", "true");
        contextMap.put("url", url);
        contextMap.put("remoteUser", AuthenticatedUserThreadLocal.get());
        try {
            return this.velocityHelperService.getRenderedTemplate(WHITELIST_ERROR_TEMPLATE, contextMap);
        }
        catch (Exception e) {
            log.error("Error while trying to display whitelist error!", (Throwable)e);
            throw new MacroExecutionException(e.getMessage());
        }
    }

    private String notFound(String url) {
        return RenderUtils.blockError((String)this.getText("whitelistedmacro.error.notfound", Collections.singletonList(url)), (String)"");
    }

    private String notPermitted(String url) {
        return RenderUtils.blockError((String)this.getText("whitelistedmacro.error.notpermitted", Collections.singletonList(url)), (String)"");
    }

    private String failed(String url, String statusMessage) {
        return RenderUtils.blockError((String)this.getText("whitelistedmacro.error.notpermitted", Collections.singletonList(url)), (String)statusMessage);
    }

    protected abstract String successfulResponse(Map<String, String> var1, ConversionContext var2, String var3, Response var4) throws MacroExecutionException;

    public String execute(Map<String, String> typeSafeMacroParams, String body, ConversionContext conversionContext) throws MacroExecutionException {
        String url = WhitelistedHttpRetrievalMacro.cleanupUrl(StringUtils.defaultString((String)typeSafeMacroParams.get("0"), (String)StringUtils.defaultString((String)typeSafeMacroParams.get("url"))));
        if (StringUtils.isBlank((CharSequence)url)) {
            return RenderUtils.error((String)this.getText("whitelistedmacro.error.nourl"));
        }
        URI uri = WhitelistedHttpRetrievalMacro.toURI(url);
        UserKey userKey = this.userManager.getRemoteUserKey();
        if (uri == null || !this.whitelist.isAllowed(uri, userKey)) {
            return this.renderDeniedByWhiteListConfiguration(url);
        }
        Optional<ReadOnlyApplicationLink> applicationLink = this.findApplicationLinkByUrl(url);
        try {
            Request request = applicationLink.isPresent() ? applicationLink.get().createAuthenticatedRequestFactory().createRequest(Request.MethodType.GET, url) : this.requestFactory.createRequest(Request.MethodType.GET, url);
            return this.executeRequest(typeSafeMacroParams, conversionContext, url, request);
        }
        catch (Exception e) {
            throw new MacroExecutionException((Throwable)e);
        }
    }

    private String executeRequest(Map<String, String> typeSafeMacroParams, ConversionContext conversionContext, String url, Request<?, Response> request) throws ResponseException {
        Assert.notNull(request, (String)"request must not be null");
        AtomicReference result = new AtomicReference();
        request.execute(response -> {
            if (response.getStatusCode() == 404) {
                result.set(this.notFound(url));
            } else if (response.getStatusCode() == 401 || response.getStatusCode() == 403) {
                result.set(this.notPermitted(url));
            } else if (response.getStatusCode() < 200 || response.getStatusCode() > 299) {
                result.set(this.failed(url, response.getStatusText()));
            } else {
                try {
                    result.set(this.successfulResponse(typeSafeMacroParams, conversionContext, url, response));
                }
                catch (MacroExecutionException e) {
                    throw new ResponseException((Throwable)e);
                }
            }
        });
        return (String)result.get();
    }

    private Optional<ReadOnlyApplicationLink> findApplicationLinkByUrl(String url) {
        String lowerUrl = url.toLowerCase();
        Stream<ReadOnlyApplicationLink> targetStream = StreamSupport.stream(this.applicationLinkService.getApplicationLinks().spliterator(), true);
        Predicate<ReadOnlyApplicationLink> filter = link -> {
            if (link == null) {
                return false;
            }
            URI displayLinkUri = link.getDisplayUrl();
            if (displayLinkUri == null) {
                return false;
            }
            String displayLinkUrl = displayLinkUri.toString();
            if (displayLinkUrl == null) {
                return false;
            }
            return displayLinkUrl.length() > 0 && lowerUrl.startsWith(displayLinkUrl.toLowerCase());
        };
        return targetStream.filter(filter).max(Comparator.comparingInt(o -> o.getDisplayUrl().toString().length()));
    }

    public String execute(Map parameters, String body, RenderContext renderContext) throws MacroException {
        try {
            return this.execute((Map<String, String>)parameters, body, (ConversionContext)null);
        }
        catch (MacroExecutionException e) {
            throw new MacroException((Throwable)e);
        }
    }

    private static URI toURI(String str) {
        try {
            return new URI(str);
        }
        catch (URISyntaxException e) {
            return null;
        }
    }
}

