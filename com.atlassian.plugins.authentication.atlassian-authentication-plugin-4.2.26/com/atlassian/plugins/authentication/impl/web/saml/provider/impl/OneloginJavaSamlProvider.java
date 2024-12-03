/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.classloader.DelegationClassLoader
 *  com.atlassian.plugin.hostcontainer.HostContainer
 *  com.atlassian.plugin.util.ContextClassLoaderSwitchingUtil
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Strings
 *  com.google.common.collect.ImmutableSet
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.inject.Inject
 *  javax.inject.Named
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.ws.rs.core.UriBuilder
 */
package com.atlassian.plugins.authentication.impl.web.saml.provider.impl;

import com.atlassian.plugin.classloader.DelegationClassLoader;
import com.atlassian.plugin.hostcontainer.HostContainer;
import com.atlassian.plugin.util.ContextClassLoaderSwitchingUtil;
import com.atlassian.plugins.authentication.api.config.saml.SamlConfig;
import com.atlassian.plugins.authentication.impl.util.ValidationUtils;
import com.atlassian.plugins.authentication.impl.web.AuthenticationHandlerNotConfiguredException;
import com.atlassian.plugins.authentication.impl.web.saml.TrackingCompatibilityModeResponseHandler;
import com.atlassian.plugins.authentication.impl.web.saml.provider.InvalidSamlResponse;
import com.atlassian.plugins.authentication.impl.web.saml.provider.SamlProvider;
import com.atlassian.plugins.authentication.impl.web.saml.provider.SamlRequest;
import com.atlassian.plugins.authentication.impl.web.saml.provider.SamlResponse;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.onelogin.saml2.Auth;
import com.onelogin.saml2.authn.AuthnRequest;
import com.onelogin.saml2.exception.SettingsException;
import com.onelogin.saml2.servlet.ServletUtils;
import com.onelogin.saml2.settings.Saml2Settings;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.Instant;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.UriBuilder;

@Named
public class OneloginJavaSamlProvider
implements SamlProvider {
    private final TrackingCompatibilityModeResponseHandler conditionlessResponseHandler;

    @Inject
    OneloginJavaSamlProvider(TrackingCompatibilityModeResponseHandler conditionlessResponseHandler) {
        this.conditionlessResponseHandler = conditionlessResponseHandler;
    }

    @Override
    public SamlRequest createSamlSingleSignOnRequest(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull SamlProvider.ServiceProviderInfo serviceProviderInfo, boolean forceAuth, SamlConfig samlConfig) {
        try {
            Auth auth = this.createAuth(request, response, serviceProviderInfo, samlConfig);
            Saml2Settings settings = auth.getSettings();
            AuthnRequest authnRequest = new AuthnRequest(settings, forceAuth, false, false);
            String samlRequest = authnRequest.getEncodedAuthnRequest();
            try {
                UriBuilder uriBuilder = UriBuilder.fromUri((URI)settings.getIdpSingleSignOnServiceUrl().toURI());
                if (!Strings.isNullOrEmpty((String)samlRequest)) {
                    uriBuilder.queryParam("SAMLRequest", new Object[]{samlRequest});
                }
                String relayState = UUID.randomUUID().toString();
                uriBuilder.queryParam("RelayState", new Object[]{relayState});
                return new SamlRequest(authnRequest.getId(), uriBuilder.build(new Object[0]).toString(), relayState);
            }
            catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public SamlResponse extractSamlResponse(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull SamlProvider.ServiceProviderInfo serviceProviderInfo, @Nonnull SamlConfig samlConfig, @Nullable SamlRequest samlRequest) throws InvalidSamlResponse {
        try {
            return (SamlResponse)ContextClassLoaderSwitchingUtil.runInContext((ClassLoader)((Object)new ServiceOverridingClassLoader()), () -> {
                Auth auth = this.createAuth(request, response, serviceProviderInfo, samlConfig);
                try {
                    auth.processResponse(samlRequest == null ? null : samlRequest.getId());
                }
                catch (Exception e) {
                    throw new InvalidSamlResponse(e);
                }
                if (!auth.isAuthenticated()) {
                    throw new InvalidSamlResponse("Received invalid SAML response: " + auth.getLastErrorReason());
                }
                return new SamlResponse(auth.getNameId(), auth.getAttributes(), auth.getLastAssertionId(), auth.getLastAssertionNotOnOrAfter().stream().map(jodaInstant -> Instant.ofEpochMilli(jodaInstant.getMillis())).collect(Collectors.toList()));
            });
        }
        catch (InvalidSamlResponse e) {
            throw e;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<String> getIssuers(HttpServletRequest request) {
        try {
            return (List)ContextClassLoaderSwitchingUtil.runInContext((ClassLoader)((Object)new ServiceOverridingClassLoader()), () -> new com.onelogin.saml2.authn.SamlResponse(new Saml2Settings(), ServletUtils.makeHttpRequest(request)).getIssuers());
        }
        catch (Exception e) {
            throw new InvalidSamlResponse("Received invalid SAML response", e);
        }
    }

    private Auth createAuth(HttpServletRequest request, HttpServletResponse response, SamlProvider.ServiceProviderInfo serviceProviderInfo, SamlConfig samlConfig) {
        try {
            Saml2Settings samlSettings = this.createSettings(serviceProviderInfo, samlConfig);
            return new Auth(samlSettings, request, response);
        }
        catch (SettingsException e) {
            throw new AuthenticationHandlerNotConfiguredException("Invalid SAML configuration", e);
        }
    }

    @VisibleForTesting
    Saml2Settings createSettings(final SamlProvider.ServiceProviderInfo serviceProviderInfo, final SamlConfig samlConfig) {
        return new Saml2Settings(){
            {
                this.setStrict(true);
                this.setWantAssertionsSigned(true);
                this.setWantAssertionsEncrypted(false);
                this.setRejectUnsolicitedResponsesWithInResponseTo(true);
                this.setSpEntityId(serviceProviderInfo.getIssuerUrl());
                this.setSpAssertionConsumerServiceUrl(ValidationUtils.convertToUrl(serviceProviderInfo.getConsumerServiceUrl()));
                this.setIdpEntityId(samlConfig.getIssuer());
                this.setIdpSingleSignOnServiceUrl(ValidationUtils.convertToUrl(samlConfig.getSsoUrl()));
                this.setIdpx509cert(ValidationUtils.convertToCertificate(samlConfig.getCertificate()));
                this.setCompatibilityMode(true);
                this.setCompatibilityModeViolationHandler(OneloginJavaSamlProvider.this.conditionlessResponseHandler);
            }
        };
    }

    private static class ServiceOverridingClassLoader
    extends DelegationClassLoader {
        private static final Set<String> OVERRIDDEN_SERVICE_RESOURCES = ImmutableSet.of((Object)"META-INF/services/javax.xml.validation.SchemaFactory", (Object)"META-INF/services/javax.xml.xpath.XPathFactory", (Object)"META-INF/services/javax.xml.parsers.DocumentBuilderFactory");

        private ServiceOverridingClassLoader() {
            this.setDelegateClassLoader(HostContainer.class.getClassLoader());
        }

        public Enumeration<URL> getResources(String name) throws IOException {
            if (OVERRIDDEN_SERVICE_RESOURCES.contains(name)) {
                return this.findResources(name);
            }
            return super.getResources(name);
        }
    }
}

