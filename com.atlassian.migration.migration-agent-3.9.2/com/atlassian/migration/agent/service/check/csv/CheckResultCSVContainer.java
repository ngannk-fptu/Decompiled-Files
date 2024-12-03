/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.service.check.csv;

import com.atlassian.migration.agent.service.check.csv.AbstractCheckResultCSVBean;
import java.util.Collection;

public interface CheckResultCSVContainer<T extends AbstractCheckResultCSVBean> {
    public String[] headers();

    public String[] fieldMappings();

    public Collection<T> beans();
}

