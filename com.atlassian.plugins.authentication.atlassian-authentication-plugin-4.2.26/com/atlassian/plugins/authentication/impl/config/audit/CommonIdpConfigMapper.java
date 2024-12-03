/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.entity.ChangedValue
 *  com.google.common.collect.ImmutableList
 *  javax.inject.Named
 *  org.jetbrains.annotations.Nullable
 */
package com.atlassian.plugins.authentication.impl.config.audit;

import com.atlassian.audit.entity.ChangedValue;
import com.atlassian.plugins.authentication.api.config.IdpConfig;
import com.atlassian.plugins.authentication.impl.config.audit.IdpConfigMapper;
import com.atlassian.plugins.authentication.impl.config.audit.KeyMapping;
import com.atlassian.plugins.authentication.impl.config.audit.MappingUtil;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.inject.Named;
import org.jetbrains.annotations.Nullable;

@Named
public class CommonIdpConfigMapper
implements IdpConfigMapper {
    public static final String NAME_KEY = "com.atlassian.plugins.authentication.audit.change.name";
    public static final String SSO_TYPE_KEY = "com.atlassian.plugins.authentication.audit.change.ssotype";
    public static final String ENABLED_KEY = "com.atlassian.plugins.authentication.audit.change.enabled";
    public static final String INCLUDE_CUSTOMER_LOGINS_KEY = "com.atlassian.plugins.authentication.audit.change.includecustomerslogin";
    public static final String ENABLE_REMEMBER_ME_KEY = "com.atlassian.plugins.authentication.audit.change.rememberme";
    public static final String BUTTON_TEXT_KEY = "com.atlassian.plugins.authentication.audit.change.buttontext";
    private static final List<KeyMapping<IdpConfig>> MAPPINGS = ImmutableList.builder().add(KeyMapping.mapping("com.atlassian.plugins.authentication.audit.change.enabled", idpConfig -> String.valueOf(idpConfig.isEnabled()))).add(KeyMapping.mapping("com.atlassian.plugins.authentication.audit.change.name", IdpConfig::getName)).add(KeyMapping.mapping("com.atlassian.plugins.authentication.audit.change.ssotype", idpConfig -> idpConfig.getSsoType().toString())).add(KeyMapping.mapping("com.atlassian.plugins.authentication.audit.change.includecustomerslogin", idpConfig -> String.valueOf(idpConfig.isIncludeCustomerLogins()))).add(KeyMapping.mapping("com.atlassian.plugins.authentication.audit.change.rememberme", idpConfig -> String.valueOf(idpConfig.isEnableRememberMe()))).add(KeyMapping.mapping("com.atlassian.plugins.authentication.audit.change.buttontext", IdpConfig::getButtonText)).build();

    @Override
    public List<ChangedValue> mapChanges(@Nullable IdpConfig oldConfig, @Nullable IdpConfig newConfig) {
        return MAPPINGS.stream().map(keyMapping -> MappingUtil.mapChange(keyMapping, oldConfig, newConfig, IdpConfig.class)).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
    }
}

