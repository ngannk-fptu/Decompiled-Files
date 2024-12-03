/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  javax.annotation.Nonnull
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.impl.cluster.hazelcast.interceptor.authenticator;

import com.google.common.collect.ImmutableList;
import java.net.ConnectException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;

public class NodeConnectionException
extends ConnectException {
    private final List<String> issues;

    public NodeConnectionException(String msg) {
        this.issues = ImmutableList.of((Object)msg);
    }

    public NodeConnectionException(@Nonnull Collection<String> issues) {
        this.issues = ImmutableList.copyOf(Objects.requireNonNull(issues, "issues"));
    }

    @Nonnull
    List<String> getIssues() {
        return this.issues;
    }

    @Override
    public String getMessage() {
        return StringUtils.join(this.issues, (String)", ");
    }
}

