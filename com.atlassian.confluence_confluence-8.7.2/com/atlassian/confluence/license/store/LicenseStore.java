/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.extras.api.AtlassianLicense
 *  com.google.common.annotations.VisibleForTesting
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.license.store;

import com.atlassian.annotations.Internal;
import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.extras.api.AtlassianLicense;
import com.google.common.annotations.VisibleForTesting;
import org.checkerframework.checker.nullness.qual.NonNull;

@ParametersAreNonnullByDefault
@Internal
public interface LicenseStore {
    public @NonNull AtlassianLicense retrieve();

    public void install(String var1);

    @VisibleForTesting
    public void installTransiently(String var1);

    @VisibleForTesting
    public void clearLicenseFromMemory();
}

