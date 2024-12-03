/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.google.common.base.Function
 */
package com.atlassian.confluence.api.model;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.Expansions;
import com.google.common.base.Function;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

@ExperimentalApi
public class Expansion {
    private final String propertyName;
    private final Expansions subExpansions;
    @Deprecated
    public static final Function<String, Expansion> AS_EXPANSION = Expansion::new;

    public Expansion(String propertyName) {
        this(propertyName, Expansions.EMPTY);
    }

    public Expansion(String propertyName, Expansions subExpansions) {
        this.propertyName = propertyName;
        this.subExpansions = subExpansions;
    }

    public String getPropertyName() {
        return this.propertyName;
    }

    public Expansions getSubExpansions() {
        return this.subExpansions;
    }

    public static Expansion combine(Object ... expansionParts) {
        String joinedString = Arrays.stream(expansionParts).map(Object::toString).collect(Collectors.joining("."));
        return new Expansion(joinedString);
    }

    public String toString() {
        return "Expansion{propertyName='" + this.propertyName + '\'' + ", subExpansions=" + this.subExpansions + '}';
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Expansion expansion = (Expansion)o;
        if (this.propertyName != null ? !this.propertyName.equals(expansion.propertyName) : expansion.propertyName != null) {
            return false;
        }
        return !(this.subExpansions != null ? !this.subExpansions.equals(expansion.subExpansions) : expansion.subExpansions != null);
    }

    public int hashCode() {
        return Objects.hash(this.propertyName, this.subExpansions);
    }
}

