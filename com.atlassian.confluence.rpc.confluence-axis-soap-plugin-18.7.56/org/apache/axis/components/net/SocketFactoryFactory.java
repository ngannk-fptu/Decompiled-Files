/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.components.net;

import java.util.Hashtable;
import org.apache.axis.AxisProperties;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.components.net.SecureSocketFactory;
import org.apache.axis.components.net.SocketFactory;
import org.apache.commons.logging.Log;

public class SocketFactoryFactory {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$components$net$SocketFactoryFactory == null ? (class$org$apache$axis$components$net$SocketFactoryFactory = SocketFactoryFactory.class$("org.apache.axis.components.net.SocketFactoryFactory")) : class$org$apache$axis$components$net$SocketFactoryFactory).getName());
    private static Hashtable factories = new Hashtable();
    private static final Class[] classes = new Class[]{class$java$util$Hashtable == null ? (class$java$util$Hashtable = SocketFactoryFactory.class$("java.util.Hashtable")) : class$java$util$Hashtable};
    static /* synthetic */ Class class$org$apache$axis$components$net$SocketFactoryFactory;
    static /* synthetic */ Class class$java$util$Hashtable;
    static /* synthetic */ Class class$org$apache$axis$components$net$SocketFactory;
    static /* synthetic */ Class class$org$apache$axis$components$net$SecureSocketFactory;

    public static synchronized SocketFactory getFactory(String protocol, Hashtable attributes) {
        SocketFactory theFactory = (SocketFactory)factories.get(protocol);
        if (theFactory == null) {
            Object[] objects = new Object[]{attributes};
            if (protocol.equalsIgnoreCase("http")) {
                theFactory = (SocketFactory)AxisProperties.newInstance(class$org$apache$axis$components$net$SocketFactory == null ? (class$org$apache$axis$components$net$SocketFactory = SocketFactoryFactory.class$("org.apache.axis.components.net.SocketFactory")) : class$org$apache$axis$components$net$SocketFactory, classes, objects);
            } else if (protocol.equalsIgnoreCase("https")) {
                theFactory = (SecureSocketFactory)AxisProperties.newInstance(class$org$apache$axis$components$net$SecureSocketFactory == null ? (class$org$apache$axis$components$net$SecureSocketFactory = SocketFactoryFactory.class$("org.apache.axis.components.net.SecureSocketFactory")) : class$org$apache$axis$components$net$SecureSocketFactory, classes, objects);
            }
            if (theFactory != null) {
                factories.put(protocol, theFactory);
            }
        }
        return theFactory;
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
        AxisProperties.setClassOverrideProperty(class$org$apache$axis$components$net$SocketFactory == null ? (class$org$apache$axis$components$net$SocketFactory = SocketFactoryFactory.class$("org.apache.axis.components.net.SocketFactory")) : class$org$apache$axis$components$net$SocketFactory, "axis.socketFactory");
        AxisProperties.setClassDefault(class$org$apache$axis$components$net$SocketFactory == null ? (class$org$apache$axis$components$net$SocketFactory = SocketFactoryFactory.class$("org.apache.axis.components.net.SocketFactory")) : class$org$apache$axis$components$net$SocketFactory, "org.apache.axis.components.net.DefaultSocketFactory");
        AxisProperties.setClassOverrideProperty(class$org$apache$axis$components$net$SecureSocketFactory == null ? (class$org$apache$axis$components$net$SecureSocketFactory = SocketFactoryFactory.class$("org.apache.axis.components.net.SecureSocketFactory")) : class$org$apache$axis$components$net$SecureSocketFactory, "axis.socketSecureFactory");
        AxisProperties.setClassDefault(class$org$apache$axis$components$net$SecureSocketFactory == null ? (class$org$apache$axis$components$net$SecureSocketFactory = SocketFactoryFactory.class$("org.apache.axis.components.net.SecureSocketFactory")) : class$org$apache$axis$components$net$SecureSocketFactory, "org.apache.axis.components.net.JSSESocketFactory");
    }
}

