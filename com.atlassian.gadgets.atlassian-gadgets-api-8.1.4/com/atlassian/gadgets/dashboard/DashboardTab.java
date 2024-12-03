/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.builder.EqualsBuilder
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.gadgets.dashboard;

import com.atlassian.gadgets.dashboard.DashboardId;
import java.net.URI;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public final class DashboardTab {
    private final DashboardId dashboardId;
    private final String title;
    private final URI tabUri;

    public DashboardTab(DashboardId dashboardId, String title, URI tabUri) {
        this.dashboardId = dashboardId;
        this.title = title;
        this.tabUri = tabUri;
    }

    public DashboardId getDashboardId() {
        return this.dashboardId;
    }

    public URI getTabUri() {
        return this.tabUri;
    }

    public String getTitle() {
        return this.title;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DashboardTab)) {
            return false;
        }
        DashboardTab rhs = (DashboardTab)o;
        return new EqualsBuilder().append((Object)this.getDashboardId(), (Object)rhs.getDashboardId()).append((Object)this.getTitle(), (Object)rhs.getTitle()).append((Object)this.getTabUri(), (Object)rhs.getTabUri()).isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder().append((Object)this.getDashboardId()).append((Object)this.getTitle()).append((Object)this.getTabUri()).toHashCode();
    }

    public String toString() {
        return new ToStringBuilder((Object)this).append("dashboardid", (Object)this.getDashboardId()).append("title", (Object)this.getTitle()).append("tabUri", (Object)this.getTabUri()).toString();
    }
}

