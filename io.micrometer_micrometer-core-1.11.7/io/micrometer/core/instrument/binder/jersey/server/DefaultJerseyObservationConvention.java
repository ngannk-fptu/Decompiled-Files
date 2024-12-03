/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.KeyValue
 *  io.micrometer.common.KeyValues
 *  io.micrometer.common.lang.Nullable
 *  org.glassfish.jersey.server.ContainerRequest
 *  org.glassfish.jersey.server.ContainerResponse
 *  org.glassfish.jersey.server.monitoring.RequestEvent
 */
package io.micrometer.core.instrument.binder.jersey.server;

import io.micrometer.common.KeyValue;
import io.micrometer.common.KeyValues;
import io.micrometer.common.lang.Nullable;
import io.micrometer.core.instrument.binder.jersey.server.JerseyContext;
import io.micrometer.core.instrument.binder.jersey.server.JerseyKeyValues;
import io.micrometer.core.instrument.binder.jersey.server.JerseyObservationConvention;
import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.server.ContainerResponse;
import org.glassfish.jersey.server.monitoring.RequestEvent;

public class DefaultJerseyObservationConvention
implements JerseyObservationConvention {
    private final String metricsName;

    public DefaultJerseyObservationConvention(String metricsName) {
        this.metricsName = metricsName;
    }

    public KeyValues getLowCardinalityKeyValues(JerseyContext context) {
        RequestEvent event = context.getRequestEvent();
        ContainerRequest request = (ContainerRequest)context.getCarrier();
        ContainerResponse response = (ContainerResponse)context.getResponse();
        return KeyValues.of((KeyValue[])new KeyValue[]{JerseyKeyValues.method(request), JerseyKeyValues.uri(event), JerseyKeyValues.exception(event), JerseyKeyValues.status(response), JerseyKeyValues.outcome(response)});
    }

    public String getName() {
        return this.metricsName;
    }

    @Nullable
    public String getContextualName(JerseyContext context) {
        if (context.getCarrier() == null) {
            return null;
        }
        return "HTTP " + ((ContainerRequest)context.getCarrier()).getMethod();
    }
}

