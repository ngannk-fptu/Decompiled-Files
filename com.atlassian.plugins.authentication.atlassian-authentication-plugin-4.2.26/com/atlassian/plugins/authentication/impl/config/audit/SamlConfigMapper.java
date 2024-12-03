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
import com.atlassian.plugins.authentication.api.config.saml.SamlConfig;
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
public class SamlConfigMapper
implements IdpConfigMapper {
    public static final String SAML_IDP_TYPE_KEY = "com.atlassian.plugins.authentication.audit.change.saml.type";
    public static final String SSO_URL_KEY = "com.atlassian.plugins.authentication.audit.change.saml.url";
    public static final String SSO_ISSUER_KEY = "com.atlassian.plugins.authentication.audit.change.saml.issuer";
    public static final String SSO_CERTIFICATE_KEY = "com.atlassian.plugins.authentication.audit.change.saml.certificate";
    public static final String SSO_USERNAME_ATTRIBUTE_KEY = "com.atlassian.plugins.authentication.audit.change.saml.username";
    private static final List<KeyMapping<SamlConfig>> MAPPINGS = ImmutableList.builder().add(KeyMapping.mapping("com.atlassian.plugins.authentication.audit.change.saml.type", c -> c.getIdpType().toString())).add(KeyMapping.mapping("com.atlassian.plugins.authentication.audit.change.saml.url", SamlConfig::getSsoUrl)).add(KeyMapping.mapping("com.atlassian.plugins.authentication.audit.change.saml.issuer", AbstractIdpConfig::getIssuer)).add(KeyMapping.mapping("com.atlassian.plugins.authentication.audit.change.saml.certificate", SamlConfig::getCertificate)).add(KeyMapping.mapping("com.atlassian.plugins.authentication.audit.change.saml.username", SamlConfig::getUsernameAttribute)).build();

    @Override
    public List<ChangedValue> mapChanges(@Nullable IdpConfig oldConfig, @Nullable IdpConfig newConfig) {
        return MAPPINGS.stream().map(keyMapping -> MappingUtil.mapChange(keyMapping, oldConfig, newConfig, SamlConfig.class)).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
    }
}

