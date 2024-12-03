/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.annotation.concurrent.Immutable
 */
package com.atlassian.gadgets;

import com.atlassian.gadgets.DashboardItemModuleId;
import com.atlassian.gadgets.DashboardItemModuleIdVisitor;
import com.atlassian.gadgets.DashboardItemType;
import com.google.common.base.Preconditions;
import java.net.URI;
import javax.annotation.concurrent.Immutable;

@Immutable
public class OpenSocialDashboardItemModuleId
implements DashboardItemModuleId {
    private final URI id;

    public OpenSocialDashboardItemModuleId(URI id) {
        this.id = (URI)Preconditions.checkNotNull((Object)id);
    }

    @Override
    public String getId() {
        return this.id.toASCIIString();
    }

    public URI getSpecUri() {
        return this.id;
    }

    @Override
    public DashboardItemType getType() {
        return DashboardItemType.OPEN_SOCIAL_GADGET;
    }

    @Override
    public <T> T accept(DashboardItemModuleIdVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        OpenSocialDashboardItemModuleId that = (OpenSocialDashboardItemModuleId)o;
        return !(this.id != null ? !this.id.equals(that.id) : that.id != null);
    }

    public int hashCode() {
        return this.id != null ? this.id.hashCode() : 0;
    }
}

