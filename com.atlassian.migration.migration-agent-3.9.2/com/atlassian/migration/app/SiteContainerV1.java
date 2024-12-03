/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.app;

import com.atlassian.migration.app.ContainerType;
import com.atlassian.migration.app.ContainerV1;
import com.atlassian.migration.app.SiteSelection;
import java.util.Set;

public class SiteContainerV1
extends ContainerV1 {
    private final Set<SiteSelection> selections;

    public SiteContainerV1(Set<SiteSelection> selections) {
        this.selections = selections;
    }

    public Set<SiteSelection> getSelections() {
        return this.selections;
    }

    @Override
    public ContainerType getType() {
        return ContainerType.Site;
    }
}

