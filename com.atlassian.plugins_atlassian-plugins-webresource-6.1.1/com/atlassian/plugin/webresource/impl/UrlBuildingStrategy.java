/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.webresource.condition.UrlReadingCondition
 *  com.atlassian.plugin.webresource.transformer.TransformerUrlBuilder
 *  com.atlassian.plugin.webresource.url.UrlBuilder
 *  com.atlassian.webresource.api.assembler.resource.PrebakeError
 *  com.atlassian.webresource.api.prebake.Coordinate
 *  com.atlassian.webresource.api.prebake.DimensionAwareTransformerUrlBuilder
 *  com.atlassian.webresource.api.prebake.DimensionAwareUrlReadingCondition
 */
package com.atlassian.plugin.webresource.impl;

import com.atlassian.plugin.webresource.condition.UrlReadingCondition;
import com.atlassian.plugin.webresource.impl.PrebakeErrorFactory;
import com.atlassian.plugin.webresource.prebake.DimensionUnawareOverride;
import com.atlassian.plugin.webresource.transformer.TransformerUrlBuilder;
import com.atlassian.plugin.webresource.url.UrlBuilder;
import com.atlassian.webresource.api.assembler.resource.PrebakeError;
import com.atlassian.webresource.api.prebake.Coordinate;
import com.atlassian.webresource.api.prebake.DimensionAwareTransformerUrlBuilder;
import com.atlassian.webresource.api.prebake.DimensionAwareUrlReadingCondition;
import java.util.Optional;

public abstract class UrlBuildingStrategy {
    private UrlBuildingStrategy() {
    }

    public static UrlBuildingStrategy normal() {
        return new NormalStrategy();
    }

    public static UrlBuildingStrategy overCoordinate(Coordinate coord) {
        return new CoordStrategy(coord);
    }

    public static UrlBuildingStrategy from(Optional<Coordinate> coord) {
        return coord.isPresent() ? UrlBuildingStrategy.overCoordinate(coord.get()) : UrlBuildingStrategy.normal();
    }

    public abstract void addToUrl(UrlReadingCondition var1, UrlBuilder var2);

    public abstract void addToUrl(TransformerUrlBuilder var1, UrlBuilder var2);

    private static class CoordStrategy
    extends UrlBuildingStrategy {
        private final Coordinate coord;

        public CoordStrategy(Coordinate coord) {
            this.coord = coord;
        }

        @Override
        public void addToUrl(UrlReadingCondition condition, UrlBuilder urlBuilder) {
            if (condition instanceof DimensionAwareUrlReadingCondition) {
                DimensionAwareUrlReadingCondition c = (DimensionAwareUrlReadingCondition)condition;
                c.addToUrl(urlBuilder, this.coord);
            } else if (DimensionUnawareOverride.contains(condition.getClass().getName())) {
                String key = DimensionUnawareOverride.key(condition.getClass().getName());
                this.coord.copyTo(urlBuilder, key);
            } else {
                condition.addToUrl(urlBuilder);
                PrebakeError e = PrebakeErrorFactory.from(condition);
                urlBuilder.addPrebakeError(e);
            }
        }

        @Override
        public void addToUrl(TransformerUrlBuilder transformer, UrlBuilder urlBuilder) {
            if (transformer instanceof DimensionAwareTransformerUrlBuilder) {
                DimensionAwareTransformerUrlBuilder t = (DimensionAwareTransformerUrlBuilder)transformer;
                t.addToUrl(urlBuilder, this.coord);
            } else {
                transformer.addToUrl(urlBuilder);
                PrebakeError e = PrebakeErrorFactory.from(transformer);
                urlBuilder.addPrebakeError(e);
            }
        }
    }

    private static class NormalStrategy
    extends UrlBuildingStrategy {
        private NormalStrategy() {
        }

        @Override
        public void addToUrl(UrlReadingCondition condition, UrlBuilder urlBuilder) {
            condition.addToUrl(urlBuilder);
        }

        @Override
        public void addToUrl(TransformerUrlBuilder transformer, UrlBuilder urlBuilder) {
            transformer.addToUrl(urlBuilder);
        }
    }
}

