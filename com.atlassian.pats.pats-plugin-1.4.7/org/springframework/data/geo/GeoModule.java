/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonIgnore
 *  com.fasterxml.jackson.annotation.JsonIgnoreProperties
 *  com.fasterxml.jackson.annotation.JsonProperty
 *  com.fasterxml.jackson.core.Version
 *  com.fasterxml.jackson.databind.annotation.JsonDeserialize
 *  com.fasterxml.jackson.databind.module.SimpleModule
 */
package org.springframework.data.geo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.util.List;
import org.springframework.data.geo.Box;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metric;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.geo.Polygon;

public class GeoModule
extends SimpleModule {
    private static final long serialVersionUID = 1L;

    public GeoModule() {
        super("Spring Data Geo Mixins", new Version(1, 0, 0, null, "org.springframework.data", "spring-data-commons-geo"));
        this.setMixInAnnotation(Distance.class, DistanceMixin.class);
        this.setMixInAnnotation(Point.class, PointMixin.class);
        this.setMixInAnnotation(Box.class, BoxMixin.class);
        this.setMixInAnnotation(Circle.class, CircleMixin.class);
        this.setMixInAnnotation(Polygon.class, PolygonMixin.class);
    }

    static abstract class PolygonMixin {
        PolygonMixin(@JsonProperty(value="points") List<Point> points) {
        }
    }

    static abstract class BoxMixin {
        BoxMixin(@JsonProperty(value="first") Point first, @JsonProperty(value="second") Point point) {
        }
    }

    static abstract class CircleMixin {
        CircleMixin(@JsonProperty(value="center") Point center, @JsonProperty(value="radius") Distance radius) {
        }
    }

    static abstract class PointMixin {
        PointMixin(@JsonProperty(value="x") double x, @JsonProperty(value="y") double y) {
        }
    }

    @JsonIgnoreProperties(value={"unit"})
    static abstract class DistanceMixin {
        DistanceMixin(@JsonProperty(value="value") double value, @JsonProperty(value="metric") @JsonDeserialize(as=Metrics.class) Metric metic) {
        }

        @JsonIgnore
        abstract double getNormalizedValue();
    }
}

