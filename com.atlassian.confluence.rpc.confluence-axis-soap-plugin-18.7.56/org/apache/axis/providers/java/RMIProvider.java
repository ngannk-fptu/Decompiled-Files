/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.providers.java;

import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.Remote;
import org.apache.axis.Handler;
import org.apache.axis.MessageContext;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.providers.java.RPCProvider;
import org.apache.commons.logging.Log;

public class RMIProvider
extends RPCProvider {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$providers$java$RMIProvider == null ? (class$org$apache$axis$providers$java$RMIProvider = RMIProvider.class$("org.apache.axis.providers.java.RMIProvider")) : class$org$apache$axis$providers$java$RMIProvider).getName());
    protected static Log entLog = LogFactory.getLog("org.apache.axis.enterprise");
    public static final String OPTION_NAMING_LOOKUP = "NamingLookup";
    public static final String OPTION_INTERFACE_CLASSNAME = "InterfaceClassName";
    static /* synthetic */ Class class$org$apache$axis$providers$java$RMIProvider;

    protected Object makeNewServiceObject(MessageContext msgContext, String clsName) throws Exception {
        String namingLookup = this.getStrOption(OPTION_NAMING_LOOKUP, msgContext.getService());
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new RMISecurityManager());
        }
        Remote targetObject = Naming.lookup(namingLookup);
        return targetObject;
    }

    protected String getServiceClassNameOptionName() {
        return OPTION_INTERFACE_CLASSNAME;
    }

    protected String getStrOption(String optionName, Handler service) {
        String value = null;
        if (service != null) {
            value = (String)service.getOption(optionName);
        }
        if (value == null) {
            value = (String)this.getOption(optionName);
        }
        return value;
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

