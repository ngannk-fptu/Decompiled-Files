/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.glassfish.external.probe.provider.annotations.Probe
 *  org.glassfish.external.probe.provider.annotations.ProbeParam
 *  org.glassfish.external.probe.provider.annotations.ProbeProvider
 */
package com.sun.xml.ws.transport.http.servlet;

import com.sun.xml.ws.transport.http.servlet.ServletAdapter;
import org.glassfish.external.probe.provider.annotations.Probe;
import org.glassfish.external.probe.provider.annotations.ProbeParam;
import org.glassfish.external.probe.provider.annotations.ProbeProvider;

@ProbeProvider(moduleProviderName="glassfish", moduleName="webservices", probeProviderName="deployment-ri")
public class JAXWSRIDeploymentProbeProvider {
    @Probe(name="deploy", hidden=true)
    public void deploy(@ProbeParam(value="adapter") ServletAdapter adpater) {
    }

    @Probe(name="undeploy", hidden=true)
    public void undeploy(@ProbeParam(value="adapter") ServletAdapter adapter) {
    }
}

