/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 *  org.springframework.util.ObjectUtils
 */
package org.springframework.data.geo;

import java.io.Serializable;
import org.springframework.data.geo.Distance;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

public final class GeoResult<T>
implements Serializable {
    private static final long serialVersionUID = 1637452570977581370L;
    private final T content;
    private final Distance distance;

    public GeoResult(T content, Distance distance) {
        Assert.notNull(content, (String)"Content must not be null");
        Assert.notNull((Object)distance, (String)"Distance must not be null");
        this.content = content;
        this.distance = distance;
    }

    public T getContent() {
        return this.content;
    }

    public Distance getDistance() {
        return this.distance;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GeoResult)) {
            return false;
        }
        GeoResult geoResult = (GeoResult)o;
        if (!ObjectUtils.nullSafeEquals(this.content, geoResult.content)) {
            return false;
        }
        return ObjectUtils.nullSafeEquals((Object)this.distance, (Object)geoResult.distance);
    }

    public int hashCode() {
        int result = ObjectUtils.nullSafeHashCode(this.content);
        result = 31 * result + ObjectUtils.nullSafeHashCode((Object)this.distance);
        return result;
    }

    public String toString() {
        return String.format("GeoResult [content: %s, distance: %s, ]", this.content.toString(), this.distance.toString());
    }
}

