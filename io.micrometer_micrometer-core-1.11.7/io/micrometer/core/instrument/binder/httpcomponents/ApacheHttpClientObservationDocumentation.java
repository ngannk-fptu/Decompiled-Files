/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.docs.KeyName
 *  io.micrometer.observation.Observation$Context
 *  io.micrometer.observation.ObservationConvention
 *  io.micrometer.observation.docs.ObservationDocumentation
 */
package io.micrometer.core.instrument.binder.httpcomponents;

import io.micrometer.common.docs.KeyName;
import io.micrometer.core.instrument.binder.httpcomponents.DefaultApacheHttpClientObservationConvention;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationConvention;
import io.micrometer.observation.docs.ObservationDocumentation;

public enum ApacheHttpClientObservationDocumentation implements ObservationDocumentation
{
    DEFAULT{

        public Class<? extends ObservationConvention<? extends Observation.Context>> getDefaultConvention() {
            return DefaultApacheHttpClientObservationConvention.class;
        }

        public KeyName[] getLowCardinalityKeyNames() {
            return ApacheHttpClientKeyNames.values();
        }
    };


    static enum ApacheHttpClientKeyNames implements KeyName
    {
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
        TARGET_SCHEME{

            public String asString() {
                return "target.scheme";
            }

            public boolean isRequired() {
                return false;
            }
        }
        ,
        TARGET_HOST{

            public String asString() {
                return "target.host";
            }

            public boolean isRequired() {
                return false;
            }
        }
        ,
        TARGET_PORT{

            public String asString() {
                return "target.port";
            }

            public boolean isRequired() {
                return false;
            }
        };

    }
}

