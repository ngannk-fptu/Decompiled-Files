/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.troubleshooting.preupgrade.modz;

import com.atlassian.troubleshooting.preupgrade.modz.VersionChecksumsDto;
import java.io.IOException;
import java.util.List;

public interface ChecksumResource {
    public List<VersionChecksumsDto> getChecksums();

    public void writeChecksums(List<VersionChecksumsDto> var1) throws IOException;
}

