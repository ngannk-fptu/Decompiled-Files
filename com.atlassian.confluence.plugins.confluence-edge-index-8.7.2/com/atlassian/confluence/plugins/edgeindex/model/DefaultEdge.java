/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.ConfluenceUser
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.plugins.edgeindex.model;

import com.atlassian.confluence.plugins.edgeindex.model.Edge;
import com.atlassian.confluence.plugins.edgeindex.model.EdgeType;
import com.atlassian.confluence.user.ConfluenceUser;
import java.util.Date;
import org.checkerframework.checker.nullness.qual.NonNull;

public class DefaultEdge
implements Edge {
    private final ConfluenceUser user;
    private final EdgeType edgeType;
    private final Object target;
    private final Date date;
    private final Object edgeId;

    public DefaultEdge(ConfluenceUser user, EdgeType edgeType, Object target, Date date, @NonNull Object edgeId) {
        this.user = user;
        this.edgeType = edgeType;
        this.target = target;
        this.date = date;
        this.edgeId = edgeId;
    }

    @Override
    public ConfluenceUser getUser() {
        return this.user;
    }

    @Override
    public EdgeType getEdgeType() {
        return this.edgeType;
    }

    @Override
    public @NonNull Object getEdgeId() {
        return this.edgeId;
    }

    @Override
    public Object getTarget() {
        return this.target;
    }

    @Override
    public Date getDate() {
        return this.date;
    }
}

