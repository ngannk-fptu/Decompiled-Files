/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableMap
 *  javax.annotation.concurrent.Immutable
 *  org.apache.commons.lang3.builder.EqualsBuilder
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.gadgets;

import com.atlassian.gadgets.DashboardItemState;
import com.atlassian.gadgets.DashboardItemStateVisitor;
import com.atlassian.gadgets.GadgetId;
import com.atlassian.gadgets.LocalDashboardItemModuleId;
import com.atlassian.gadgets.dashboard.Color;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import java.util.Collections;
import java.util.Map;
import javax.annotation.concurrent.Immutable;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

@Immutable
public final class LocalDashboardItemState
implements DashboardItemState {
    private final GadgetId gadgetId;
    private final Color color;
    private final LocalDashboardItemModuleId dashboardItemModuleId;
    private final Map<String, String> properties;

    public LocalDashboardItemState(GadgetId gadgetId, Color color, LocalDashboardItemModuleId dashboardItemModuleId, Map<String, String> properties) {
        this.gadgetId = (GadgetId)Preconditions.checkNotNull((Object)gadgetId);
        this.color = (Color)((Object)Preconditions.checkNotNull((Object)((Object)color)));
        this.dashboardItemModuleId = dashboardItemModuleId;
        this.properties = ImmutableMap.copyOf(properties);
    }

    @Override
    public GadgetId getId() {
        return this.gadgetId;
    }

    @Override
    public Color getColor() {
        return this.color;
    }

    public Map<String, String> getProperties() {
        return this.properties;
    }

    public LocalDashboardItemModuleId getDashboardItemModuleId() {
        return this.dashboardItemModuleId;
    }

    @Override
    public <V> V accept(DashboardItemStateVisitor<V> visitor) {
        return visitor.visit(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(LocalDashboardItemState dashboardItemState) {
        return new Builder().color(dashboardItemState.color).gadgetId(dashboardItemState.gadgetId).dashboardItemModuleId(dashboardItemState.dashboardItemModuleId).properties(dashboardItemState.getProperties());
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LocalDashboardItemState)) {
            return false;
        }
        LocalDashboardItemState rhs = (LocalDashboardItemState)o;
        return new EqualsBuilder().append((Object)this.getId(), (Object)rhs.getId()).append((Object)this.getDashboardItemModuleId(), (Object)rhs.getDashboardItemModuleId()).append((Object)this.getColor(), (Object)rhs.getColor()).isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder().append((Object)this.getId()).append((Object)this.getDashboardItemModuleId()).append((Object)this.getColor()).toHashCode();
    }

    public String toString() {
        return new ToStringBuilder((Object)this).append("id", (Object)this.getId()).append("dashboard-item-module-id", (Object)this.getDashboardItemModuleId()).append("color", (Object)this.getColor()).toString();
    }

    public static class Builder {
        private GadgetId gadgetId;
        private Color color = Color.defaultColor();
        private LocalDashboardItemModuleId dashboardItemModuleId;
        private Map<String, String> properties = Collections.emptyMap();

        public Builder gadgetId(GadgetId gadgetId) {
            this.gadgetId = gadgetId;
            return this;
        }

        public Builder color(Color color) {
            this.color = color;
            return this;
        }

        public Builder dashboardItemModuleId(LocalDashboardItemModuleId dashboardItemModuleId) {
            this.dashboardItemModuleId = dashboardItemModuleId;
            return this;
        }

        public Builder properties(Map<String, String> properties) {
            this.properties = properties;
            return this;
        }

        public LocalDashboardItemState build() {
            return new LocalDashboardItemState(this.gadgetId, this.color, this.dashboardItemModuleId, this.properties);
        }
    }
}

