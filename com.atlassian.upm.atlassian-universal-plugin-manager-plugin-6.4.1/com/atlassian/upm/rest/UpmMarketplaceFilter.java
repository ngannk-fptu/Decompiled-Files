/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.rest;

import com.atlassian.marketplace.client.api.AddonQuery;
import com.atlassian.upm.api.util.Option;

public enum UpmMarketplaceFilter {
    FEATURED,
    HIGHEST_RATED,
    TOP_GROSSING,
    MOST_POPULAR,
    TRENDING,
    BY_ATLASSIAN,
    RECENTLY_UPDATED,
    TOP_VENDOR;


    public AddonQuery.View getMarketplaceView() {
        switch (this) {
            case FEATURED: {
                return AddonQuery.View.FEATURED;
            }
            case HIGHEST_RATED: {
                return AddonQuery.View.HIGHEST_RATED;
            }
            case TOP_GROSSING: {
                return AddonQuery.View.TOP_GROSSING;
            }
            case MOST_POPULAR: {
                return AddonQuery.View.POPULAR;
            }
            case TRENDING: {
                return AddonQuery.View.TRENDING;
            }
            case BY_ATLASSIAN: {
                return AddonQuery.View.BY_ATLASSIAN;
            }
            case TOP_VENDOR: {
                return AddonQuery.View.TOP_VENDOR;
            }
        }
        return AddonQuery.View.RECENTLY_UPDATED;
    }

    public String getKey() {
        switch (this) {
            case FEATURED: {
                return "featured";
            }
            case HIGHEST_RATED: {
                return "highest-rated";
            }
            case TOP_GROSSING: {
                return "top-grossing";
            }
            case MOST_POPULAR: {
                return "popular";
            }
            case TRENDING: {
                return "trending";
            }
            case BY_ATLASSIAN: {
                return "atlassian";
            }
            case TOP_VENDOR: {
                return "top-vendor";
            }
        }
        return "recent";
    }

    public static Option<UpmMarketplaceFilter> fromKey(String key) {
        for (UpmMarketplaceFilter f : UpmMarketplaceFilter.values()) {
            if (!f.getKey().equalsIgnoreCase(key)) continue;
            return Option.some(f);
        }
        return Option.none();
    }

    public String getLinkRel() {
        switch (this) {
            case FEATURED: {
                return "featured";
            }
            case HIGHEST_RATED: {
                return "highest-rated";
            }
            case TOP_GROSSING: {
                return "top-grossing";
            }
            case MOST_POPULAR: {
                return "popular";
            }
            case TRENDING: {
                return "trending";
            }
            case BY_ATLASSIAN: {
                return "atlassian";
            }
            case TOP_VENDOR: {
                return "top-vendor";
            }
        }
        return "available";
    }
}

