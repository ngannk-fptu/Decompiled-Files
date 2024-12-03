/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.ApplicationProperties
 *  javax.annotation.Nonnull
 */
package com.atlassian.analytics.client.properties;

import com.atlassian.sal.api.ApplicationProperties;
import java.io.File;
import java.util.Optional;
import javax.annotation.Nonnull;

public interface AnalyticsPropertyService
extends ApplicationProperties {
    @Nonnull
    default public Optional<String> getHomeDirectoryAbsolutePath() {
        return Optional.ofNullable(this.getHomeDirectory()).map(File::getAbsolutePath);
    }
}

