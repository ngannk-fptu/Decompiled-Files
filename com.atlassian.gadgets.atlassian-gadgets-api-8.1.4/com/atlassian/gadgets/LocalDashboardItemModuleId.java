/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleCompleteKey
 *  io.atlassian.fugue.Option
 *  org.apache.commons.lang3.builder.EqualsBuilder
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 */
package com.atlassian.gadgets;

import com.atlassian.gadgets.DashboardItemModuleId;
import com.atlassian.gadgets.DashboardItemModuleIdVisitor;
import com.atlassian.gadgets.DashboardItemType;
import com.atlassian.gadgets.OpenSocialDashboardItemModuleId;
import com.atlassian.plugin.ModuleCompleteKey;
import io.atlassian.fugue.Option;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class LocalDashboardItemModuleId
implements DashboardItemModuleId {
    private final ModuleCompleteKey fullModuleKey;
    private final Option<OpenSocialDashboardItemModuleId> replacedGadgetId;

    public LocalDashboardItemModuleId(ModuleCompleteKey fullModuleKey) {
        this(fullModuleKey, (Option<OpenSocialDashboardItemModuleId>)Option.none());
    }

    public LocalDashboardItemModuleId(ModuleCompleteKey fullModuleKey, Option<OpenSocialDashboardItemModuleId> replacedGadgetId) {
        this.fullModuleKey = fullModuleKey;
        this.replacedGadgetId = replacedGadgetId;
    }

    @Override
    public String getId() {
        return this.fullModuleKey.getCompleteKey();
    }

    @Override
    public DashboardItemType getType() {
        return DashboardItemType.LOCAL_DASHBOARD_ITEM;
    }

    public ModuleCompleteKey getFullModuleKey() {
        return this.fullModuleKey;
    }

    public Option<OpenSocialDashboardItemModuleId> getReplacedGadgetId() {
        return this.replacedGadgetId;
    }

    @Override
    public <T> T accept(DashboardItemModuleIdVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LocalDashboardItemModuleId)) {
            return false;
        }
        LocalDashboardItemModuleId rhs = (LocalDashboardItemModuleId)o;
        return new EqualsBuilder().append((Object)this.getId(), (Object)rhs.getId()).append(this.getReplacedGadgetId(), rhs.getReplacedGadgetId()).isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder().append((Object)this.getId()).append((Object)this.getFullModuleKey()).append(this.getReplacedGadgetId()).toHashCode();
    }
}

