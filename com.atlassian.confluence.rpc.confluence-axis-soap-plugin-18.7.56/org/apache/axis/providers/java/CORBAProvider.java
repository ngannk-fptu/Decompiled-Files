/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.omg.CORBA.ORB
 *  org.omg.CORBA.Object
 *  org.omg.CosNaming.NameComponent
 *  org.omg.CosNaming.NamingContext
 *  org.omg.CosNaming.NamingContextHelper
 */
package org.apache.axis.providers.java;

import java.lang.reflect.Method;
import java.util.Properties;
import org.apache.axis.Handler;
import org.apache.axis.MessageContext;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.providers.java.RPCProvider;
import org.apache.axis.utils.ClassUtils;
import org.apache.commons.logging.Log;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContext;
import org.omg.CosNaming.NamingContextHelper;

public class CORBAProvider
extends RPCProvider {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$providers$java$CORBAProvider == null ? (class$org$apache$axis$providers$java$CORBAProvider = CORBAProvider.class$("org.apache.axis.providers.java.CORBAProvider")) : class$org$apache$axis$providers$java$CORBAProvider).getName());
    private static final String DEFAULT_ORB_INITIAL_HOST = "localhost";
    private static final String DEFAULT_ORB_INITIAL_PORT = "900";
    protected static Log entLog = LogFactory.getLog("org.apache.axis.enterprise");
    public static final String OPTION_ORB_INITIAL_HOST = "ORBInitialHost";
    public static final String OPTION_ORB_INITIAL_PORT = "ORBInitialPort";
    public static final String OPTION_NAME_ID = "NameID";
    public static final String OPTION_NAME_KIND = "NameKind";
    public static final String OPTION_INTERFACE_CLASSNAME = "InterfaceClassName";
    public static final String OPTION_HELPER_CLASSNAME = "HelperClassName";
    private static final Class[] CORBA_OBJECT_CLASS = new Class[]{class$org$omg$CORBA$Object == null ? (class$org$omg$CORBA$Object = CORBAProvider.class$("org.omg.CORBA.Object")) : class$org$omg$CORBA$Object};
    static /* synthetic */ Class class$org$apache$axis$providers$java$CORBAProvider;
    static /* synthetic */ Class class$org$omg$CORBA$Object;

    protected java.lang.Object makeNewServiceObject(MessageContext msgContext, String clsName) throws Exception {
        String orbInitialPort;
        String orbInitialHost = this.getStrOption(OPTION_ORB_INITIAL_HOST, msgContext.getService());
        if (orbInitialHost == null) {
            orbInitialHost = DEFAULT_ORB_INITIAL_HOST;
        }
        if ((orbInitialPort = this.getStrOption(OPTION_ORB_INITIAL_PORT, msgContext.getService())) == null) {
            orbInitialPort = DEFAULT_ORB_INITIAL_PORT;
        }
        String nameId = this.getStrOption(OPTION_NAME_ID, msgContext.getService());
        String nameKind = this.getStrOption(OPTION_NAME_KIND, msgContext.getService());
        String helperClassName = this.getStrOption(OPTION_HELPER_CLASSNAME, msgContext.getService());
        Properties orbProps = new Properties();
        orbProps.put("org.omg.CORBA.ORBInitialHost", orbInitialHost);
        orbProps.put("org.omg.CORBA.ORBInitialPort", orbInitialPort);
        ORB orb = ORB.init((String[])new String[0], (Properties)orbProps);
        NamingContext root = NamingContextHelper.narrow((Object)orb.resolve_initial_references("NameService"));
        NameComponent nc = new NameComponent(nameId, nameKind);
        NameComponent[] ncs = new NameComponent[]{nc};
        Object corbaObject = root.resolve(ncs);
        Class helperClass = ClassUtils.forName(helperClassName);
        Method narrowMethod = helperClass.getMethod("narrow", CORBA_OBJECT_CLASS);
        java.lang.Object targetObject = narrowMethod.invoke(null, corbaObject);
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

