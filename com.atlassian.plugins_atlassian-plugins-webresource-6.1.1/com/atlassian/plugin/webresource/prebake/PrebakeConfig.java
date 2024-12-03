/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableMap
 *  javax.annotation.Nonnull
 *  org.apache.commons.lang3.text.StrSubstitutor
 */
package com.atlassian.plugin.webresource.prebake;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import java.io.File;
import java.util.Map;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.text.StrSubstitutor;

public interface PrebakeConfig {
    public static PrebakeConfig forPattern(final @Nonnull String mappingPathPattern) {
        Preconditions.checkNotNull((Object)mappingPathPattern, (Object)"mappingPathPattern null!");
        return new PrebakeConfig(){

            @Override
            @Nonnull
            public File getMappingLocation(@Nonnull String globalStateHash) {
                Preconditions.checkNotNull((Object)globalStateHash, (Object)"globalStateHash null!");
                return new File(StrSubstitutor.replace((Object)mappingPathPattern, (Map)ImmutableMap.builder().put((Object)"state", (Object)globalStateHash).build()));
            }

            @Override
            public String getPattern() {
                return mappingPathPattern;
            }
        };
    }

    @Nonnull
    public File getMappingLocation(@Nonnull String var1);

    public String getPattern();
}

