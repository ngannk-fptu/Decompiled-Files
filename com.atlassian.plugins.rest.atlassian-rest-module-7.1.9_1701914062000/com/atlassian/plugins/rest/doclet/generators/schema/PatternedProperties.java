/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  com.google.common.base.Preconditions
 */
package com.atlassian.plugins.rest.doclet.generators.schema;

import com.atlassian.plugins.rest.doclet.generators.schema.ModelClass;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import java.util.Objects;

public class PatternedProperties {
    private final String pattern;
    private final ModelClass valuesType;

    public PatternedProperties(String pattern, ModelClass valuesType) {
        this.pattern = (String)Preconditions.checkNotNull((Object)pattern);
        this.valuesType = (ModelClass)Preconditions.checkNotNull((Object)valuesType);
    }

    public String getPattern() {
        return this.pattern;
    }

    public ModelClass getValuesType() {
        return this.valuesType;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        PatternedProperties that = (PatternedProperties)o;
        return Objects.equals(this.pattern, that.pattern) && Objects.equals(this.valuesType, that.valuesType);
    }

    public int hashCode() {
        return Objects.hash(this.pattern, this.valuesType);
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("pattern", (Object)this.pattern).add("valuesType", (Object)this.valuesType).toString();
    }
}

