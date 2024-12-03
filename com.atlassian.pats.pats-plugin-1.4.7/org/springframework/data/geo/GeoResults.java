/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 *  org.springframework.util.ObjectUtils
 *  org.springframework.util.StringUtils
 */
package org.springframework.data.geo;

import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.Metric;
import org.springframework.data.geo.Metrics;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

public class GeoResults<T>
implements Iterable<GeoResult<T>>,
Serializable {
    private static final long serialVersionUID = 8347363491300219485L;
    private final List<? extends GeoResult<T>> results;
    private final Distance averageDistance;

    public GeoResults(List<? extends GeoResult<T>> results) {
        this(results, Metrics.NEUTRAL);
    }

    public GeoResults(List<? extends GeoResult<T>> results, Metric metric) {
        this(results, GeoResults.calculateAverageDistance(results, metric));
    }

    @PersistenceConstructor
    public GeoResults(List<? extends GeoResult<T>> results, Distance averageDistance) {
        Assert.notNull(results, (String)"Results must not be null!");
        Assert.notNull((Object)averageDistance, (String)"Average Distance must not be null!");
        this.results = results;
        this.averageDistance = averageDistance;
    }

    public Distance getAverageDistance() {
        return this.averageDistance;
    }

    public List<GeoResult<T>> getContent() {
        return Collections.unmodifiableList(this.results);
    }

    @Override
    public Iterator<GeoResult<T>> iterator() {
        return this.results.iterator();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GeoResults)) {
            return false;
        }
        GeoResults that = (GeoResults)o;
        if (!ObjectUtils.nullSafeEquals(this.results, that.results)) {
            return false;
        }
        return ObjectUtils.nullSafeEquals((Object)this.averageDistance, (Object)that.averageDistance);
    }

    public int hashCode() {
        int result = ObjectUtils.nullSafeHashCode(this.results);
        result = 31 * result + ObjectUtils.nullSafeHashCode((Object)this.averageDistance);
        return result;
    }

    public String toString() {
        return String.format("GeoResults: [averageDistance: %s, results: %s]", this.averageDistance.toString(), StringUtils.collectionToCommaDelimitedString(this.results));
    }

    private static Distance calculateAverageDistance(List<? extends GeoResult<?>> results, Metric metric) {
        Assert.notNull(results, (String)"Results must not be null!");
        Assert.notNull((Object)metric, (String)"Metric must not be null!");
        if (results.isEmpty()) {
            return new Distance(0.0, metric);
        }
        double averageDistance = results.stream().mapToDouble(it -> it.getDistance().getValue()).average().orElse(0.0);
        return new Distance(averageDistance, metric);
    }
}

