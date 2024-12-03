/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.index.api;

import com.atlassian.confluence.plugins.index.api.AnalyzerDescriptorProvider;
import com.atlassian.confluence.plugins.index.api.LanguageDescriptor;
import java.util.Optional;

public interface MappingAnalyzerDescriptor
extends AnalyzerDescriptorProvider {
    @Override
    default public Optional<MappingAnalyzerDescriptor> getAnalyzer(LanguageDescriptor language) {
        return Optional.of(this);
    }
}

