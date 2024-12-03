/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.builder.EqualsBuilder
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 */
package com.atlassian.troubleshooting.preupgrade.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class SupportedPlatformQuery {
    private final String product;
    private final String version;
    private final boolean enterpriseRecommended;

    public SupportedPlatformQuery(String product, String version, boolean enterpriseRecommended) {
        this.product = product;
        this.version = version;
        this.enterpriseRecommended = enterpriseRecommended;
    }

    public String getProduct() {
        return this.product;
    }

    public String getVersion() {
        return this.version;
    }

    public boolean isEnterpriseRecommended() {
        return this.enterpriseRecommended;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        SupportedPlatformQuery that = (SupportedPlatformQuery)o;
        return new EqualsBuilder().append(this.enterpriseRecommended, that.enterpriseRecommended).append((Object)this.product, (Object)that.product).append((Object)this.version, (Object)that.version).isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder(17, 37).append((Object)this.product).append((Object)this.version).append(this.enterpriseRecommended).toHashCode();
    }
}

