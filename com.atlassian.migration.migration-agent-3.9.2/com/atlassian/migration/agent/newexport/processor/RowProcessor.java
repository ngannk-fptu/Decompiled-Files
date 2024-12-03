/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.newexport.processor;

import com.atlassian.migration.agent.newexport.Query;
import java.sql.ResultSet;

@FunctionalInterface
public interface RowProcessor {
    default public void initialise(ResultSet rs, Query query) {
    }

    public void process(ResultSet var1);
}

