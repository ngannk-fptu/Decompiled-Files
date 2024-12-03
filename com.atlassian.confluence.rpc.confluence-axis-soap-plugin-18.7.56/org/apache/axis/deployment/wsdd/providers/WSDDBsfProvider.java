/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.deployment.wsdd.providers;

import java.io.IOException;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.Handler;
import org.apache.axis.deployment.wsdd.WSDDProvider;
import org.apache.axis.deployment.wsdd.WSDDService;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.providers.BSFProvider;

public class WSDDBsfProvider
extends WSDDProvider {
    public String getName() {
        return "BSF";
    }

    public Handler newProviderInstance(WSDDService service, EngineConfiguration registry) throws Exception {
        BSFProvider provider = new BSFProvider();
        String option = service.getParameter("language");
        if (!option.equals("")) {
            provider.setOption("language", option);
        }
        if (!(option = service.getParameter("src")).equals("")) {
            provider.setOption("src", option);
        }
        if (!option.equals("")) {
            provider.setOption("script", option);
        }
        return provider;
    }

    public void writeToContext(SerializationContext context) throws IOException {
    }
}

