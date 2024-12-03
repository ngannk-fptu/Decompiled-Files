/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.cluster.hazelcast.interceptor.authenticator;

import com.atlassian.confluence.impl.cluster.hazelcast.interceptor.authenticator.ClusterAuthenticationResult;
import com.atlassian.confluence.impl.cluster.hazelcast.interceptor.authenticator.ClusterJoinRequest;
import java.io.IOException;

public interface ClusterAuthenticator {
    public ClusterAuthenticationResult authenticate(ClusterJoinRequest var1) throws IOException;
}

