/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicSpi
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 */
package com.atlassian.confluence.api.model.index;

import com.atlassian.annotations.PublicSpi;
import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import java.io.File;
import java.io.IOException;

@Deprecated
@ParametersAreNonnullByDefault
@PublicSpi
public interface IndexRecoverer {
    public void snapshot(File var1) throws IOException;

    public void reset(Runnable var1);

    public void reindex();
}

