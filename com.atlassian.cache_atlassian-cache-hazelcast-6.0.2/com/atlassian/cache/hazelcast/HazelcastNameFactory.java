/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nonnull.ReturnValuesAreNonnullByDefault
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.cache.hazelcast;

import com.atlassian.annotations.nonnull.ReturnValuesAreNonnullByDefault;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@ReturnValuesAreNonnullByDefault
public interface HazelcastNameFactory {
    public String getCacheIMapName(String var1);

    public String getCachedReferenceIMapName(String var1);

    public String getCacheInvalidationTopicName(String var1);

    public String getCachedReferenceInvalidationTopicName(String var1);

    public String getCacheVersionCounterName(String var1);
}

