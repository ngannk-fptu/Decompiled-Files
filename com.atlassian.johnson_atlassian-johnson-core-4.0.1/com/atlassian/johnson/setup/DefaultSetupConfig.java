/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nonnull
 */
package com.atlassian.johnson.setup;

import com.atlassian.johnson.setup.SetupConfig;
import com.google.common.base.Preconditions;
import javax.annotation.Nonnull;

public class DefaultSetupConfig
implements SetupConfig {
    @Override
    public boolean isSetup() {
        return true;
    }

    @Override
    public boolean isSetupPage(@Nonnull String uri) {
        Preconditions.checkNotNull((Object)uri, (Object)"uri");
        return false;
    }
}

