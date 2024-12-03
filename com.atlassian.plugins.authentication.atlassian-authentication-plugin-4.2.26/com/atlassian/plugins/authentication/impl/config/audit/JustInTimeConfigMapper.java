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
import com.atlassian.plugins.authentication.api.config.IdpConfig;
import com.atlassian.plugins.authentication.api.config.JustInTimeConfig;
import com.atlassian.plugins.authentication.impl.config.audit.IdpConfigMapper;
import com.atlassian.plugins.authentication.impl.config.audit.KeyMapping;
import com.atlassian.plugins.authentication.impl.config.audit.MappingUtil;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import javax.inject.Named;

@Named
public class JustInTimeConfigMapper
implements IdpConfigMapper {
    public static final String ENABLED_I18N_KEY = "com.atlassian.plugins.authentication.audit.change.jit.enabled";
    public static final String DISPLAY_NAME_I18N_KEY = "com.atlassian.plugins.authentication.audit.change.jit.displayname";
    public static final String EMAIL_I18N_KEY = "com.atlassian.plugins.authentication.audit.change.jit.email";
    public static final String GROUPS_SOURCE_I18N_KEY = "com.atlassian.plugins.authentication.audit.change.jit.groups";
    public static final String ADDITIONAL_SCOPES_I18N_KEY = "com.atlassian.plugins.authentication.audit.change.jit.additionalscopes";
    private static final List<KeyMapping<JustInTimeConfig>> MAPPINGS = ImmutableList.builder().add(KeyMapping.mapping("com.atlassian.plugins.authentication.audit.change.jit.enabled", JustInTimeConfigMapper.extractOptional(JustInTimeConfig::isEnabled))).add(KeyMapping.mapping("com.atlassian.plugins.authentication.audit.change.jit.displayname", JustInTimeConfigMapper.extractOptional(JustInTimeConfig::getDisplayNameMappingExpression))).add(KeyMapping.mapping("com.atlassian.plugins.authentication.audit.change.jit.email", JustInTimeConfigMapper.extractOptional(JustInTimeConfig::getEmailMappingExpression))).add(KeyMapping.mapping("com.atlassian.plugins.authentication.audit.change.jit.groups", JustInTimeConfigMapper.extractOptional(JustInTimeConfig::getGroupsMappingSource))).add(KeyMapping.mapping("com.atlassian.plugins.authentication.audit.change.jit.additionalscopes", MappingUtil.toJson(JustInTimeConfig::getAdditionalJitScopes))).build();

    @Override
    public List<ChangedValue> mapChanges(@Nullable IdpConfig oldConfig, @Nullable IdpConfig newConfig) {
        return MAPPINGS.stream().map(mapping -> MappingUtil.mapChange(mapping, this.extractJustInTimeConfig(oldConfig), this.extractJustInTimeConfig(newConfig), JustInTimeConfig.class)).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
    }

    public static <T> Function<T, String> extractOptional(Function<T, Optional<?>> optionalFunction) {
        return optionalFunction.andThen(optional -> optional.map(Object::toString).orElse(""));
    }

    private JustInTimeConfig extractJustInTimeConfig(@Nullable IdpConfig idpConfig) {
        return Optional.ofNullable(idpConfig).map(IdpConfig::getJustInTimeConfig).orElse(null);
    }
}

