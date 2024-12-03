/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.internal.hbm;

import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmFetchStyleEnum;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmLazyEnum;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmLazyWithNoProxyEnum;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmOuterJoinEnum;
import org.hibernate.boot.model.source.spi.FetchCharacteristicsSingularAssociation;
import org.hibernate.boot.spi.MappingDefaults;
import org.hibernate.engine.FetchStyle;
import org.hibernate.engine.FetchTiming;
import org.hibernate.internal.log.DeprecationLogger;

public class FetchCharacteristicsSingularAssociationImpl
implements FetchCharacteristicsSingularAssociation {
    private final FetchTiming fetchTiming;
    private final FetchStyle fetchStyle;
    private final boolean unwrapProxies;

    private FetchCharacteristicsSingularAssociationImpl(FetchTiming fetchTiming, FetchStyle fetchStyle, boolean unwrapProxies) {
        this.fetchTiming = fetchTiming;
        this.fetchStyle = fetchStyle;
        this.unwrapProxies = unwrapProxies;
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
    public boolean isUnwrapProxies() {
        return this.unwrapProxies;
    }

    public static FetchCharacteristicsSingularAssociationImpl interpretManyToOne(MappingDefaults mappingDefaults, JaxbHbmFetchStyleEnum fetchMapping, JaxbHbmOuterJoinEnum outerJoinMapping, JaxbHbmLazyWithNoProxyEnum lazyMapping) {
        Builder builder = new Builder(mappingDefaults);
        if (fetchMapping == null) {
            if (outerJoinMapping == null) {
                builder.setFetchStyle(FetchStyle.SELECT);
            } else {
                switch (outerJoinMapping) {
                    case TRUE: {
                        builder.setFetchStyle(FetchStyle.JOIN);
                        break;
                    }
                    default: {
                        builder.setFetchStyle(FetchStyle.SELECT);
                        break;
                    }
                }
            }
        } else if (fetchMapping == JaxbHbmFetchStyleEnum.JOIN) {
            builder.setFetchStyle(FetchStyle.JOIN);
        } else {
            builder.setFetchStyle(FetchStyle.SELECT);
        }
        if (lazyMapping != null) {
            if (lazyMapping == JaxbHbmLazyWithNoProxyEnum.NO_PROXY) {
                builder.setFetchTiming(FetchTiming.DELAYED);
                builder.setUnwrapProxies(true);
            } else if (lazyMapping == JaxbHbmLazyWithNoProxyEnum.PROXY) {
                builder.setFetchTiming(FetchTiming.DELAYED);
            } else if (lazyMapping == JaxbHbmLazyWithNoProxyEnum.FALSE) {
                builder.setFetchTiming(FetchTiming.IMMEDIATE);
            }
        }
        return builder.createFetchCharacteristics();
    }

    public static FetchCharacteristicsSingularAssociationImpl interpretManyToManyElement(MappingDefaults mappingDefaults, JaxbHbmFetchStyleEnum fetchMapping, JaxbHbmOuterJoinEnum outerJoinMapping, JaxbHbmLazyEnum lazyMapping) {
        Builder builder = new Builder(mappingDefaults);
        if (fetchMapping == null) {
            if (outerJoinMapping == null) {
                builder.setFetchTiming(FetchTiming.IMMEDIATE);
                builder.setFetchStyle(FetchStyle.JOIN);
            } else {
                DeprecationLogger.DEPRECATION_LOGGER.deprecatedManyToManyOuterJoin();
                builder.setFetchTiming(FetchTiming.IMMEDIATE);
                builder.setFetchStyle(FetchStyle.JOIN);
            }
        } else {
            DeprecationLogger.DEPRECATION_LOGGER.deprecatedManyToManyFetch();
            builder.setFetchTiming(FetchTiming.IMMEDIATE);
            builder.setFetchStyle(FetchStyle.JOIN);
        }
        if (lazyMapping != null && lazyMapping == JaxbHbmLazyEnum.FALSE) {
            builder.setFetchTiming(FetchTiming.IMMEDIATE);
        }
        return builder.createFetchCharacteristics();
    }

    public static FetchCharacteristicsSingularAssociationImpl interpretOneToOne(MappingDefaults mappingDefaults, JaxbHbmFetchStyleEnum fetchMapping, JaxbHbmOuterJoinEnum outerJoinMapping, JaxbHbmLazyWithNoProxyEnum lazyMapping, boolean constrained) {
        Builder builder = new Builder(mappingDefaults);
        if (fetchMapping == null) {
            if (outerJoinMapping == null) {
                if (!constrained) {
                    builder.setFetchTiming(FetchTiming.IMMEDIATE);
                    builder.setFetchStyle(FetchStyle.JOIN);
                } else {
                    builder.setFetchStyle(FetchStyle.SELECT);
                }
            } else {
                switch (outerJoinMapping) {
                    case TRUE: {
                        builder.setFetchStyle(FetchStyle.JOIN);
                        break;
                    }
                    default: {
                        builder.setFetchStyle(FetchStyle.SELECT);
                        break;
                    }
                }
            }
        } else if (fetchMapping == JaxbHbmFetchStyleEnum.JOIN) {
            builder.setFetchStyle(FetchStyle.JOIN);
        } else {
            builder.setFetchStyle(FetchStyle.SELECT);
        }
        if (lazyMapping != null) {
            if (lazyMapping == JaxbHbmLazyWithNoProxyEnum.NO_PROXY) {
                builder.setFetchTiming(FetchTiming.DELAYED);
                builder.setUnwrapProxies(true);
            } else if (lazyMapping == JaxbHbmLazyWithNoProxyEnum.PROXY) {
                builder.setFetchTiming(FetchTiming.DELAYED);
            } else if (lazyMapping == JaxbHbmLazyWithNoProxyEnum.FALSE) {
                builder.setFetchTiming(FetchTiming.IMMEDIATE);
            }
        }
        return builder.createFetchCharacteristics();
    }

    public static class Builder {
        private FetchTiming fetchTiming = FetchTiming.DELAYED;
        private FetchStyle fetchStyle = FetchStyle.SELECT;
        private boolean unwrapProxies;

        public Builder(MappingDefaults mappingDefaults) {
        }

        public Builder setFetchTiming(FetchTiming fetchTiming) {
            this.fetchTiming = fetchTiming;
            return this;
        }

        public Builder setFetchStyle(FetchStyle fetchStyle) {
            this.fetchStyle = fetchStyle;
            return this;
        }

        public Builder setUnwrapProxies(boolean unwrapProxies) {
            this.unwrapProxies = unwrapProxies;
            return this;
        }

        public FetchCharacteristicsSingularAssociationImpl createFetchCharacteristics() {
            return new FetchCharacteristicsSingularAssociationImpl(this.fetchTiming, this.fetchStyle, this.unwrapProxies);
        }
    }
}

