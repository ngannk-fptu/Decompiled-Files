/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package com.atlassian.crowd.model.application;

import com.google.common.collect.ImmutableList;
import java.util.List;

public enum ApplicationType {
    CROWD("Crowd"),
    GENERIC_APPLICATION("Generic Application"),
    PLUGIN("Plugin"),
    JIRA("Jira"),
    CONFLUENCE("Confluence"),
    BAMBOO("Bamboo"),
    FISHEYE("Fisheye"),
    CRUCIBLE("Crucible"),
    STASH("Bitbucket Server");

    private final String displayName;

    private ApplicationType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public static List<ApplicationType> getCreatableAppTypes() {
        return ImmutableList.of((Object)((Object)JIRA), (Object)((Object)CONFLUENCE), (Object)((Object)BAMBOO), (Object)((Object)FISHEYE), (Object)((Object)CRUCIBLE), (Object)((Object)STASH), (Object)((Object)GENERIC_APPLICATION));
    }
}

