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
package io.micrometer.core.instrument.binder.okhttp3;

import io.micrometer.common.docs.KeyName;
import io.micrometer.common.lang.NonNullApi;
import io.micrometer.core.instrument.binder.okhttp3.DefaultOkHttpObservationConvention;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationConvention;
import io.micrometer.observation.docs.ObservationDocumentation;

@NonNullApi
public enum OkHttpObservationDocumentation implements ObservationDocumentation
{
    DEFAULT{

        public Class<? extends ObservationConvention<? extends Observation.Context>> getDefaultConvention() {
            return DefaultOkHttpObservationConvention.class;
        }

        public KeyName[] getLowCardinalityKeyNames() {
            return OkHttpLegacyLowCardinalityTags.values();
        }
    };


    @NonNullApi
    static enum OkHttpLegacyLowCardinalityTags implements KeyName
    {
        TARGET_SCHEME{

            public String asString() {
                return "target.scheme";
            }
        }
        ,
        TARGET_HOST{

            public String asString() {
                return "target.host";
            }
        }
        ,
        TARGET_PORT{

            public String asString() {
                return "target.port";
            }
        }
        ,
        HOST{

            public String asString() {
                return "host";
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
        STATUS{

            public String asString() {
                return "status";
            }
        }
        ,
        OUTCOME{

            public String asString() {
                return "outcome";
            }
        };

    }
}

