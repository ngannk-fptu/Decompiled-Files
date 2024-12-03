/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.atlassian.migration.agent.service.catalogue.model;

import com.atlassian.migration.agent.service.catalogue.model.AbstractContainer;
import java.util.Set;
import lombok.Generated;

public class SiteContainer
extends AbstractContainer {
    private final Set<SiteSelection> selections;

    public SiteContainer(Set<SiteSelection> selections) {
        super(AbstractContainer.Type.Site);
        this.selections = selections;
    }

    @Generated
    public Set<SiteSelection> getSelections() {
        return this.selections;
    }

    public static enum SiteSelection {
        USERS,
        GLOBAL_ENTITIES;

    }
}

