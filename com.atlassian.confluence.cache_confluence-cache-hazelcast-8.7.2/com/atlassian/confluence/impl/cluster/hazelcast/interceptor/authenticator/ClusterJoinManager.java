/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.confluence.impl.cluster.hazelcast.interceptor.authenticator;

import com.atlassian.confluence.impl.cluster.hazelcast.interceptor.authenticator.ClusterJoinRequest;
import java.io.IOException;
import javax.annotation.Nonnull;

public interface ClusterJoinManager {
    public void accept(@Nonnull ClusterJoinRequest var1) throws IOException;

    public void connect(@Nonnull ClusterJoinRequest var1) throws IOException;
}

