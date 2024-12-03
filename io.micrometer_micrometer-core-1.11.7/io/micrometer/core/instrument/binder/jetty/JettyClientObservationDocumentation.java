/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.docs.KeyName
 *  io.micrometer.observation.Observation$Context
 *  io.micrometer.observation.ObservationConvention
 *  io.micrometer.observation.docs.ObservationDocumentation
 */
package io.micrometer.core.instrument.binder.jetty;

import io.micrometer.common.docs.KeyName;
import io.micrometer.core.instrument.binder.jetty.JettyClientObservationConvention;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationConvention;
import io.micrometer.observation.docs.ObservationDocumentation;

public enum JettyClientObservationDocumentation implements ObservationDocumentation
{
    DEFAULT{

        public Class<? extends ObservationConvention<? extends Observation.Context>> getDefaultConvention() {
            return JettyClientObservationConvention.class;
        }

        public KeyName[] getLowCardinalityKeyNames() {
            return JettyClientLowCardinalityTags.values();
        }
    };


    static enum JettyClientLowCardinalityTags implements KeyName
    {
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
        METHOD{

            public String asString() {
                return "method";
            }
        }
        ,
        OUTCOME{

            public String asString() {
                return "outcome";
            }
        }
        ,
        STATUS{

            public String asString() {
                return "status";
            }
        }
        ,
        HOST{

            public String asString() {
                return "host";
            }
        };

    }
}

