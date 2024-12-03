/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.docs.KeyName
 *  io.micrometer.common.lang.NonNullApi
 *  io.micrometer.observation.Observation$Context
 *  io.micrometer.observation.ObservationConvention
 *  io.micrometer.observation.docs.ObservationDocumentation
 */
package io.micrometer.core.instrument.binder.jersey.server;

import io.micrometer.common.docs.KeyName;
import io.micrometer.common.lang.NonNullApi;
import io.micrometer.core.instrument.binder.jersey.server.DefaultJerseyObservationConvention;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationConvention;
import io.micrometer.observation.docs.ObservationDocumentation;

@NonNullApi
public enum JerseyObservationDocumentation implements ObservationDocumentation
{
    DEFAULT{

        public Class<? extends ObservationConvention<? extends Observation.Context>> getDefaultConvention() {
            return DefaultJerseyObservationConvention.class;
        }

        public KeyName[] getLowCardinalityKeyNames() {
            return JerseyLegacyLowCardinalityTags.values();
        }
    };


    @NonNullApi
    static enum JerseyLegacyLowCardinalityTags implements KeyName
    {
        OUTCOME{

            public String asString() {
                return "outcome";
            }
        }
        ,
        METHOD{

            public String asString() {
                return "method";
            }
        }
        ,
        URI{

            public String asString() {
                return "uri";
            }
        }
        ,
        EXCEPTION{

            public String asString() {
                return "exception";
            }
        }
        ,
        STATUS{

            public String asString() {
                return "status";
            }
        };

    }
}

