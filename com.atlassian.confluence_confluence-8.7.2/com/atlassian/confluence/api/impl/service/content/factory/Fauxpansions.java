/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansions
 *  com.google.common.base.MoreObjects
 */
package com.atlassian.confluence.api.impl.service.content.factory;

import com.atlassian.confluence.api.model.Expansions;
import com.google.common.base.MoreObjects;

public class Fauxpansions {
    private final String propertyName;
    private final boolean canExpand;
    private final Expansions subExpansions;

    private Fauxpansions(String propertyName, boolean canExpand, Expansions subExpansions) {
        this.propertyName = propertyName;
        this.canExpand = canExpand;
        this.subExpansions = subExpansions;
    }

    public static Fauxpansions fauxpansions(Expansions expansions, String propertyName) {
        boolean canExpand = expansions.canExpand(propertyName);
        Expansions subExpansions = expansions.getSubExpansions(propertyName);
        return new Fauxpansions(propertyName, canExpand, subExpansions);
    }

    public boolean canExpand() {
        return this.canExpand;
    }

    public Expansions getSubExpansions() {
        return this.subExpansions;
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("propertyName", (Object)this.propertyName).add("canExpand", this.canExpand).add("subExpansions", (Object)this.subExpansions).toString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Fauxpansions otherObject = (Fauxpansions)o;
        return otherObject.canExpand == this.canExpand && otherObject.propertyName.equals(this.propertyName) && otherObject.subExpansions.equals((Object)this.subExpansions);
    }

    public int hashCode() {
        return (this.propertyName + this.canExpand + this.subExpansions).hashCode();
    }
}

