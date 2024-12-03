/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.management.jmx;

import org.apache.axis.deployment.wsdd.WSDDService;

public class WSDDServiceWrapper {
    private WSDDService _wsddService;

    public WSDDService getWSDDService() {
        return this._wsddService;
    }

    public void setWSDDService(WSDDService wsddService) {
        this._wsddService = wsddService;
    }
}

