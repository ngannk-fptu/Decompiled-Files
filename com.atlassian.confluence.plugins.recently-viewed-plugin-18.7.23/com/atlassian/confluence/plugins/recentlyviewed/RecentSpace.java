/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.Space
 *  com.atlassian.confluence.spaces.Space
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 */
package com.atlassian.confluence.plugins.recentlyviewed;

import com.atlassian.confluence.api.model.content.Space;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(value=XmlAccessType.FIELD)
public class RecentSpace {
    private final long id;
    private final String key;
    private final String name;

    public RecentSpace(long id, String key, String name) {
        this.id = id;
        this.key = key;
        this.name = name;
    }

    public long getId() {
        return this.id;
    }

    public String getKey() {
        return this.key;
    }

    public String getName() {
        return this.name;
    }

    @Deprecated
    public static RecentSpace fromSpace(com.atlassian.confluence.spaces.Space space) {
        return new RecentSpace(space.getId(), space.getKey(), space.getName());
    }

    public static RecentSpace fromSpace(Space space) {
        return new RecentSpace(space.getId(), space.getKey(), space.getName());
    }
}

