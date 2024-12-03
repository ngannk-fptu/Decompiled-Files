/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package com.atlassian.oauth2.scopes.api;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Objects;

public class ScopeDescription {
    private final List<String> descriptions;

    public ScopeDescription(List<String> descriptions) {
        this.descriptions = descriptions;
    }

    public ScopeDescription(String descriptions) {
        this.descriptions = ImmutableList.of((Object)descriptions);
    }

    public List<String> getDescriptions() {
        return this.descriptions;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ScopeDescription that = (ScopeDescription)o;
        return this.descriptions.equals(that.descriptions);
    }

    public int hashCode() {
        return Objects.hash(this.descriptions);
    }
}

