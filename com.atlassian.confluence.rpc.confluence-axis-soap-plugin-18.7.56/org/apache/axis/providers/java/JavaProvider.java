/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.wsdl.OperationType
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.providers.java;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.StringTokenizer;
import javax.wsdl.OperationType;
import javax.xml.rpc.holders.IntHolder;
import javax.xml.rpc.server.ServiceLifecycle;
import org.apache.axis.AxisEngine;
import org.apache.axis.AxisFault;
import org.apache.axis.Constants;
import org.apache.axis.Handler;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.constants.Scope;
import org.apache.axis.description.JavaServiceDesc;
import org.apache.axis.description.OperationDesc;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.providers.BasicProvider;
import org.apache.axis.session.Session;
import org.apache.axis.utils.ClassUtils;
import org.apache.axis.utils.Messages;
import org.apache.axis.utils.XMLUtils;
import org.apache.axis.utils.cache.ClassCache;
import org.apache.axis.utils.cache.JavaClass;
import org.apache.commons.logging.Log;
import org.xml.sax.SAXException;

public abstract class JavaProvider
extends BasicProvider {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$providers$java$JavaProvider == null ? (class$org$apache$axis$providers$java$JavaProvider = JavaProvider.class$("org.apache.axis.providers.java.JavaProvider")) : class$org$apache$axis$providers$java$JavaProvider).getName());
    protected static Log entLog = LogFactory.getLog("org.apache.axis.enterprise");
    public static final String OPTION_CLASSNAME = "className";
    public static final String OPTION_ALLOWEDMETHODS = "allowedMethods";
    public static final String OPTION_SCOPE = "scope";
    static /* synthetic */ Class class$org$apache$axis$providers$java$JavaProvider;
    static /* synthetic */ Class class$org$apache$axis$providers$java$JavaProvider$LockObject;

    public Object getServiceObject(MessageContext msgContext, Handler service, String clsName, IntHolder scopeHolder) throws Exception {
        String serviceName = msgContext.getService().getName();
        Scope scope = Scope.getScope((String)service.getOption(OPTION_SCOPE), Scope.DEFAULT);
        scopeHolder.value = scope.getValue();
        if (scope == Scope.REQUEST) {
            return this.getNewServiceObject(msgContext, clsName);
        }
        if (scope == Scope.SESSION) {
            Session session;
            if (serviceName == null) {
                serviceName = msgContext.getService().toString();
            }
            if ((session = msgContext.getSession()) != null) {
                return this.getSessionServiceObject(session, serviceName, msgContext, clsName);
            }
            scopeHolder.value = Scope.DEFAULT.getValue();
            return this.getNewServiceObject(msgContext, clsName);
        }
        if (scope == Scope.APPLICATION) {
            return this.getApplicationScopedObject(msgContext, serviceName, clsName, scopeHolder);
        }
        if (scope == Scope.FACTORY) {
            String objectID = msgContext.getStrProp("objectID");
            if (objectID == null) {
                return this.getApplicationScopedObject(msgContext, serviceName, clsName, scopeHolder);
            }
            SOAPService svc = (SOAPService)service;
            Object ret = svc.serviceObjects.get(objectID);
            if (ret == null) {
                throw new AxisFault("NoSuchObject", null, null, null);
            }
            return ret;
        }
        return null;
    }

    private Object getApplicationScopedObject(MessageContext msgContext, String serviceName, String clsName, IntHolder scopeHolder) throws Exception {
        AxisEngine engine = msgContext.getAxisEngine();
        Session appSession = engine.getApplicationSession();
        if (appSession != null) {
            return this.getSessionServiceObject(appSession, serviceName, msgContext, clsName);
        }
        log.error((Object)Messages.getMessage("noAppSession"));
        scopeHolder.value = Scope.DEFAULT.getValue();
        return this.getNewServiceObject(msgContext, clsName);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Object getSessionServiceObject(Session session, String serviceName, MessageContext msgContext, String clsName) throws Exception {
        Object obj = null;
        boolean makeNewObject = false;
        Object object = session.getLockObject();
        synchronized (object) {
            obj = session.get(serviceName);
            if (obj == null) {
                obj = new LockObject();
                makeNewObject = true;
                session.set(serviceName, obj);
                msgContext.getService().addSession(session);
            }
        }
        if ((class$org$apache$axis$providers$java$JavaProvider$LockObject == null ? (class$org$apache$axis$providers$java$JavaProvider$LockObject = JavaProvider.class$("org.apache.axis.providers.java.JavaProvider$LockObject")) : class$org$apache$axis$providers$java$JavaProvider$LockObject) == obj.getClass()) {
            LockObject lock = (LockObject)obj;
            if (makeNewObject) {
                try {
                    obj = this.getNewServiceObject(msgContext, clsName);
                    session.set(serviceName, obj);
                    msgContext.getService().addSession(session);
                }
                catch (Exception e) {
                    session.remove(serviceName);
                    throw e;
                }
                finally {
                    lock.complete();
                }
            } else {
                lock.waitUntilComplete();
                obj = session.get(serviceName);
            }
        }
        return obj;
    }

    private Object getNewServiceObject(MessageContext msgContext, String clsName) throws Exception {
        Object serviceObject = this.makeNewServiceObject(msgContext, clsName);
        if (serviceObject != null && serviceObject instanceof ServiceLifecycle) {
            ((ServiceLifecycle)serviceObject).init(msgContext.getProperty("servletEndpointContext"));
        }
        return serviceObject;
    }

    public abstract void processMessage(MessageContext var1, SOAPEnvelope var2, SOAPEnvelope var3, Object var4) throws Exception;

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public void invoke(MessageContext msgContext) throws AxisFault {
        block16: {
            if (log.isDebugEnabled()) {
                log.debug((Object)("Enter: JavaProvider::invoke (" + this + ")"));
            }
            String serviceName = msgContext.getTargetService();
            SOAPService service = msgContext.getService();
            String clsName = this.getServiceClassName(service);
            if (clsName == null || clsName.equals("")) {
                throw new AxisFault("Server.NoClassForService", Messages.getMessage("noOption00", this.getServiceClassNameOptionName(), serviceName), null, null);
            }
            IntHolder scope = new IntHolder();
            Object serviceObject = null;
            try {
                try {
                    serviceObject = this.getServiceObject(msgContext, service, clsName, scope);
                    SOAPEnvelope resEnv = null;
                    OperationDesc operation = msgContext.getOperation();
                    if (operation != null && OperationType.ONE_WAY.equals(operation.getMep())) {
                        msgContext.setResponseMessage(null);
                    } else {
                        Message resMsg = msgContext.getResponseMessage();
                        if (resMsg == null) {
                            resEnv = new SOAPEnvelope(msgContext.getSOAPConstants(), msgContext.getSchemaVersion());
                            resMsg = new Message(resEnv);
                            String encoding = XMLUtils.getEncoding(msgContext);
                            resMsg.setProperty("javax.xml.soap.character-set-encoding", encoding);
                            msgContext.setResponseMessage(resMsg);
                        } else {
                            resEnv = resMsg.getSOAPEnvelope();
                        }
                    }
                    Message reqMsg = msgContext.getRequestMessage();
                    SOAPEnvelope reqEnv = reqMsg.getSOAPEnvelope();
                    this.processMessage(msgContext, reqEnv, resEnv, serviceObject);
                }
                catch (SAXException exp) {
                    entLog.debug((Object)Messages.getMessage("toAxisFault00"), (Throwable)exp);
                    Exception real = exp.getException();
                    if (real == null) {
                        real = exp;
                    }
                    throw AxisFault.makeFault(real);
                }
                catch (Exception exp) {
                    entLog.debug((Object)Messages.getMessage("toAxisFault00"), (Throwable)exp);
                    AxisFault fault = AxisFault.makeFault(exp);
                    if (exp instanceof RuntimeException) {
                        fault.addFaultDetail(Constants.QNAME_FAULTDETAIL_RUNTIMEEXCEPTION, "true");
                    }
                    throw fault;
                }
                Object var12_15 = null;
                if (serviceObject == null) break block16;
            }
            catch (Throwable throwable) {
                Object var12_16 = null;
                if (serviceObject != null && scope.value == Scope.REQUEST.getValue() && serviceObject instanceof ServiceLifecycle) {
                    ((ServiceLifecycle)serviceObject).destroy();
                }
                throw throwable;
            }
            if (scope.value == Scope.REQUEST.getValue() && serviceObject instanceof ServiceLifecycle) {
                ((ServiceLifecycle)serviceObject).destroy();
            }
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)("Exit: JavaProvider::invoke (" + this + ")"));
        }
    }

    private String getAllowedMethods(Handler service) {
        String val = (String)service.getOption(OPTION_ALLOWEDMETHODS);
        if (val == null || val.length() == 0) {
            val = (String)service.getOption("methodName");
        }
        return val;
    }

    protected Object makeNewServiceObject(MessageContext msgContext, String clsName) throws Exception {
        ClassLoader cl = msgContext.getClassLoader();
        ClassCache cache = msgContext.getAxisEngine().getClassCache();
        JavaClass jc = cache.lookup(clsName, cl);
        return jc.getJavaClass().newInstance();
    }

    protected String getServiceClassName(Handler service) {
        return (String)service.getOption(this.getServiceClassNameOptionName());
    }

    protected String getServiceClassNameOptionName() {
        return OPTION_CLASSNAME;
    }

    protected Class getServiceClass(String clsName, SOAPService service, MessageContext msgContext) throws AxisFault {
        ClassLoader cl = null;
        Class serviceClass = null;
        AxisEngine engine = service.getEngine();
        cl = msgContext != null ? msgContext.getClassLoader() : Thread.currentThread().getContextClassLoader();
        if (engine != null) {
            ClassCache cache = engine.getClassCache();
            try {
                JavaClass jc = cache.lookup(clsName, cl);
                serviceClass = jc.getJavaClass();
            }
            catch (ClassNotFoundException e) {
                log.error((Object)Messages.getMessage("exception00"), (Throwable)e);
                throw new AxisFault(Messages.getMessage("noClassForService00", clsName), e);
            }
        }
        try {
            serviceClass = ClassUtils.forName(clsName, true, cl);
        }
        catch (ClassNotFoundException e) {
            log.error((Object)Messages.getMessage("exception00"), (Throwable)e);
            throw new AxisFault(Messages.getMessage("noClassForService00", clsName), e);
        }
        return serviceClass;
    }

    public void initServiceDesc(SOAPService service, MessageContext msgContext) throws AxisFault {
        String allowedMethods;
        String clsName = this.getServiceClassName(service);
        if (clsName == null) {
            throw new AxisFault(Messages.getMessage("noServiceClass"));
        }
        Class cls = this.getServiceClass(clsName, service, msgContext);
        JavaServiceDesc serviceDescription = (JavaServiceDesc)service.getServiceDescription();
        if (serviceDescription.getAllowedMethods() == null && service != null && (allowedMethods = this.getAllowedMethods(service)) != null && !"*".equals(allowedMethods)) {
            ArrayList<String> methodList = new ArrayList<String>();
            StringTokenizer tokenizer = new StringTokenizer(allowedMethods, " ,");
            while (tokenizer.hasMoreTokens()) {
                methodList.add(tokenizer.nextToken());
            }
            serviceDescription.setAllowedMethods(methodList);
        }
        serviceDescription.loadServiceDescByIntrospection(cls);
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    class LockObject
    implements Serializable {
        private boolean completed = false;

        LockObject() {
        }

        synchronized void waitUntilComplete() throws InterruptedException {
            while (!this.completed) {
                this.wait();
            }
        }

        synchronized void complete() {
            this.completed = true;
            this.notifyAll();
        }
    }
}

