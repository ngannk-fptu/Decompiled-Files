/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.KeyValue
 *  io.micrometer.common.KeyValues
 */
package io.micrometer.core.instrument.binder.grpc;

import io.micrometer.common.KeyValue;
import io.micrometer.common.KeyValues;
import io.micrometer.core.instrument.binder.grpc.GrpcClientObservationContext;
import io.micrometer.core.instrument.binder.grpc.GrpcClientObservationConvention;
import io.micrometer.core.instrument.binder.grpc.GrpcObservationDocumentation;
import java.util.ArrayList;

public class DefaultGrpcClientObservationConvention
implements GrpcClientObservationConvention {
    public String getName() {
        return "grpc.client";
    }

    public String getContextualName(GrpcClientObservationContext context) {
        return context.getFullMethodName();
    }

    public KeyValues getLowCardinalityKeyValues(GrpcClientObservationContext context) {
        ArrayList<KeyValue> keyValues = new ArrayList<KeyValue>();
        keyValues.add(GrpcObservationDocumentation.LowCardinalityKeyNames.METHOD.withValue(context.getMethodName()));
        keyValues.add(GrpcObservationDocumentation.LowCardinalityKeyNames.SERVICE.withValue(context.getServiceName()));
        keyValues.add(GrpcObservationDocumentation.LowCardinalityKeyNames.METHOD_TYPE.withValue(context.getMethodType().name()));
        if (context.getStatusCode() != null) {
            keyValues.add(GrpcObservationDocumentation.LowCardinalityKeyNames.STATUS_CODE.withValue(context.getStatusCode().name()));
        }
        return KeyValues.of(keyValues);
    }
}

