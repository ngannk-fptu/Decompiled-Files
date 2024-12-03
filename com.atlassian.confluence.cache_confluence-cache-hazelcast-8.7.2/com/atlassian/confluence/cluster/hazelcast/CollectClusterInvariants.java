/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.cluster.ClusterInvariants
 *  com.google.common.base.Preconditions
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.cluster.hazelcast;

import com.atlassian.confluence.cluster.ClusterInvariants;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.util.concurrent.Callable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CollectClusterInvariants
implements Callable<ClusterInvariants>,
Serializable {
    private static final long serialVersionUID = -7900392314635172475L;
    private static final Logger log = LoggerFactory.getLogger(CollectClusterInvariants.class);
    private String requestingMember;

    public CollectClusterInvariants(String requestingMember) {
        this.requestingMember = (String)Preconditions.checkNotNull((Object)requestingMember);
    }

    @Override
    public ClusterInvariants call() throws Exception {
        log.debug("Collecing ClusterInvariants for {}", (Object)this.requestingMember);
        return new ClusterInvariants();
    }
}

