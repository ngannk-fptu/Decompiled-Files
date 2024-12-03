/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bonnie.Searchable
 */
package com.atlassian.confluence.plugins.confluence_kb_space_blueprint.services.impl;

import com.atlassian.bonnie.Searchable;
import java.util.Collection;

class SearchableWithId
implements Searchable {
    private final long id;

    SearchableWithId(long id) {
        this.id = id;
    }

    public long getId() {
        return this.id;
    }

    public Collection getSearchableDependants() {
        return null;
    }

    public boolean isIndexable() {
        return false;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        SearchableWithId that = (SearchableWithId)o;
        return this.id == that.id;
    }

    public int hashCode() {
        return (int)(this.id ^ this.id >>> 32);
    }
}

