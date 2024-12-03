/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  javax.xml.ws.WebServiceFeature
 *  org.glassfish.external.amx.AMXGlassfish
 *  org.glassfish.gmbal.Description
 *  org.glassfish.gmbal.GmbalMBean
 *  org.glassfish.gmbal.InheritedAttributes
 *  org.glassfish.gmbal.ManagedData
 *  org.glassfish.gmbal.ManagedObjectManager
 *  org.glassfish.gmbal.ManagedObjectManager$RegistrationDebugLevel
 *  org.glassfish.gmbal.ManagedObjectManagerFactory
 */
package com.sun.xml.ws.server;

import com.sun.istack.NotNull;
import com.sun.xml.ws.api.EndpointAddress;
import com.sun.xml.ws.api.config.management.policy.ManagedClientAssertion;
import com.sun.xml.ws.api.config.management.policy.ManagedServiceAssertion;
import com.sun.xml.ws.api.config.management.policy.ManagementAssertion;
import com.sun.xml.ws.api.server.Container;
import com.sun.xml.ws.api.server.WSEndpoint;
import com.sun.xml.ws.client.Stub;
import com.sun.xml.ws.server.DummyWebServiceFeature;
import com.sun.xml.ws.server.RewritingMOM;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.ObjectName;
import javax.xml.ws.WebServiceFeature;
import org.glassfish.external.amx.AMXGlassfish;
import org.glassfish.gmbal.Description;
import org.glassfish.gmbal.GmbalMBean;
import org.glassfish.gmbal.InheritedAttributes;
import org.glassfish.gmbal.ManagedData;
import org.glassfish.gmbal.ManagedObjectManager;
import org.glassfish.gmbal.ManagedObjectManagerFactory;

public abstract class MonitorBase {
    private static final Logger logger = Logger.getLogger("com.sun.xml.ws.monitoring");
    private static ManagementAssertion.Setting clientMonitoring = ManagementAssertion.Setting.NOT_SET;
    private static ManagementAssertion.Setting endpointMonitoring = ManagementAssertion.Setting.NOT_SET;
    private static int typelibDebug = -1;
    private static String registrationDebug = "NONE";
    private static boolean runtimeDebug = false;
    private static int maxUniqueEndpointRootNameRetries = 100;
    private static final String monitorProperty = "com.sun.xml.ws.monitoring.";

    @NotNull
    public ManagedObjectManager createManagedObjectManager(WSEndpoint endpoint) {
        ManagedServiceAssertion assertion;
        String contextPath;
        String rootName = endpoint.getServiceName().getLocalPart() + "-" + endpoint.getPortName().getLocalPart();
        if (rootName.equals("-")) {
            rootName = "provider";
        }
        if ((contextPath = this.getContextPath(endpoint)) != null) {
            rootName = contextPath + "-" + rootName;
        }
        if ((assertion = ManagedServiceAssertion.getAssertion(endpoint)) != null) {
            String id = assertion.getId();
            if (id != null) {
                rootName = id;
            }
            if (assertion.monitoringAttribute() == ManagementAssertion.Setting.OFF) {
                return this.disabled("This endpoint", rootName);
            }
        }
        if (endpointMonitoring.equals((Object)ManagementAssertion.Setting.OFF)) {
            return this.disabled("Global endpoint", rootName);
        }
        return this.createMOMLoop(rootName, 0);
    }

    private String getContextPath(WSEndpoint endpoint) {
        try {
            Container container = endpoint.getContainer();
            try {
                Object servletContext;
                Class<?> servletContextClass = Class.forName("javax.servlet.ServletContext");
                if (servletContextClass != null && (servletContext = container.getSPI(servletContextClass)) != null) {
                    Method getContextPath = servletContextClass.getDeclaredMethod("getContextPath", new Class[0]);
                    return (String)getContextPath.invoke(servletContext, new Object[0]);
                }
            }
            catch (ClassNotFoundException cnfe) {
                logger.log(Level.FINEST, "Class {0} not found", cnfe.getMessage());
                return null;
            }
        }
        catch (Throwable t) {
            logger.log(Level.FINEST, "getContextPath", t);
        }
        return null;
    }

    @NotNull
    public ManagedObjectManager createManagedObjectManager(Stub stub) {
        EndpointAddress ea = stub.requestContext.getEndpointAddress();
        if (ea == null) {
            return ManagedObjectManagerFactory.createNOOP();
        }
        String rootName = ea.toString();
        ManagedClientAssertion assertion = ManagedClientAssertion.getAssertion(stub.getPortInfo());
        if (assertion != null) {
            String id = assertion.getId();
            if (id != null) {
                rootName = id;
            }
            if (assertion.monitoringAttribute() == ManagementAssertion.Setting.OFF) {
                return this.disabled("This client", rootName);
            }
            if (assertion.monitoringAttribute() == ManagementAssertion.Setting.ON && clientMonitoring != ManagementAssertion.Setting.OFF) {
                return this.createMOMLoop(rootName, 0);
            }
        }
        if (clientMonitoring == ManagementAssertion.Setting.NOT_SET || clientMonitoring == ManagementAssertion.Setting.OFF) {
            return this.disabled("Global client", rootName);
        }
        return this.createMOMLoop(rootName, 0);
    }

    @NotNull
    private ManagedObjectManager disabled(String x, String rootName) {
        String msg = x + " monitoring disabled. " + rootName + " will not be monitored";
        logger.log(Level.CONFIG, msg);
        return ManagedObjectManagerFactory.createNOOP();
    }

    @NotNull
    private ManagedObjectManager createMOMLoop(String rootName, int unique) {
        boolean isFederated = AMXGlassfish.getGlassfishVersion() != null;
        ManagedObjectManager mom = this.createMOM(isFederated);
        mom = this.initMOM(mom);
        mom = this.createRoot(mom, rootName, unique);
        return mom;
    }

    @NotNull
    private ManagedObjectManager createMOM(boolean isFederated) {
        try {
            return new RewritingMOM(isFederated ? ManagedObjectManagerFactory.createFederated((ObjectName)AMXGlassfish.DEFAULT.serverMon(AMXGlassfish.DEFAULT.dasName())) : ManagedObjectManagerFactory.createStandalone((String)"com.sun.metro"));
        }
        catch (Throwable t) {
            if (isFederated) {
                logger.log(Level.CONFIG, "Problem while attempting to federate with GlassFish AMX monitoring.  Trying standalone.", t);
                return this.createMOM(false);
            }
            logger.log(Level.WARNING, "Ignoring exception - starting up without monitoring", t);
            return ManagedObjectManagerFactory.createNOOP();
        }
    }

    @NotNull
    private ManagedObjectManager initMOM(ManagedObjectManager mom) {
        try {
            if (typelibDebug != -1) {
                mom.setTypelibDebug(typelibDebug);
            }
            if (registrationDebug.equals("FINE")) {
                mom.setRegistrationDebug(ManagedObjectManager.RegistrationDebugLevel.FINE);
            } else if (registrationDebug.equals("NORMAL")) {
                mom.setRegistrationDebug(ManagedObjectManager.RegistrationDebugLevel.NORMAL);
            } else {
                mom.setRegistrationDebug(ManagedObjectManager.RegistrationDebugLevel.NONE);
            }
            mom.setRuntimeDebug(runtimeDebug);
            mom.suppressDuplicateRootReport(true);
            mom.stripPrefix(new String[]{"com.sun.xml.ws.server", "com.sun.xml.ws.rx.rm.runtime.sequence"});
            mom.addAnnotation(WebServiceFeature.class, (Annotation)DummyWebServiceFeature.class.getAnnotation(ManagedData.class));
            mom.addAnnotation(WebServiceFeature.class, (Annotation)DummyWebServiceFeature.class.getAnnotation(Description.class));
            mom.addAnnotation(WebServiceFeature.class, (Annotation)DummyWebServiceFeature.class.getAnnotation(InheritedAttributes.class));
            mom.suspendJMXRegistration();
        }
        catch (Throwable t) {
            try {
                mom.close();
            }
            catch (IOException e) {
                logger.log(Level.CONFIG, "Ignoring exception caught when closing unused ManagedObjectManager", e);
            }
            logger.log(Level.WARNING, "Ignoring exception - starting up without monitoring", t);
            return ManagedObjectManagerFactory.createNOOP();
        }
        return mom;
    }

    private ManagedObjectManager createRoot(ManagedObjectManager mom, String rootName, int unique) {
        String name = rootName + (unique == 0 ? "" : "-" + String.valueOf(unique));
        try {
            GmbalMBean ignored = mom.createRoot((Object)this, name);
            if (ignored != null) {
                ObjectName ignoredName = mom.getObjectName(mom.getRoot());
                if (ignoredName != null) {
                    logger.log(Level.INFO, "Metro monitoring rootname successfully set to: {0}", ignoredName);
                }
                return mom;
            }
            try {
                mom.close();
            }
            catch (IOException e) {
                logger.log(Level.CONFIG, "Ignoring exception caught when closing unused ManagedObjectManager", e);
            }
            String basemsg = "Duplicate Metro monitoring rootname: " + name + " : ";
            if (unique > maxUniqueEndpointRootNameRetries) {
                String msg = basemsg + "Giving up.";
                logger.log(Level.INFO, msg);
                return ManagedObjectManagerFactory.createNOOP();
            }
            String msg = basemsg + "Will try to make unique";
            logger.log(Level.CONFIG, msg);
            return this.createMOMLoop(rootName, ++unique);
        }
        catch (Throwable t) {
            logger.log(Level.WARNING, "Error while creating monitoring root with name: " + rootName, t);
            return ManagedObjectManagerFactory.createNOOP();
        }
    }

    private static ManagementAssertion.Setting propertyToSetting(String propName) {
        String s = System.getProperty(propName);
        if (s == null) {
            return ManagementAssertion.Setting.NOT_SET;
        }
        if ((s = s.toLowerCase()).equals("false") || s.equals("off")) {
            return ManagementAssertion.Setting.OFF;
        }
        if (s.equals("true") || s.equals("on")) {
            return ManagementAssertion.Setting.ON;
        }
        return ManagementAssertion.Setting.NOT_SET;
    }

    static {
        try {
            String s;
            endpointMonitoring = MonitorBase.propertyToSetting("com.sun.xml.ws.monitoring.endpoint");
            clientMonitoring = MonitorBase.propertyToSetting("com.sun.xml.ws.monitoring.client");
            Integer i = Integer.getInteger("com.sun.xml.ws.monitoring.typelibDebug");
            if (i != null) {
                typelibDebug = i;
            }
            if ((s = System.getProperty("com.sun.xml.ws.monitoring.registrationDebug")) != null) {
                registrationDebug = s.toUpperCase();
            }
            if ((s = System.getProperty("com.sun.xml.ws.monitoring.runtimeDebug")) != null && s.toLowerCase().equals("true")) {
                runtimeDebug = true;
            }
            if ((i = Integer.getInteger("com.sun.xml.ws.monitoring.maxUniqueEndpointRootNameRetries")) != null) {
                maxUniqueEndpointRootNameRetries = i;
            }
        }
        catch (Exception e) {
            logger.log(Level.WARNING, "Error while reading monitoring properties", e);
        }
    }
}

