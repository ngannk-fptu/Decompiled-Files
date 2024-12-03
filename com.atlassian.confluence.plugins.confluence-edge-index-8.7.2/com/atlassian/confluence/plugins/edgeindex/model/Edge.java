/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.ConfluenceUser
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.plugins.edgeindex.model;

import com.atlassian.confluence.plugins.edgeindex.model.EdgeType;
import com.atlassian.confluence.user.ConfluenceUser;
import java.util.Date;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface Edge {
    public ConfluenceUser getUser();

    public Object getTarget();

    public Date getDate();

    public EdgeType getEdgeType();

    public @Nullable Object getEdgeId();
}

