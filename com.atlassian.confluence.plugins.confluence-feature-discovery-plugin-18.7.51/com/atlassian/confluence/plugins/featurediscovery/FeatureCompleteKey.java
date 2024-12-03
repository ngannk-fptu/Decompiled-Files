/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleCompleteKey
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Objects
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.featurediscovery;

import com.atlassian.plugin.ModuleCompleteKey;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Objects;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;

public class FeatureCompleteKey {
    @VisibleForTesting
    protected static final String SEPARATOR = ":";
    private final String context;
    private final String key;

    public FeatureCompleteKey(String featureKey) {
        this(StringUtils.substringBefore((String)featureKey, (String)SEPARATOR), StringUtils.substringAfter((String)featureKey, (String)SEPARATOR));
    }

    public FeatureCompleteKey(ModuleCompleteKey moduleCompleteKey) {
        this(moduleCompleteKey.getPluginKey(), moduleCompleteKey.getModuleKey());
    }

    public FeatureCompleteKey(String context, String key) {
        this.context = StringUtils.trimToEmpty((String)context);
        if (!this.isValidKey(this.context)) {
            throw new IllegalArgumentException("Invalid context specified: " + this.context);
        }
        this.key = StringUtils.trimToEmpty((String)key);
        if (StringUtils.isEmpty((CharSequence)this.key)) {
            throw new IllegalArgumentException("Invalid key specified: " + this.key);
        }
    }

    private boolean isValidKey(String key) {
        return StringUtils.isNotBlank((CharSequence)key) && !key.contains(SEPARATOR);
    }

    public String getKey() {
        return this.key;
    }

    public String getContext() {
        return this.context;
    }

    public String getCompleteKey() {
        return this.context + SEPARATOR + this.key;
    }

    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        FeatureCompleteKey that = (FeatureCompleteKey)o;
        return Objects.equal((Object)this.key, (Object)that.key) && Objects.equal((Object)this.context, (Object)that.context);
    }

    public int hashCode() {
        return Objects.hashCode((Object[])new Object[]{this.context, this.key});
    }

    public String toString() {
        return this.getCompleteKey();
    }
}

