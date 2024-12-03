/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.troubleshooting.stp.zip;

import com.atlassian.troubleshooting.stp.zip.ClusteredSupportZipService;
import com.atlassian.troubleshooting.stp.zip.SupportZipRequest;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class NoopClusteredSupportZipService
implements ClusteredSupportZipService {
    private static final String NOT_IMPLEMENTED_MESSAGE = "The ATST plugin for this product does not support clustering";

    @Override
    @Nonnull
    public Optional<String> requestSupportZipCreationOnOtherNodes(SupportZipRequest supportZipRequest) {
        return Optional.of(NOT_IMPLEMENTED_MESSAGE);
    }

    @Override
    public void requestSupportZipCancellationOnOtherNodes(String taskId) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED_MESSAGE);
    }

    @Override
    public boolean isClusterSupportZipSupported() {
        return false;
    }
}

