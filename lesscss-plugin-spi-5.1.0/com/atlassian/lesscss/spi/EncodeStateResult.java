/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.webresource.api.assembler.resource.PrebakeError
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nonnull
 */
package com.atlassian.lesscss.spi;

import com.atlassian.webresource.api.assembler.resource.PrebakeError;
import com.google.common.base.Preconditions;
import java.util.Optional;
import javax.annotation.Nonnull;

public class EncodeStateResult {
    private final String state;
    private final Optional<PrebakeError> prebakeError;

    public EncodeStateResult(@Nonnull String state, @Nonnull Optional<PrebakeError> prebakeError) {
        this.state = (String)Preconditions.checkNotNull((Object)state);
        this.prebakeError = (Optional)Preconditions.checkNotNull(prebakeError);
    }

    public String getState() {
        return this.state;
    }

    public Optional<PrebakeError> getPrebakeError() {
        return this.prebakeError;
    }
}

