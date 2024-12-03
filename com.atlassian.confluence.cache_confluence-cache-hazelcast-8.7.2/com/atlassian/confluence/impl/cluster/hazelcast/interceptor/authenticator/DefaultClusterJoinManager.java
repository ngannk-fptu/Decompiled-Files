/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.cluster.hazelcast.interceptor.authenticator;

import com.atlassian.confluence.impl.cluster.hazelcast.interceptor.authenticator.ClusterAuthenticationResult;
import com.atlassian.confluence.impl.cluster.hazelcast.interceptor.authenticator.ClusterAuthenticator;
import com.atlassian.confluence.impl.cluster.hazelcast.interceptor.authenticator.ClusterJoinManager;
import com.atlassian.confluence.impl.cluster.hazelcast.interceptor.authenticator.ClusterJoinMode;
import com.atlassian.confluence.impl.cluster.hazelcast.interceptor.authenticator.ClusterJoinRequest;
import com.atlassian.confluence.impl.cluster.hazelcast.interceptor.authenticator.NodeConnectionException;
import com.google.common.base.Preconditions;
import java.io.IOException;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultClusterJoinManager
implements ClusterJoinManager {
    private static final Logger log = LoggerFactory.getLogger(DefaultClusterJoinManager.class);
    private final ClusterAuthenticator clusterAuthenticator;
    private final boolean isNodeAuthenticationEnabled;

    public DefaultClusterJoinManager(ClusterAuthenticator clusterAuthenticator, boolean enableNodeAuthentication) {
        this.clusterAuthenticator = clusterAuthenticator;
        this.isNodeAuthenticationEnabled = enableNodeAuthentication;
    }

    @Override
    public void accept(@Nonnull ClusterJoinRequest request) throws IOException {
        log.info("enableNodeAuthentication : {}", (Object)this.isNodeAuthenticationEnabled);
        log.debug("{}: Authenticating cluster node in accept .... ", (Object)request);
        Preconditions.checkArgument((Objects.requireNonNull(request, "request").getJoinMode() == ClusterJoinMode.ACCEPT ? 1 : 0) != 0, (Object)"Expected accept request");
        this.checkNodeAuthenticationEnabled(request);
    }

    @Override
    public void connect(@Nonnull ClusterJoinRequest request) throws IOException {
        log.info("enableNodeAuthentication : {}", (Object)this.isNodeAuthenticationEnabled);
        log.debug("{}: Authenticating cluster node in connect .... ", (Object)request);
        Preconditions.checkArgument((Objects.requireNonNull(request, "request").getJoinMode() == ClusterJoinMode.CONNECT ? 1 : 0) != 0, (Object)"Expected connect request");
        this.checkNodeAuthenticationEnabled(request);
    }

    private void checkNodeAuthenticationEnabled(@Nonnull ClusterJoinRequest request) throws IOException {
        if (this.isNodeAuthenticationEnabled) {
            log.debug("{}: Authenticating cluster node", (Object)request);
            ClusterAuthenticationResult authenticationResult = this.clusterAuthenticator.authenticate(request);
            if (!authenticationResult.isSuccessful()) {
                log.warn("{}: Node authentication failed: {} ", (Object)request, (Object)authenticationResult.getMessage());
                throw new NodeConnectionException(authenticationResult.getMessage());
            }
            log.debug("{}: Node authenticated success", (Object)request);
        } else {
            log.debug("{}: Node authentication disabled", (Object)request);
        }
    }
}

