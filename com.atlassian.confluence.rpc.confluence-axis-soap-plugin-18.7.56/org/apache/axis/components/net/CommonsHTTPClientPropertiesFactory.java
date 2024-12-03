/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.components.net;

import org.apache.axis.AxisProperties;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.components.net.CommonsHTTPClientProperties;
import org.apache.commons.logging.Log;

public class CommonsHTTPClientPropertiesFactory {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$components$net$CommonsHTTPClientPropertiesFactory == null ? (class$org$apache$axis$components$net$CommonsHTTPClientPropertiesFactory = CommonsHTTPClientPropertiesFactory.class$("org.apache.axis.components.net.CommonsHTTPClientPropertiesFactory")) : class$org$apache$axis$components$net$CommonsHTTPClientPropertiesFactory).getName());
    private static CommonsHTTPClientProperties properties;
    static /* synthetic */ Class class$org$apache$axis$components$net$CommonsHTTPClientPropertiesFactory;
    static /* synthetic */ Class class$org$apache$axis$components$net$CommonsHTTPClientProperties;
    static /* synthetic */ Class class$org$apache$axis$components$net$DefaultCommonsHTTPClientProperties;

    public static synchronized CommonsHTTPClientProperties create() {
        if (properties == null) {
            properties = (CommonsHTTPClientProperties)AxisProperties.newInstance(class$org$apache$axis$components$net$CommonsHTTPClientProperties == null ? (class$org$apache$axis$components$net$CommonsHTTPClientProperties = CommonsHTTPClientPropertiesFactory.class$("org.apache.axis.components.net.CommonsHTTPClientProperties")) : class$org$apache$axis$components$net$CommonsHTTPClientProperties, class$org$apache$axis$components$net$DefaultCommonsHTTPClientProperties == null ? (class$org$apache$axis$components$net$DefaultCommonsHTTPClientProperties = CommonsHTTPClientPropertiesFactory.class$("org.apache.axis.components.net.DefaultCommonsHTTPClientProperties")) : class$org$apache$axis$components$net$DefaultCommonsHTTPClientProperties);
        }
        return properties;
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

