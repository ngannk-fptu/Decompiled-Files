/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.domain.Product
 *  lombok.Generated
 */
package com.atlassian.migration.agent.service.user.request.v2;

import com.atlassian.cmpt.domain.Product;
import com.atlassian.migration.agent.service.user.GroupPermission;
import java.util.Collection;
import lombok.Generated;

public class GroupProductPermission {
    private final Product product;
    private final Collection<GroupPermission> permissions;

    @Generated
    public GroupProductPermission(Product product, Collection<GroupPermission> permissions) {
        this.product = product;
        this.permissions = permissions;
    }

    @Generated
    public Product getProduct() {
        return this.product;
    }

    @Generated
    public Collection<GroupPermission> getPermissions() {
        return this.permissions;
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof GroupProductPermission)) {
            return false;
        }
        GroupProductPermission other = (GroupProductPermission)o;
        if (!other.canEqual(this)) {
            return false;
        }
        Product this$product = this.getProduct();
        Product other$product = other.getProduct();
        if (this$product == null ? other$product != null : !this$product.equals(other$product)) {
            return false;
        }
        Collection<GroupPermission> this$permissions = this.getPermissions();
        Collection<GroupPermission> other$permissions = other.getPermissions();
        return !(this$permissions == null ? other$permissions != null : !((Object)this$permissions).equals(other$permissions));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof GroupProductPermission;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Product $product = this.getProduct();
        result = result * 59 + ($product == null ? 43 : $product.hashCode());
        Collection<GroupPermission> $permissions = this.getPermissions();
        result = result * 59 + ($permissions == null ? 43 : ((Object)$permissions).hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "GroupProductPermission(product=" + this.getProduct() + ", permissions=" + this.getPermissions() + ")";
    }
}

