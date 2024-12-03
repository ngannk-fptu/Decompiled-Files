/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationLinkService
 *  com.atlassian.applinks.host.spi.InternalHostApplication
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.applinks.ui.validators;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkService;
import com.atlassian.applinks.core.util.MessageFactory;
import com.atlassian.applinks.host.spi.InternalHostApplication;
import com.atlassian.applinks.ui.AbstractApplinksServlet;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.stream.StreamSupport;
import org.springframework.beans.factory.annotation.Autowired;

public class CallbackParameterValidator {
    private final MessageFactory messageFactory;
    private final InternalHostApplication internalHostApplication;
    private final ApplicationLinkService applicationLinkService;

    @Autowired
    public CallbackParameterValidator(MessageFactory messageFactory, InternalHostApplication internalHostApplication, ApplicationLinkService applicationLinkService) {
        this.messageFactory = messageFactory;
        this.internalHostApplication = internalHostApplication;
        this.applicationLinkService = applicationLinkService;
    }

    public boolean isCallbackUrlValid(String callbackUrl) {
        try {
            this.validate(callbackUrl);
            return true;
        }
        catch (AbstractApplinksServlet.BadRequestException e) {
            return false;
        }
    }

    public void validate(String redirectUri) throws AbstractApplinksServlet.BadRequestException {
        URI callbackUri;
        URI baseUrl = this.internalHostApplication.getBaseUrl();
        try {
            callbackUri = new URI(redirectUri);
        }
        catch (URISyntaxException e) {
            throw this.createBadRequestException(redirectUri);
        }
        if (redirectUri.startsWith("//")) {
            throw this.createBadRequestException(redirectUri);
        }
        if (!callbackUri.isAbsolute() && !redirectUri.startsWith(baseUrl.getPath())) {
            throw this.createBadRequestException(redirectUri);
        }
        if (!callbackUri.isAbsolute()) {
            return;
        }
        if (!this.validateAbsoluteUri(baseUrl, callbackUri) && !this.isPointingToAnyApplinkUrl(callbackUri)) {
            throw this.createBadRequestException(redirectUri);
        }
    }

    private boolean isPointingToAnyApplinkUrl(URI callbackUri) {
        return StreamSupport.stream(this.applicationLinkService.getApplicationLinks().spliterator(), false).anyMatch(applicationLink -> this.validateCallbackUriAgainstApplink(callbackUri, (ApplicationLink)applicationLink));
    }

    public boolean validateCallbackUriAgainstApplink(URI callbackUri, ApplicationLink applicationLink) {
        boolean isValidByDisplayUrl = this.validateAbsoluteUri(applicationLink.getDisplayUrl(), callbackUri);
        boolean isValidByRpcUrl = this.validateAbsoluteUri(applicationLink.getRpcUrl(), callbackUri);
        return isValidByDisplayUrl || isValidByRpcUrl;
    }

    public boolean validateAbsoluteUri(URI baseUri, URI targetUri) {
        if (!baseUri.getScheme().equals(targetUri.getScheme())) {
            return false;
        }
        if (!baseUri.getHost().equals(targetUri.getHost())) {
            return false;
        }
        if (baseUri.getPort() != targetUri.getPort()) {
            return false;
        }
        return targetUri.getPath().startsWith(baseUri.getPath());
    }

    private AbstractApplinksServlet.BadRequestException createBadRequestException(String redirectUri) {
        return new AbstractApplinksServlet.BadRequestException(this.messageFactory.newI18nMessage("auth.config.parameter.callback.invalid", new Serializable[]{redirectUri}));
    }
}

