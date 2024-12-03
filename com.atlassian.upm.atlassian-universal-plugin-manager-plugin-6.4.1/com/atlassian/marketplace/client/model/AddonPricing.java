/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  io.atlassian.fugue.Option
 *  org.joda.time.DateTime
 */
package com.atlassian.marketplace.client.model;

import com.atlassian.marketplace.client.model.AddonPricingItem;
import com.atlassian.marketplace.client.model.Links;
import com.atlassian.marketplace.client.model.ReadOnly;
import com.google.common.collect.ImmutableList;
import io.atlassian.fugue.Option;
import java.util.function.Function;
import org.joda.time.DateTime;

public class AddonPricing {
    @ReadOnly
    Links _links;
    ImmutableList<AddonPricingItem> items;
    Option<ImmutableList<AddonPricingItem>> perUnitItems;
    Boolean expertDiscountOptOut;
    Boolean contactSalesForAdditionalPricing;
    Option<String> parent;
    @ReadOnly
    Option<DateTime> lastModified;
    Option<RoleInfo> role;

    public Links getLinks() {
        return this._links;
    }

    public Iterable<AddonPricingItem> getItems() {
        return this.items;
    }

    public Iterable<AddonPricingItem> getPerUnitItems() {
        return (Iterable)this.perUnitItems.getOrElse((Object)ImmutableList.of());
    }

    public Option<Iterable<AddonPricingItem>> getPerUnitItemsIfSpecified() {
        return this.perUnitItems.map(Function.identity());
    }

    public boolean isExpertDiscountOptOut() {
        return this.expertDiscountOptOut;
    }

    public boolean isContactSalesForAdditionalPricing() {
        return this.contactSalesForAdditionalPricing;
    }

    public Option<String> getParent() {
        return this.parent;
    }

    public Option<DateTime> getLastModified() {
        return this.lastModified;
    }

    public boolean isRoleBased() {
        return this.getRoleInfo().isDefined();
    }

    public Option<RoleInfo> getRoleInfo() {
        return this.role;
    }

    public static class RoleInfo {
        String singularName;
        String pluralName;

        public String getSingularName() {
            return this.singularName;
        }

        public String getPluralName() {
            return this.pluralName;
        }
    }
}

