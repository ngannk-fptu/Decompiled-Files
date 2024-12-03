/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 */
package com.atlassian.confluence.internal.diagnostics.ipd.userdirectory.service;

import com.atlassian.crowd.embedded.api.Directory;
import java.time.Duration;
import java.util.Optional;
import java.util.stream.Stream;

public interface UserDirectoryConnectionService {
    public boolean getConnectionState(Directory var1);

    public Optional<Duration> getLatency(Directory var1);

    public Stream<Directory> findAllActiveExternalDirectories();
}

