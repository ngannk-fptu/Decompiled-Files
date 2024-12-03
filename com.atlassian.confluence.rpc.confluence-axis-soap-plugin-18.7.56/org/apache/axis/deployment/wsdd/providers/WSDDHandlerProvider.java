/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.deployment.wsdd.providers;

import org.apache.axis.ConfigurationException;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.Handler;
import org.apache.axis.deployment.wsdd.WSDDProvider;
import org.apache.axis.deployment.wsdd.WSDDService;
import org.apache.axis.utils.ClassUtils;
import org.apache.axis.utils.Messages;

public class WSDDHandlerProvider
extends WSDDProvider {
    static /* synthetic */ Class class$org$apache$axis$Handler;

    public String getName() {
        return "Handler";
    }

    public Handler newProviderInstance(WSDDService service, EngineConfiguration registry) throws Exception {
        Class _class;
        String providerClass = service.getParameter("handlerClass");
        if (providerClass == null) {
            throw new ConfigurationException(Messages.getMessage("noHandlerClass00"));
        }
        if (!(class$org$apache$axis$Handler == null ? (class$org$apache$axis$Handler = WSDDHandlerProvider.class$("org.apache.axis.Handler")) : class$org$apache$axis$Handler).isAssignableFrom(_class = ClassUtils.forName(providerClass))) {
            throw new ConfigurationException(Messages.getMessage("badHandlerClass00", _class.getName()));
        }
        return (Handler)_class.newInstance();
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

