/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.app;

import com.atlassian.migration.app.Container;
import com.atlassian.migration.app.SiteSelection;
import java.util.Set;

@Deprecated
public class SiteContainer
extends Container {
    private static final String CONTAINER_TYPE = "Site";
    private final Set<SiteSelection> selections;

    public SiteContainer(Set<SiteSelection> selections) {
        this.selections = selections;
    }

    public Set<SiteSelection> getSelections() {
        return this.selections;
    }

    @Override
    public String getType() {
        return CONTAINER_TYPE;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        SiteContainer that = (SiteContainer)o;
        return this.selections != null ? this.selections.equals(that.selections) : that.selections == null;
    }

    public int hashCode() {
        return this.selections != null ? this.selections.hashCode() : 0;
    }
}

