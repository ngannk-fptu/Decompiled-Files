/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.spaces.Space
 */
package com.atlassian.confluence.plugins.createcontent.services.model;

import com.atlassian.confluence.spaces.Space;

public class BlueprintSpace {
    private final Space space;

    public BlueprintSpace(Space space) {
        this.space = space;
    }

    public Space getSpace() {
        return this.space;
    }
}

