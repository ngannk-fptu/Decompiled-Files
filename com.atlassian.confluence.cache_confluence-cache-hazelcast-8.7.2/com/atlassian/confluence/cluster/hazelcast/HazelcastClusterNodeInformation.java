/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.cluster.ClusterNodeInformation
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Supplier
 *  com.google.common.base.Suppliers
 *  com.hazelcast.core.Member
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.cluster.hazelcast;

import com.atlassian.confluence.cluster.ClusterNodeInformation;
import com.atlassian.confluence.cluster.hazelcast.HazelcastUtils;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.hazelcast.core.Member;
import java.net.InetSocketAddress;
import java.util.Objects;
import java.util.Optional;
import org.checkerframework.checker.nullness.qual.NonNull;

public class HazelcastClusterNodeInformation
implements ClusterNodeInformation {
    private static final long serialVersionUID = 7196398488574659353L;
    private final Member member;
    private final Supplier<String> nodeIdentifierRef;

    HazelcastClusterNodeInformation(Member member) {
        this.member = (Member)Preconditions.checkNotNull((Object)member);
        this.nodeIdentifierRef = Suppliers.memoize(() -> HazelcastUtils.getMemberId(member));
    }

    @Deprecated
    public int getId() {
        return HazelcastClusterNodeInformation.generateId(this.member);
    }

    public InetSocketAddress getLocalSocketAddress() {
        return this.member.getSocketAddress();
    }

    public String getAnonymizedNodeIdentifier() {
        return (String)this.nodeIdentifierRef.get();
    }

    public @NonNull Optional<String> humanReadableNodeName() {
        return HazelcastUtils.getConfiguredMemberName(this.member);
    }

    public int hashCode() {
        return this.member.hashCode();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        HazelcastClusterNodeInformation that = (HazelcastClusterNodeInformation)obj;
        return Objects.equals(this.member, that.member);
    }

    @Deprecated
    static int generateId(Member mem) {
        return mem.getUuid().hashCode();
    }

    public String toString() {
        return this.member.toString();
    }

    public boolean isLocal() {
        return this.member.localMember();
    }
}

