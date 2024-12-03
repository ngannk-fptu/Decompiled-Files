/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.troubleshooting.stp.zip;

import com.atlassian.troubleshooting.stp.zip.SupportZipRequest;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface ClusteredSupportZipService {
    @Nonnull
    public Optional<String> requestSupportZipCreationOnOtherNodes(SupportZipRequest var1);

    public void requestSupportZipCancellationOnOtherNodes(String var1);

    default public boolean isClusterSupportZipSupported() {
        return true;
    }
}

