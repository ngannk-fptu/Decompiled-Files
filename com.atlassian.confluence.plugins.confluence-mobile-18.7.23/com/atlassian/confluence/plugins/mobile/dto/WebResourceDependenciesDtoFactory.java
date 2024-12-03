/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.mobile.dto;

import com.atlassian.confluence.plugins.mobile.dto.WebResourceDependenciesDto;
import java.util.Set;

public interface WebResourceDependenciesDtoFactory {
    public WebResourceDependenciesDto getWebResourceDependenciesDto(Set<String> var1, Set<String> var2);

    public WebResourceDependenciesDto getWebResourceDependenciesDto(String var1, String var2);
}

