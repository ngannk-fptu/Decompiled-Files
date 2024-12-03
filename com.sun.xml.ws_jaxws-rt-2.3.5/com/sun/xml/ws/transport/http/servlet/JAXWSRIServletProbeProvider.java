/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.glassfish.external.probe.provider.annotations.Probe
 *  org.glassfish.external.probe.provider.annotations.ProbeParam
 *  org.glassfish.external.probe.provider.annotations.ProbeProvider
 */
package com.sun.xml.ws.transport.http.servlet;

import org.glassfish.external.probe.provider.annotations.Probe;
import org.glassfish.external.probe.provider.annotations.ProbeParam;
import org.glassfish.external.probe.provider.annotations.ProbeProvider;

@ProbeProvider(moduleProviderName="glassfish", moduleName="webservices", probeProviderName="servlet-ri")
public class JAXWSRIServletProbeProvider {
    @Probe(name="startedEvent")
    public void startedEvent(@ProbeParam(value="endpointAddress") String endpointAddress) {
    }

    @Probe(name="endedEvent")
    public void endedEvent(@ProbeParam(value="endpointAddress") String endpointAddress) {
    }
}

