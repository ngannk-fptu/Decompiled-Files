/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.entity.ChangedValue
 *  com.google.common.collect.ImmutableList
 *  javax.annotation.Nullable
 *  javax.inject.Named
 */
package com.atlassian.plugins.authentication.impl.config.audit;

import com.atlassian.audit.entity.ChangedValue;
import com.atlassian.plugins.authentication.api.config.AbstractIdpConfig;
import com.atlassian.plugins.authentication.api.config.IdpConfig;
import com.atlassian.plugins.authentication.api.config.oidc.OidcConfig;
import com.atlassian.plugins.authentication.impl.config.audit.IdpConfigMapper;
import com.atlassian.plugins.authentication.impl.config.audit.KeyMapping;
import com.atlassian.plugins.authentication.impl.config.audit.MappingUtil;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import javax.inject.Named;

@Named
public class OidcConfigMapper
implements IdpConfigMapper {
    public static final String CLIENT_ID_KEY = "com.atlassian.plugins.authentication.audit.change.oidc.id";
    public static final String CLIENT_SECRET_KEY = "com.atlassian.plugins.authentication.audit.change.oidc.secret";
    public static final String ISSUER_URL_KEY = "com.atlassian.plugins.authentication.audit.change.oidc.issuer";
    public static final String AUTHORIZATION_ENDPOINT_KEY = "com.atlassian.plugins.authentication.audit.change.oidc.endpoint.authorization";
    public static final String TOKEN_ENDPOINT_KEY = "com.atlassian.plugins.authentication.audit.change.oidc.endpoint.token";
    public static final String USER_INFO_ENDPOINT_KEY = "com.atlassian.plugins.authentication.audit.change.oidc.endpoint.userinfo";
    public static final String DISCOVERY_ENABLED_KEY = "com.atlassian.plugins.authentication.audit.change.oidc.discoveryenabled";
    public static final String ADDITIONAL_SCOPES_KEY = "com.atlassian.plugins.authentication.audit.change.oidc.additionalscopes";
    public static final String USERNAME_CLAIM_KEY = "com.atlassian.plugins.authentication.audit.change.oidc.usernameclaim";
    private static final List<KeyMapping<OidcConfig>> MAPPINGS = ImmutableList.builder().add(KeyMapping.mapping("com.atlassian.plugins.authentication.audit.change.oidc.id", OidcConfig::getClientId)).add(KeyMapping.mapping("com.atlassian.plugins.authentication.audit.change.oidc.secret", OidcConfig::getClientSecret, true)).add(KeyMapping.mapping("com.atlassian.plugins.authentication.audit.change.oidc.issuer", AbstractIdpConfig::getIssuer)).add(KeyMapping.mapping("com.atlassian.plugins.authentication.audit.change.oidc.endpoint.authorization", OidcConfig::getAuthorizationEndpoint)).add(KeyMapping.mapping("com.atlassian.plugins.authentication.audit.change.oidc.endpoint.token", OidcConfig::getTokenEndpoint)).add(KeyMapping.mapping("com.atlassian.plugins.authentication.audit.change.oidc.endpoint.userinfo", OidcConfig::getUserInfoEndpoint)).add(KeyMapping.mapping("com.atlassian.plugins.authentication.audit.change.oidc.discoveryenabled", oidcConfig -> String.valueOf(oidcConfig.isDiscoveryEnabled()))).add(KeyMapping.mapping("com.atlassian.plugins.authentication.audit.change.oidc.additionalscopes", MappingUtil.toJson(OidcConfig::getAdditionalScopes))).add(KeyMapping.mapping("com.atlassian.plugins.authentication.audit.change.oidc.usernameclaim", OidcConfig::getUsernameClaim)).build();

    @Override
    public List<ChangedValue> mapChanges(@Nullable IdpConfig oldConfig, @Nullable IdpConfig newConfig) {
        return MAPPINGS.stream().map(keyMapping -> MappingUtil.mapChange(keyMapping, oldConfig, newConfig, OidcConfig.class)).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
    }
}

