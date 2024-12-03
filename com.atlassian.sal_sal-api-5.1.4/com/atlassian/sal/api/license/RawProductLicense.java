/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.sal.api.license;

import java.util.Optional;
import javax.annotation.Nonnull;

public interface RawProductLicense {
    @Nonnull
    public Optional<String> getProductKey();

    @Nonnull
    public Optional<String> getLicense();

    public boolean isDeleteLicense();
}

