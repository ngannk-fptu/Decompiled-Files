/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 */
package com.atlassian.confluence.search.v2.query;

import com.atlassian.confluence.search.v2.SearchQuery;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.util.List;

public class MacroStorageVersionQuery
implements SearchQuery {
    private static final String KEY = "macroStorageVersion";
    private final String macroName;
    private final int version;

    public MacroStorageVersionQuery(String macroName, int version) {
        Preconditions.checkNotNull((Object)macroName);
        this.macroName = macroName;
        this.version = version;
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public List<Object> getParameters() {
        return ImmutableList.of((Object)this.macroName, (Object)this.version);
    }

    public String getMacroName() {
        return this.macroName;
    }

    public int getVersion() {
        return this.version;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        MacroStorageVersionQuery that = (MacroStorageVersionQuery)o;
        if (this.version != that.version) {
            return false;
        }
        return this.macroName.equals(that.macroName);
    }

    public int hashCode() {
        int result = this.macroName.hashCode();
        result = 31 * result + this.version;
        return result;
    }
}

