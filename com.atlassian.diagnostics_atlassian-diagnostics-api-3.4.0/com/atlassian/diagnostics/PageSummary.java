/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.diagnostics;

import com.atlassian.diagnostics.PageRequest;
import java.util.Optional;
import javax.annotation.Nonnull;

public interface PageSummary {
    @Nonnull
    public Optional<PageRequest> getNextRequest();

    @Nonnull
    public Optional<PageRequest> getPrevRequest();

    public int size();
}

