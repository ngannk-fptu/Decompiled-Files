/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleCompleteKey
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nullable
 */
package com.atlassian.confluence.plugins.featurediscovery;

import com.atlassian.confluence.plugins.featurediscovery.FeatureCompleteKey;
import com.atlassian.confluence.plugins.featurediscovery.entity.FeatureMetadataAo;
import com.atlassian.plugin.ModuleCompleteKey;
import com.google.common.base.Preconditions;
import java.util.Date;
import java.util.Objects;
import javax.annotation.Nullable;

public class FeatureMetadata {
    private final FeatureCompleteKey featureCompleteKey;
    private final Date installationDate;

    public FeatureMetadata(FeatureMetadataAo featureMetadataEntity) {
        this(featureMetadataEntity.getContext(), featureMetadataEntity.getKey(), featureMetadataEntity.getInstallationDate());
    }

    public FeatureMetadata(ModuleCompleteKey moduleCompleteKey, Date installationDate) {
        this(moduleCompleteKey.getPluginKey(), moduleCompleteKey.getModuleKey(), installationDate);
    }

    public FeatureMetadata(FeatureCompleteKey featureCompleteKey, Date installationDate) {
        this(featureCompleteKey.getContext(), featureCompleteKey.getKey(), installationDate);
    }

    public FeatureMetadata(String context, String key, Date installationDate) {
        this.featureCompleteKey = new FeatureCompleteKey(context, key);
        this.installationDate = (Date)Preconditions.checkNotNull((Object)installationDate, (Object)"installationDate cannot be null");
    }

    public String getContext() {
        return this.featureCompleteKey.getContext();
    }

    public String getKey() {
        return this.featureCompleteKey.getKey();
    }

    public FeatureCompleteKey getFeatureCompleteKey() {
        return this.featureCompleteKey;
    }

    public Date getInstallationDate() {
        return this.installationDate;
    }

    public int hashCode() {
        return Objects.hash(this.featureCompleteKey, this.installationDate.getTime());
    }

    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FeatureMetadata)) {
            return false;
        }
        FeatureMetadata that = (FeatureMetadata)o;
        return Objects.equals(this.featureCompleteKey, that.featureCompleteKey) && Objects.equals(this.installationDate.getTime(), that.installationDate.getTime());
    }
}

