/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.atlassian.migration.agent.model;

import lombok.Generated;

public class UserDomainRuleRecord {
    private String domain = "";
    private String decision = "";

    @Generated
    public String getDomain() {
        return this.domain;
    }

    @Generated
    public String getDecision() {
        return this.decision;
    }

    @Generated
    public void setDomain(String domain) {
        this.domain = domain;
    }

    @Generated
    public void setDecision(String decision) {
        this.decision = decision;
    }
}

