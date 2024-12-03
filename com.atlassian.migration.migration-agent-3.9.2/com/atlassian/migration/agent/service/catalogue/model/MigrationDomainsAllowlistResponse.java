/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.atlassian.migration.agent.service.catalogue.model;

import java.io.Serializable;
import java.util.List;
import lombok.Generated;

public class MigrationDomainsAllowlistResponse {
    private String product;
    private List<Entry> configs;

    @Generated
    public String getProduct() {
        return this.product;
    }

    @Generated
    public List<Entry> getConfigs() {
        return this.configs;
    }

    @Generated
    public void setProduct(String product) {
        this.product = product;
    }

    @Generated
    public void setConfigs(List<Entry> configs) {
        this.configs = configs;
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof MigrationDomainsAllowlistResponse)) {
            return false;
        }
        MigrationDomainsAllowlistResponse other = (MigrationDomainsAllowlistResponse)o;
        if (!other.canEqual(this)) {
            return false;
        }
        String this$product = this.getProduct();
        String other$product = other.getProduct();
        if (this$product == null ? other$product != null : !this$product.equals(other$product)) {
            return false;
        }
        List<Entry> this$configs = this.getConfigs();
        List<Entry> other$configs = other.getConfigs();
        return !(this$configs == null ? other$configs != null : !((Object)this$configs).equals(other$configs));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof MigrationDomainsAllowlistResponse;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $product = this.getProduct();
        result = result * 59 + ($product == null ? 43 : $product.hashCode());
        List<Entry> $configs = this.getConfigs();
        result = result * 59 + ($configs == null ? 43 : ((Object)$configs).hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "MigrationDomainsAllowlistResponse(product=" + this.getProduct() + ", configs=" + this.getConfigs() + ")";
    }

    @Generated
    public MigrationDomainsAllowlistResponse() {
    }

    @Generated
    public MigrationDomainsAllowlistResponse(String product, List<Entry> configs) {
        this.product = product;
        this.configs = configs;
    }

    public static class UrlEntry
    implements Entry {
        private String name;
        private List<String> urls;

        @Override
        public String getType() {
            return "default";
        }

        @Override
        @Generated
        public String getName() {
            return this.name;
        }

        @Generated
        public List<String> getUrls() {
            return this.urls;
        }

        @Generated
        public void setName(String name) {
            this.name = name;
        }

        @Generated
        public void setUrls(List<String> urls) {
            this.urls = urls;
        }

        @Generated
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof UrlEntry)) {
                return false;
            }
            UrlEntry other = (UrlEntry)o;
            if (!other.canEqual(this)) {
                return false;
            }
            String this$name = this.getName();
            String other$name = other.getName();
            if (this$name == null ? other$name != null : !this$name.equals(other$name)) {
                return false;
            }
            List<String> this$urls = this.getUrls();
            List<String> other$urls = other.getUrls();
            return !(this$urls == null ? other$urls != null : !((Object)this$urls).equals(other$urls));
        }

        @Generated
        protected boolean canEqual(Object other) {
            return other instanceof UrlEntry;
        }

        @Generated
        public int hashCode() {
            int PRIME = 59;
            int result = 1;
            String $name = this.getName();
            result = result * 59 + ($name == null ? 43 : $name.hashCode());
            List<String> $urls = this.getUrls();
            result = result * 59 + ($urls == null ? 43 : ((Object)$urls).hashCode());
            return result;
        }

        @Generated
        public String toString() {
            return "MigrationDomainsAllowlistResponse.UrlEntry(name=" + this.getName() + ", urls=" + this.getUrls() + ")";
        }

        @Generated
        public UrlEntry() {
        }

        @Generated
        public UrlEntry(String name, List<String> urls) {
            this.name = name;
            this.urls = urls;
        }
    }

    public static interface Entry
    extends Serializable {
        public String getType();

        public String getName();
    }
}

