/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.edgeindex.model;

import com.atlassian.confluence.plugins.edgeindex.model.EdgeTargetId;

public interface EdgeTargetInfo {
    public String getTargetType();

    public EdgeTargetId getTargetId();

    public float getScore();
}

