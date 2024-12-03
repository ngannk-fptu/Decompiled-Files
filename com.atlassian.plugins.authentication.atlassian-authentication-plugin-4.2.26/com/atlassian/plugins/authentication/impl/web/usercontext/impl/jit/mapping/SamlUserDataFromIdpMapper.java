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
import com.atlassian.plugins.authentication.api.config.saml.SamlConfig;
import com.atlassian.plugins.authentication.impl.web.saml.provider.SamlResponse;
import com.atlassian.plugins.authentication.impl.web.usercontext.impl.jit.JitException;
import com.atlassian.plugins.authentication.impl.web.usercontext.impl.jit.mapping.JitUserData;
import com.atlassian.plugins.authentication.impl.web.usercontext.impl.jit.mapping.MappingExpression;
import com.google.common.collect.ImmutableSet;
import java.util.Set;
import java.util.function.Supplier;
import javax.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
public class SamlUserDataFromIdpMapper {
    private static final Logger log = LoggerFactory.getLogger(SamlUserDataFromIdpMapper.class);

    public JitUserData mapUser(SamlResponse samlResponse, String username, SamlConfig samlConfig) {
        JustInTimeConfig justInTimeConfig = samlConfig.getJustInTimeConfig();
        String idpId = samlResponse.getNameId();
        if (idpId == null) {
            throw new JitException("NameID not found");
        }
        String displayName = this.evaluateExpression(justInTimeConfig.getDisplayNameMappingExpression().orElseThrow(SamlUserDataFromIdpMapper.mappingConfigurationNotPresentException("display name")), samlResponse);
        String email = this.evaluateExpression(justInTimeConfig.getEmailMappingExpression().orElseThrow(SamlUserDataFromIdpMapper.mappingConfigurationNotPresentException("email")), samlResponse);
        Set<String> groups = this.mapGroups(justInTimeConfig.getGroupsMappingSource().orElseThrow(SamlUserDataFromIdpMapper.mappingConfigurationNotPresentException("groups")), samlResponse);
        return new JitUserData(idpId, username, displayName, email, groups);
    }

    private static Supplier<JitException> mappingConfigurationNotPresentException(String mappingKey) {
        return () -> new JitException("Configuration for " + mappingKey + " for SAML is not set");
    }

    private String extractAttribute(SamlResponse samlResponse, String attributeKey) {
        Iterable<String> attributes = samlResponse.getAttribute(attributeKey);
        if (attributes == null || !attributes.iterator().hasNext()) {
            log.error("Could not find {} in the SAML response, it could mean that there is misconfiguration", (Object)attributeKey);
            throw new JitException(String.format("Attribute [%s] could not be found", attributeKey));
        }
        return attributes.iterator().next();
    }

    private String evaluateExpression(String rawExpression, SamlResponse samlResponse) {
        MappingExpression expression = new MappingExpression(rawExpression);
        return expression.evaluateWithValues(varName -> varName.equalsIgnoreCase("NameId") ? samlResponse.getNameId() : this.extractAttribute(samlResponse, (String)varName));
    }

    private Set<String> mapGroups(String groupMapping, SamlResponse samlResponse) {
        Iterable<String> groups = samlResponse.getAttribute(groupMapping);
        if (groups == null) {
            throw new JitException(String.format("Attribute [%s] could not be found", groupMapping));
        }
        return ImmutableSet.copyOf(groups);
    }
}

