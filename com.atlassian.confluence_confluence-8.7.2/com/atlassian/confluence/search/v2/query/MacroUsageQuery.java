/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package com.atlassian.confluence.search.v2.query;

import com.atlassian.confluence.search.v2.SearchFieldNames;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.query.ConstantScoreQuery;
import com.atlassian.confluence.search.v2.query.TermQuery;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Objects;

public class MacroUsageQuery
implements SearchQuery {
    private static final String KEY = "macroUsage";
    private final String macroName;

    public MacroUsageQuery(String macroName) {
        this.macroName = macroName;
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public List<String> getParameters() {
        return ImmutableList.of((Object)this.macroName);
    }

    public String getMacroName() {
        return this.macroName;
    }

    @Override
    public SearchQuery expand() {
        return new ConstantScoreQuery(new TermQuery(SearchFieldNames.MACRO_NAME, this.getMacroName()));
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + (this.macroName == null ? 0 : this.macroName.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        MacroUsageQuery other = (MacroUsageQuery)obj;
        return Objects.equals(this.macroName, other.macroName);
    }
}

