/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 *  javax.inject.Named
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.authentication.impl.web.oidc;

import com.atlassian.plugins.authentication.api.config.oidc.OidcConfig;
import com.atlassian.plugins.authentication.impl.web.oidc.OidcDiscoveryException;
import com.atlassian.plugins.authentication.impl.web.oidc.OidcTimeouts;
import com.nimbusds.oauth2.sdk.GeneralException;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import java.io.IOException;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
public class OidcDiscoverySupport {
    private static final Logger log = LoggerFactory.getLogger(OidcDiscoverySupport.class);
    private final OidcTimeouts oidcTimeouts;

    @Inject
    public OidcDiscoverySupport(OidcTimeouts oidcTimeouts) {
        this.oidcTimeouts = oidcTimeouts;
    }

    public OidcConfig refresh(OidcConfig config) throws OidcDiscoveryException {
        OIDCProviderMetadata metadata;
        try {
            metadata = this.fetch(config.getIssuer());
        }
        catch (GeneralException | IOException e) {
            String strippedIssuer = StringUtils.stripEnd((String)config.getIssuer(), (String)"/");
            if (!strippedIssuer.equals(config.getIssuer())) {
                return this.refresh(((OidcConfig.Builder)config.toBuilder().setIssuer(strippedIssuer)).build());
            }
            throw new OidcDiscoveryException(e);
        }
        log.debug("Fetched configuration from [{}] using discovery.", (Object)config.getIssuer());
        OidcConfig oidcConfig = config.toBuilder().setAuthorizationEndpoint(metadata.getAuthorizationEndpointURI().toString()).setTokenEndpoint(metadata.getTokenEndpointURI().toString()).setUserInfoEndpoint(metadata.getUserInfoEndpointURI().toString()).build();
        log.debug("Fetched configuration from [{}] using discovery: {}", (Object)config.getIssuer(), (Object)oidcConfig);
        return oidcConfig;
    }

    protected OIDCProviderMetadata fetch(String issuerUrl) throws IOException, GeneralException {
        return OIDCProviderMetadata.resolve(new Issuer(issuerUrl), this.oidcTimeouts.getConnectTimeoutInMillis(), this.oidcTimeouts.getReadTimeoutInMillis());
    }
}

