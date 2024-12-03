/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  javax.inject.Named
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.authentication.impl.web.usercontext.impl.jit.mapping;

import com.atlassian.plugins.authentication.api.config.JustInTimeConfig;
import com.atlassian.plugins.authentication.api.config.oidc.OidcConfig;
import com.atlassian.plugins.authentication.impl.web.usercontext.impl.jit.JitException;
import com.atlassian.plugins.authentication.impl.web.usercontext.impl.jit.mapping.JitUserData;
import com.atlassian.plugins.authentication.impl.web.usercontext.impl.jit.mapping.MappingExpression;
import com.google.common.collect.ImmutableSet;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.openid.connect.sdk.token.OIDCTokens;
import java.text.ParseException;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import javax.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
public class OidcUserDataFromIdpMapper {
    private static final Logger log = LoggerFactory.getLogger(OidcUserDataFromIdpMapper.class);

    public JitUserData mapUser(OIDCTokens tokens, String username, OidcConfig oidcConfig) {
        try {
            JustInTimeConfig justInTimeConfig = oidcConfig.getJustInTimeConfig();
            JWTClaimsSet jwtClaimsSet = tokens.getIDToken().getJWTClaimsSet();
            log.trace("Claims received in response for IdP: {}", jwtClaimsSet.getClaims().keySet());
            String subject = jwtClaimsSet.getSubject();
            if (subject == null) {
                throw new JitException("Subject not found");
            }
            String displayName = this.evaluateExpression(justInTimeConfig.getDisplayNameMappingExpression().orElseThrow(OidcUserDataFromIdpMapper.mappingConfigurationNotPresentException("display name")), jwtClaimsSet);
            String email = this.evaluateExpression(justInTimeConfig.getEmailMappingExpression().orElseThrow(OidcUserDataFromIdpMapper.mappingConfigurationNotPresentException("email")), jwtClaimsSet);
            List<String> parsedGroups = jwtClaimsSet.getStringListClaim(justInTimeConfig.getGroupsMappingSource().orElseThrow(OidcUserDataFromIdpMapper.mappingConfigurationNotPresentException("groups")));
            if (parsedGroups == null) {
                throw new JitException(String.format("Received no groups claim in OIDC response, the group mapping may be incorrect. Mapping user '%s' for IdP '%s'", username, oidcConfig.getName()));
            }
            ImmutableSet groups = ImmutableSet.copyOf(parsedGroups);
            return new JitUserData(subject, username, displayName, email, (Set<String>)groups);
        }
        catch (Exception e) {
            throw new JitException(e);
        }
    }

    private static Supplier<IllegalStateException> mappingConfigurationNotPresentException(String mappingKey) {
        return () -> new IllegalStateException("Configuration for " + mappingKey + " for OIDC is not set");
    }

    private String evaluateExpression(String rawExpression, JWTClaimsSet claims) {
        MappingExpression expression = new MappingExpression(rawExpression);
        return expression.evaluateWithValues(varName -> this.getStringClaimOrFail(claims, (String)varName));
    }

    private String getStringClaimOrFail(JWTClaimsSet claimSet, String claimName) {
        try {
            String claim = claimSet.getStringClaim(claimName);
            if (claim != null) {
                return claim;
            }
            throw new JitException(String.format("Claim [%s] could not be found", claimName));
        }
        catch (ParseException exception) {
            throw new JitException(String.format("Claim [%s] was not of type String", claimName));
        }
    }
}

