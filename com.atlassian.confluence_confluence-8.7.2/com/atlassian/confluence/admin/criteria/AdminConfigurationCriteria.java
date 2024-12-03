/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.admin.criteria;

public interface AdminConfigurationCriteria {
    public boolean isMet();

    public boolean hasValue();

    public String getValue();

    public boolean hasLiveValue();

    public boolean getIgnored();

    public void setIgnored(boolean var1);
}

