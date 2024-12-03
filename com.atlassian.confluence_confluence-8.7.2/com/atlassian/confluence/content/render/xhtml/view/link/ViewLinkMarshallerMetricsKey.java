/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.atlassian.confluence.content.render.xhtml.view.link;

import com.atlassian.confluence.impl.content.render.xhtml.analytics.MarshallerMetricsAccumulationKey;
import com.google.common.base.Preconditions;

public class ViewLinkMarshallerMetricsKey
implements MarshallerMetricsAccumulationKey {
    private final String linkType;

    ViewLinkMarshallerMetricsKey(String linkType) {
        this.linkType = (String)Preconditions.checkNotNull((Object)linkType);
    }

    @Override
    public String getAccumulationKeyAsString() {
        return this.linkType;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ViewLinkMarshallerMetricsKey that = (ViewLinkMarshallerMetricsKey)o;
        return this.linkType.equals(that.linkType);
    }

    public int hashCode() {
        return this.linkType.hashCode();
    }
}

