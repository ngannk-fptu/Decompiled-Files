/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.search.v2.CustomSearchIndexRegistry
 *  com.atlassian.confluence.search.v2.DelegatingSearchIndexAccessor
 *  com.atlassian.confluence.search.v2.ScoringStrategy
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.tasklist.report.searchindex;

import com.atlassian.confluence.search.v2.CustomSearchIndexRegistry;
import com.atlassian.confluence.search.v2.DelegatingSearchIndexAccessor;
import com.atlassian.confluence.search.v2.ScoringStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InlineTaskSearchIndexAccessor
extends DelegatingSearchIndexAccessor {
    public static final String INDEX_NAME = "task_report_index";

    @Autowired
    public InlineTaskSearchIndexAccessor(CustomSearchIndexRegistry customSearchIndexRegistry) {
        super(customSearchIndexRegistry, INDEX_NAME, INDEX_NAME, ScoringStrategy.DEFAULT, null);
    }
}

