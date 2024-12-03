/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.KeyValue
 *  io.micrometer.common.KeyValues
 *  org.eclipse.jetty.client.api.Request
 *  org.eclipse.jetty.client.api.Result
 */
package io.micrometer.core.instrument.binder.jetty;

import io.micrometer.common.KeyValue;
import io.micrometer.common.KeyValues;
import io.micrometer.core.instrument.binder.jetty.JettyClientContext;
import io.micrometer.core.instrument.binder.jetty.JettyClientKeyValues;
import io.micrometer.core.instrument.binder.jetty.JettyClientObservationConvention;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.api.Result;

public class DefaultJettyClientObservationConvention
implements JettyClientObservationConvention {
    public static DefaultJettyClientObservationConvention INSTANCE = new DefaultJettyClientObservationConvention();

    public KeyValues getLowCardinalityKeyValues(JettyClientContext context) {
        Request request = (Request)context.getCarrier();
        Result result = (Result)context.getResponse();
        return KeyValues.of((KeyValue[])new KeyValue[]{JettyClientKeyValues.method(request), JettyClientKeyValues.host(request), JettyClientKeyValues.uri(request, result, context.getUriPatternFunction()), JettyClientKeyValues.exception(result), JettyClientKeyValues.status(result), JettyClientKeyValues.outcome(result)});
    }

    public String getName() {
        return "jetty.client.requests";
    }
}

