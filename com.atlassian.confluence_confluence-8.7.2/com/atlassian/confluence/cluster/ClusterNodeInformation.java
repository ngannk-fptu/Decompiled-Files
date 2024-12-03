/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.util.FugueConversionUtil
 *  com.atlassian.fugue.Maybe
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.cluster;

import com.atlassian.confluence.api.util.FugueConversionUtil;
import com.atlassian.fugue.Maybe;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.Optional;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface ClusterNodeInformation
extends Serializable {
    @Deprecated(forRemoval=true)
    public int getId();

    public InetSocketAddress getLocalSocketAddress();

    public @NonNull String getAnonymizedNodeIdentifier();

    @Deprecated(forRemoval=true)
    default public @NonNull Maybe<String> getHumanReadableNodeName() {
        return FugueConversionUtil.toComOption(this.humanReadableNodeName());
    }

    public @NonNull Optional<String> humanReadableNodeName();

    public boolean isLocal();
}

