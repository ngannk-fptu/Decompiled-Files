/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.atlassian.migration.agent.service.user.request.v2;

import com.atlassian.migration.agent.service.user.request.v2.GroupProductPermission;
import java.util.Collection;
import lombok.Generated;

public class MigrationGroupV2Dto {
    private final String name;
    private final Collection<GroupProductPermission> products;

    @Generated
    public MigrationGroupV2Dto(String name, Collection<GroupProductPermission> products) {
        this.name = name;
        this.products = products;
    }

    @Generated
    public String getName() {
        return this.name;
    }

    @Generated
    public Collection<GroupProductPermission> getProducts() {
        return this.products;
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof MigrationGroupV2Dto)) {
            return false;
        }
        MigrationGroupV2Dto other = (MigrationGroupV2Dto)o;
        if (!other.canEqual(this)) {
            return false;
        }
        String this$name = this.getName();
        String other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) {
            return false;
        }
        Collection<GroupProductPermission> this$products = this.getProducts();
        Collection<GroupProductPermission> other$products = other.getProducts();
        return !(this$products == null ? other$products != null : !((Object)this$products).equals(other$products));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof MigrationGroupV2Dto;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $name = this.getName();
        result = result * 59 + ($name == null ? 43 : $name.hashCode());
        Collection<GroupProductPermission> $products = this.getProducts();
        result = result * 59 + ($products == null ? 43 : ((Object)$products).hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "MigrationGroupV2Dto(name=" + this.getName() + ", products=" + this.getProducts() + ")";
    }
}

