/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.internal.hbm;

import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmFetchStyleWithSubselectEnum;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmLazyWithExtraEnum;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmOuterJoinEnum;
import org.hibernate.boot.model.source.spi.FetchCharacteristicsPluralAttribute;
import org.hibernate.boot.spi.MappingDefaults;
import org.hibernate.engine.FetchStyle;
import org.hibernate.engine.FetchTiming;

public class FetchCharacteristicsPluralAttributeImpl
implements FetchCharacteristicsPluralAttribute {
    private final FetchTiming fetchTiming;
    private final FetchStyle fetchStyle;
    private final Integer batchSize;
    private boolean extraLazy;

    public FetchCharacteristicsPluralAttributeImpl(FetchTiming fetchTiming, FetchStyle fetchStyle, Integer batchSize, boolean extraLazy) {
        this.fetchTiming = fetchTiming;
        this.fetchStyle = fetchStyle;
        this.batchSize = batchSize;
        this.extraLazy = extraLazy;
    }

    @Override
    public FetchTiming getFetchTiming() {
        return this.fetchTiming;
    }

    @Override
    public FetchStyle getFetchStyle() {
        return this.fetchStyle;
    }

    @Override
    public Integer getBatchSize() {
        return this.batchSize;
    }

    @Override
    public boolean isExtraLazy() {
        return this.getFetchTiming() == FetchTiming.DELAYED && this.extraLazy;
    }

    public static FetchCharacteristicsPluralAttributeImpl interpret(MappingDefaults mappingDefaults, JaxbHbmFetchStyleWithSubselectEnum fetch, JaxbHbmOuterJoinEnum outerJoin, JaxbHbmLazyWithExtraEnum lazy, int batchSize) {
        Builder builder = new Builder(mappingDefaults);
        if (fetch == null) {
            if (outerJoin != null && outerJoin == JaxbHbmOuterJoinEnum.TRUE) {
                builder.setFetchStyle(FetchStyle.JOIN);
            }
        } else if (fetch == JaxbHbmFetchStyleWithSubselectEnum.SUBSELECT) {
            builder.setFetchStyle(FetchStyle.SUBSELECT);
        } else if (fetch == JaxbHbmFetchStyleWithSubselectEnum.JOIN) {
            builder.setFetchStyle(FetchStyle.JOIN);
        }
        if (lazy != null) {
            if (lazy == JaxbHbmLazyWithExtraEnum.TRUE) {
                builder.setFetchTiming(FetchTiming.DELAYED);
            } else if (lazy == JaxbHbmLazyWithExtraEnum.FALSE) {
                builder.setFetchTiming(FetchTiming.IMMEDIATE);
            } else if (lazy == JaxbHbmLazyWithExtraEnum.EXTRA) {
                builder.setFetchTiming(FetchTiming.DELAYED);
                builder.setExtraLazy(true);
            }
        }
        builder.setBatchSize(batchSize);
        if (batchSize > 0 && (builder.fetchStyle == FetchStyle.JOIN || builder.fetchStyle == FetchStyle.SELECT)) {
            builder.setFetchStyle(FetchStyle.BATCH);
        }
        return builder.createPluralAttributeFetchCharacteristics();
    }

    public static class Builder {
        private FetchTiming fetchTiming;
        private FetchStyle fetchStyle;
        private Integer batchSize;
        private boolean extraLazy;

        public Builder(MappingDefaults mappingDefaults) {
            this.setFetchStyle(FetchStyle.SELECT);
            if (mappingDefaults.areCollectionsImplicitlyLazy()) {
                this.setFetchTiming(FetchTiming.DELAYED);
            } else {
                this.setFetchTiming(FetchTiming.IMMEDIATE);
            }
        }

        public Builder setFetchTiming(FetchTiming fetchTiming) {
            this.fetchTiming = fetchTiming;
            return this;
        }

        public Builder setFetchStyle(FetchStyle fetchStyle) {
            this.fetchStyle = fetchStyle;
            return this;
        }

        public Builder setBatchSize(Integer batchSize) {
            this.batchSize = batchSize;
            return this;
        }

        public void setExtraLazy(boolean extraLazy) {
            this.extraLazy = extraLazy;
        }

        public FetchCharacteristicsPluralAttributeImpl createPluralAttributeFetchCharacteristics() {
            return new FetchCharacteristicsPluralAttributeImpl(this.fetchTiming, this.fetchStyle, this.batchSize, this.extraLazy);
        }
    }
}

