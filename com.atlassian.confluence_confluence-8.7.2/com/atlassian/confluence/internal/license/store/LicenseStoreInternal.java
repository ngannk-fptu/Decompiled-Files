/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.extras.api.AtlassianLicense
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.internal.license.store;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.confluence.license.exception.LicenseException;
import com.atlassian.confluence.license.store.LicenseStore;
import com.atlassian.extras.api.AtlassianLicense;
import java.util.Optional;
import org.checkerframework.checker.nullness.qual.NonNull;

@ParametersAreNonnullByDefault
public interface LicenseStoreInternal
extends LicenseStore {
    public @NonNull Optional<AtlassianLicense> retrieveOptional() throws LicenseException;
}

