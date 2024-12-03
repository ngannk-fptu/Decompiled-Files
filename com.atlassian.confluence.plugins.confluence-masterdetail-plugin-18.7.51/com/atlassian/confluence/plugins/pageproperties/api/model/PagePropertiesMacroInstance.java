/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Lists
 */
package com.atlassian.confluence.plugins.pageproperties.api.model;

import com.atlassian.confluence.plugins.pageproperties.api.model.PageProperty;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;

public class PagePropertiesMacroInstance {
    private final Map<String, PageProperty> pagePropertyReportRow;

    public PagePropertiesMacroInstance(Map<String, PageProperty> pagePropertyReportRow) {
        this.pagePropertyReportRow = ImmutableMap.copyOf(pagePropertyReportRow);
    }

    public Map<String, PageProperty> getPagePropertyReportRow() {
        return this.pagePropertyReportRow;
    }

    public List<PageProperty> getAllPagePropertyReportValues() {
        return Lists.newArrayList(this.pagePropertyReportRow.values());
    }
}

