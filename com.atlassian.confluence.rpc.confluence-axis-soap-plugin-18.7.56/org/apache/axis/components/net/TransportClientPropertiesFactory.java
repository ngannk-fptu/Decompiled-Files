/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.components.net;

import java.util.HashMap;
import org.apache.axis.AxisProperties;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.components.net.TransportClientProperties;
import org.apache.commons.logging.Log;

public class TransportClientPropertiesFactory {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$components$net$SocketFactoryFactory == null ? (class$org$apache$axis$components$net$SocketFactoryFactory = TransportClientPropertiesFactory.class$("org.apache.axis.components.net.SocketFactoryFactory")) : class$org$apache$axis$components$net$SocketFactoryFactory).getName());
    private static HashMap cache = new HashMap();
    private static HashMap defaults = new HashMap();
    static /* synthetic */ Class class$org$apache$axis$components$net$SocketFactoryFactory;
    static /* synthetic */ Class class$org$apache$axis$components$net$DefaultHTTPTransportClientProperties;
    static /* synthetic */ Class class$org$apache$axis$components$net$DefaultHTTPSTransportClientProperties;
    static /* synthetic */ Class class$org$apache$axis$components$net$TransportClientProperties;

    public static TransportClientProperties create(String protocol) {
        TransportClientProperties tcp = (TransportClientProperties)cache.get(protocol);
        if (tcp == null && (tcp = (TransportClientProperties)AxisProperties.newInstance(class$org$apache$axis$components$net$TransportClientProperties == null ? (class$org$apache$axis$components$net$TransportClientProperties = TransportClientPropertiesFactory.class$("org.apache.axis.components.net.TransportClientProperties")) : class$org$apache$axis$components$net$TransportClientProperties, (Class)defaults.get(protocol))) != null) {
            cache.put(protocol, tcp);
        }
        return tcp;
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    static {
        defaults.put("http", class$org$apache$axis$components$net$DefaultHTTPTransportClientProperties == null ? (class$org$apache$axis$components$net$DefaultHTTPTransportClientProperties = TransportClientPropertiesFactory.class$("org.apache.axis.components.net.DefaultHTTPTransportClientProperties")) : class$org$apache$axis$components$net$DefaultHTTPTransportClientProperties);
        defaults.put("https", class$org$apache$axis$components$net$DefaultHTTPSTransportClientProperties == null ? (class$org$apache$axis$components$net$DefaultHTTPSTransportClientProperties = TransportClientPropertiesFactory.class$("org.apache.axis.components.net.DefaultHTTPSTransportClientProperties")) : class$org$apache$axis$components$net$DefaultHTTPSTransportClientProperties);
    }
}

