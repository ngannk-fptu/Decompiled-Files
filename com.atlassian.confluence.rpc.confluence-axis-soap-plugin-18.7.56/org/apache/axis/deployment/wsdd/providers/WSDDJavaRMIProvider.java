/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.deployment.wsdd.providers;

import org.apache.axis.EngineConfiguration;
import org.apache.axis.Handler;
import org.apache.axis.deployment.wsdd.WSDDProvider;
import org.apache.axis.deployment.wsdd.WSDDService;
import org.apache.axis.providers.java.RMIProvider;

public class WSDDJavaRMIProvider
extends WSDDProvider {
    public String getName() {
        return "RMI";
    }

    public Handler newProviderInstance(WSDDService service, EngineConfiguration registry) throws Exception {
        return new RMIProvider();
    }
}

