/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.ObjectUtils
 */
package org.springframework.data.geo;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.GeoResults;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;

public class GeoPage<T>
extends PageImpl<GeoResult<T>> {
    private static final long serialVersionUID = -5655267379242128600L;
    private final Distance averageDistance;

    public GeoPage(GeoResults<T> results) {
        super(results.getContent());
        this.averageDistance = results.getAverageDistance();
    }

    public GeoPage(GeoResults<T> results, Pageable pageable, long total) {
        super(results.getContent(), pageable, total);
        this.averageDistance = results.getAverageDistance();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof GeoPage)) {
            return false;
        }
        GeoPage that = (GeoPage)obj;
        return super.equals(obj) && ObjectUtils.nullSafeEquals((Object)this.averageDistance, (Object)that.averageDistance);
    }

    @Override
    public int hashCode() {
        return super.hashCode() + ObjectUtils.nullSafeHashCode((Object)this.averageDistance);
    }

    public Distance getAverageDistance() {
        return this.averageDistance;
    }
}

