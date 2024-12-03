/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.entity;

import com.atlassian.migration.agent.entity.StatsType;
import java.io.Serializable;
import java.util.Objects;

public class StatsKey
implements Serializable {
    private StatsType type;
    private String name;

    public StatsKey(StatsType type, String name) {
        this.type = type;
        this.name = name;
    }

    private StatsKey() {
    }

    public StatsType getType() {
        return this.type;
    }

    public String getName() {
        return this.name;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        StatsKey statsKey = (StatsKey)o;
        return Objects.equals((Object)this.type, (Object)statsKey.type) && Objects.equals(this.name, statsKey.name);
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.type, this.name});
    }
}

