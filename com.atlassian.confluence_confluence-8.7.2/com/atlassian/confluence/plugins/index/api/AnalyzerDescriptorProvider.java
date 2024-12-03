/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 */
package com.atlassian.confluence.plugins.index.api;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.plugins.index.api.LanguageDescriptor;
import com.atlassian.confluence.plugins.index.api.MappingAnalyzerDescriptor;
import java.util.Optional;

@ExperimentalApi
@FunctionalInterface
public interface AnalyzerDescriptorProvider {
    public static final AnalyzerDescriptorProvider EMPTY = lang -> Optional.empty();

    public Optional<MappingAnalyzerDescriptor> getAnalyzer(LanguageDescriptor var1);
}

