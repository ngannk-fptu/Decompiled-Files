/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.management.jmx;

import org.apache.axis.deployment.wsdd.WSDDTransport;

public class WSDDTransportWrapper {
    private WSDDTransport _wsddTransport;

    public WSDDTransport getWSDDTransport() {
        return this._wsddTransport;
    }

    public void setWSDDTransport(WSDDTransport _wsddTransport) {
        this._wsddTransport = _wsddTransport;
    }
}

