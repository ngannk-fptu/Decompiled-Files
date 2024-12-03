/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.beehive.ClusterLock
 *  com.atlassian.confluence.cluster.ClusteredLock
 */
package com.atlassian.confluence.cluster.hazelcast;

import com.atlassian.annotations.Internal;
import com.atlassian.beehive.ClusterLock;
import com.atlassian.confluence.cluster.ClusteredLock;

@Internal
interface DualLock
extends ClusteredLock,
ClusterLock {
}

