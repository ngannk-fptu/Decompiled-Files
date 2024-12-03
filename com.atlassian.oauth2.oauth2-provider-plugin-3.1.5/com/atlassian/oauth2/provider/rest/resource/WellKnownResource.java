/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  com.atlassian.annotations.security.XsrfProtectionExcluded
 *  com.atlassian.oauth2.provider.api.external.OAuth2AuthorizationServerMetadata
 *  com.atlassian.oauth2.provider.api.external.OAuth2ProviderService
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.sun.jersey.spi.resource.Singleton
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 */
package com.atlassian.oauth2.provider.rest.resource;

import com.atlassian.annotations.PublicApi;
import com.atlassian.annotations.security.XsrfProtectionExcluded;
import com.atlassian.oauth2.provider.api.external.OAuth2AuthorizationServerMetadata;
import com.atlassian.oauth2.provider.api.external.OAuth2ProviderService;
import com.atlassian.oauth2.provider.rest.model.RestOAuth2AuthorizationServerMetadata;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.sun.jersey.spi.resource.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path(value=".well-known")
@Produces(value={"application/json"})
@Singleton
@AnonymousAllowed
public class WellKnownResource {
    private final OAuth2ProviderService oAuth2ProviderService;

    public WellKnownResource(OAuth2ProviderService oAuth2ProviderService) {
        this.oAuth2ProviderService = oAuth2ProviderService;
    }

    @XsrfProtectionExcluded
    @GET
    @Path(value="/oauth-authorization-server")
    @PublicApi
    public Response getOAuthAuthorizationServerMetadata() {
        OAuth2AuthorizationServerMetadata oAuth2AuthorizationServerMetadata = this.oAuth2ProviderService.getOAuth2AuthorizationServerMetadata();
        return Response.ok((Object)RestOAuth2AuthorizationServerMetadata.builder().issuer(oAuth2AuthorizationServerMetadata.getIssuer()).tokenEndpoint(oAuth2AuthorizationServerMetadata.getTokenEndpoint()).revocationEndpoint(oAuth2AuthorizationServerMetadata.getRevocationEndpoint()).authorizationEndpoint(oAuth2AuthorizationServerMetadata.getAuthorizationEndpoint()).scopesSupported(oAuth2AuthorizationServerMetadata.getScopesSupported()).responseTypesSupported(oAuth2AuthorizationServerMetadata.getResponseTypesSupported()).responseModesSupported(oAuth2AuthorizationServerMetadata.getResponseModesSupported()).grantTypesSupported(oAuth2AuthorizationServerMetadata.getGrantTypesSupported()).tokenEndpointAuthMethodsSupported(oAuth2AuthorizationServerMetadata.getTokenEndpointAuthMethodsSupported()).revocationEndpointAuthMethodsSupported(oAuth2AuthorizationServerMetadata.getRevocationEndpointAuthMethodsSupported()).build()).build();
    }
}

