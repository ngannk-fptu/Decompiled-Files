/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.service.check.csv;

import com.atlassian.migration.agent.service.check.csv.AbstractCheckResultCSVBean;

public class SpaceAnonymousCSVBean
implements AbstractCheckResultCSVBean {
    private String spaceName;

    public SpaceAnonymousCSVBean(String spaceName) {
        this.spaceName = spaceName;
    }

    public String getSpaceName() {
        return this.spaceName;
    }

    public void setSpaceName(String spaceName) {
        this.spaceName = spaceName;
    }
}

