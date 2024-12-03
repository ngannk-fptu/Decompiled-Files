/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 */
package com.atlassian.plugins.authentication.api.config;

import com.atlassian.annotations.Internal;
import java.util.List;
import java.util.Optional;

@Internal
public interface JustInTimeConfig {
    public Optional<Boolean> isEnabled();

    public Optional<String> getDisplayNameMappingExpression();

    public Optional<String> getEmailMappingExpression();

    public Optional<String> getGroupsMappingSource();

    public List<String> getAdditionalJitScopes();
}

