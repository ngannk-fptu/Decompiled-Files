/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.service.check.csv;

import com.atlassian.migration.agent.service.check.csv.CheckResultCSVContainer;
import com.atlassian.migration.agent.service.check.csv.SpaceAnonymousCSVBean;
import java.util.Collection;
import java.util.Collections;

public class SpaceAnonymousCSVContainer
implements CheckResultCSVContainer<SpaceAnonymousCSVBean> {
    private final Collection<SpaceAnonymousCSVBean> beans;

    public SpaceAnonymousCSVContainer() {
        this.beans = Collections.emptyList();
    }

    public SpaceAnonymousCSVContainer(Collection<SpaceAnonymousCSVBean> beans) {
        this.beans = beans;
    }

    @Override
    public Collection<SpaceAnonymousCSVBean> beans() {
        return this.beans;
    }

    @Override
    public String[] headers() {
        return new String[]{"Space Name"};
    }

    @Override
    public String[] fieldMappings() {
        return new String[]{"spaceName"};
    }
}

