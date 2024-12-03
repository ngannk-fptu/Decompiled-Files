/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.docs.KeyName
 *  io.micrometer.observation.Observation$Context
 *  io.micrometer.observation.ObservationConvention
 *  io.micrometer.observation.docs.ObservationDocumentation
 */
package io.micrometer.core.instrument.binder.jdk;

import io.micrometer.common.docs.KeyName;
import io.micrometer.core.instrument.binder.jdk.DefaultHttpClientObservationConvention;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationConvention;
import io.micrometer.observation.docs.ObservationDocumentation;

enum HttpClientObservationDocumentation implements ObservationDocumentation
{
    HTTP_CALL{

        public Class<? extends ObservationConvention<? extends Observation.Context>> getDefaultConvention() {
            return DefaultHttpClientObservationConvention.class;
        }

        public KeyName[] getLowCardinalityKeyNames() {
            return LowCardinalityKeys.values();
        }
    };


    static enum LowCardinalityKeys implements KeyName
    {
        METHOD{

            public String asString() {
                return "method";
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
        }
        ,
        URI{

            public String asString() {
                return "uri";
            }
        };

    }
}

