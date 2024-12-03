/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.troubleshooting.preupgrade.modz;

import com.atlassian.troubleshooting.preupgrade.modz.ChecksumResource;
import com.atlassian.troubleshooting.preupgrade.modz.VersionChecksumsDto;
import java.util.List;

public class NoopChecksumResource
implements ChecksumResource {
    @Override
    public List<VersionChecksumsDto> getChecksums() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void writeChecksums(List<VersionChecksumsDto> versions) {
        throw new UnsupportedOperationException("Not implemented");
    }
}

