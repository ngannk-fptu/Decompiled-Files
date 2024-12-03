/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.deployment.wsdd.providers;

import org.apache.axis.EngineConfiguration;
import org.apache.axis.Handler;
import org.apache.axis.deployment.wsdd.WSDDProvider;
import org.apache.axis.deployment.wsdd.WSDDService;
import org.apache.axis.providers.BasicProvider;
import org.apache.axis.utils.ClassUtils;

public class WSDDComProvider
extends WSDDProvider {
    public static final String OPTION_PROGID = "ProgID";
    public static final String OPTION_THREADING_MODEL = "threadingModel";

    public String getName() {
        return "COM";
    }

    public Handler newProviderInstance(WSDDService service, EngineConfiguration registry) throws Exception {
        Class _class = ClassUtils.forName("org.apache.axis.providers.ComProvider");
        BasicProvider provider = (BasicProvider)_class.newInstance();
        String option = service.getParameter(OPTION_PROGID);
        if (!option.equals("")) {
            provider.setOption(OPTION_PROGID, option);
        }
        if ((option = service.getParameter(OPTION_THREADING_MODEL)) != null && !option.equals("")) {
            provider.setOption(OPTION_THREADING_MODEL, option);
        }
        return provider;
    }
}

