/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.docs.KeyName
 *  io.micrometer.observation.Observation$Context
 *  io.micrometer.observation.Observation$Event
 *  io.micrometer.observation.ObservationConvention
 *  io.micrometer.observation.docs.ObservationDocumentation
 */
package io.micrometer.core.instrument.binder.grpc;

import io.micrometer.common.docs.KeyName;
import io.micrometer.core.instrument.binder.grpc.GrpcClientObservationConvention;
import io.micrometer.core.instrument.binder.grpc.GrpcServerObservationConvention;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationConvention;
import io.micrometer.observation.docs.ObservationDocumentation;

public enum GrpcObservationDocumentation implements ObservationDocumentation
{
    CLIENT{

        public Class<? extends ObservationConvention<? extends Observation.Context>> getDefaultConvention() {
            return GrpcClientObservationConvention.class;
        }

        public KeyName[] getLowCardinalityKeyNames() {
            return LowCardinalityKeyNames.values();
        }
    }
    ,
    SERVER{

        public Class<? extends ObservationConvention<? extends Observation.Context>> getDefaultConvention() {
            return GrpcServerObservationConvention.class;
        }

        public KeyName[] getLowCardinalityKeyNames() {
            return LowCardinalityKeyNames.values();
        }
    };


    public static enum GrpcServerEvents implements Observation.Event
    {
        MESSAGE_RECEIVED{

            public String getName() {
                return "received";
            }
        }
        ,
        MESSAGE_SENT{

            public String getName() {
                return "sent";
            }
        };

    }

    public static enum GrpcClientEvents implements Observation.Event
    {
        MESSAGE_SENT{

            public String getName() {
                return "sent";
            }
        }
        ,
        MESSAGE_RECEIVED{

            public String getName() {
                return "received";
            }
        };

    }

    public static enum LowCardinalityKeyNames implements KeyName
    {
        METHOD{

            public String asString() {
                return "rpc.method";
            }
        }
        ,
        METHOD_TYPE{

            public String asString() {
                return "rpc.type";
            }
        }
        ,
        SERVICE{

            public String asString() {
                return "rpc.service";
            }
        }
        ,
        ERROR_CODE{

            public String asString() {
                return "rpc.error_code";
            }
        }
        ,
        STATUS_CODE{

            public String asString() {
                return "grpc.status_code";
            }
        };

    }
}

