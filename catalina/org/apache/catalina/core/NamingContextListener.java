/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.descriptor.web.ContextEjb
 *  org.apache.tomcat.util.descriptor.web.ContextEnvironment
 *  org.apache.tomcat.util.descriptor.web.ContextHandler
 *  org.apache.tomcat.util.descriptor.web.ContextLocalEjb
 *  org.apache.tomcat.util.descriptor.web.ContextResource
 *  org.apache.tomcat.util.descriptor.web.ContextResourceEnvRef
 *  org.apache.tomcat.util.descriptor.web.ContextResourceLink
 *  org.apache.tomcat.util.descriptor.web.ContextService
 *  org.apache.tomcat.util.descriptor.web.ContextTransaction
 *  org.apache.tomcat.util.descriptor.web.MessageDestinationRef
 *  org.apache.tomcat.util.descriptor.web.ResourceBase
 *  org.apache.tomcat.util.modeler.Registry
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.core;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NamingException;
import javax.naming.StringRefAddr;
import org.apache.catalina.ContainerEvent;
import org.apache.catalina.ContainerListener;
import org.apache.catalina.Context;
import org.apache.catalina.Engine;
import org.apache.catalina.Host;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Server;
import org.apache.catalina.core.ContainerBase;
import org.apache.catalina.core.StandardServer;
import org.apache.catalina.deploy.NamingResourcesImpl;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.naming.AbstractRef;
import org.apache.naming.ContextAccessController;
import org.apache.naming.ContextBindings;
import org.apache.naming.EjbRef;
import org.apache.naming.HandlerRef;
import org.apache.naming.LookupRef;
import org.apache.naming.NamingContext;
import org.apache.naming.ResourceEnvRef;
import org.apache.naming.ResourceLinkRef;
import org.apache.naming.ResourceRef;
import org.apache.naming.ServiceRef;
import org.apache.naming.TransactionRef;
import org.apache.naming.factory.ResourceLinkFactory;
import org.apache.tomcat.util.descriptor.web.ContextEjb;
import org.apache.tomcat.util.descriptor.web.ContextEnvironment;
import org.apache.tomcat.util.descriptor.web.ContextHandler;
import org.apache.tomcat.util.descriptor.web.ContextLocalEjb;
import org.apache.tomcat.util.descriptor.web.ContextResource;
import org.apache.tomcat.util.descriptor.web.ContextResourceEnvRef;
import org.apache.tomcat.util.descriptor.web.ContextResourceLink;
import org.apache.tomcat.util.descriptor.web.ContextService;
import org.apache.tomcat.util.descriptor.web.ContextTransaction;
import org.apache.tomcat.util.descriptor.web.MessageDestinationRef;
import org.apache.tomcat.util.descriptor.web.ResourceBase;
import org.apache.tomcat.util.modeler.Registry;
import org.apache.tomcat.util.res.StringManager;

public class NamingContextListener
implements LifecycleListener,
ContainerListener,
PropertyChangeListener {
    private static final Log log = LogFactory.getLog(NamingContextListener.class);
    protected static final StringManager sm = StringManager.getManager(NamingContextListener.class);
    protected String name = "/";
    protected Object container = null;
    private Object token = null;
    protected boolean initialized = false;
    protected NamingResourcesImpl namingResources = null;
    protected NamingContext namingContext = null;
    protected javax.naming.Context compCtx = null;
    protected javax.naming.Context envCtx = null;
    protected HashMap<String, ObjectName> objectNames = new HashMap();
    private boolean exceptionOnFailedWrite = true;

    public boolean getExceptionOnFailedWrite() {
        return this.exceptionOnFailedWrite;
    }

    public void setExceptionOnFailedWrite(boolean exceptionOnFailedWrite) {
        this.exceptionOnFailedWrite = exceptionOnFailedWrite;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public javax.naming.Context getEnvContext() {
        return this.envCtx;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public void lifecycleEvent(LifecycleEvent event) {
        this.container = event.getLifecycle();
        if (this.container instanceof Context) {
            this.namingResources = ((Context)this.container).getNamingResources();
            this.token = ((Context)this.container).getNamingToken();
        } else {
            if (!(this.container instanceof Server)) return;
            this.namingResources = ((Server)this.container).getGlobalNamingResources();
            this.token = ((Server)this.container).getNamingToken();
        }
        if ("configure_start".equals(event.getType())) {
            if (this.initialized) {
                return;
            }
            try {
                Hashtable<String, Object> contextEnv = new Hashtable<String, Object>();
                this.namingContext = new NamingContext(contextEnv, this.getName());
                ContextAccessController.setSecurityToken(this.getName(), this.token);
                ContextAccessController.setSecurityToken(this.container, this.token);
                ContextBindings.bindContext(this.container, this.namingContext, this.token);
                if (log.isDebugEnabled()) {
                    log.debug((Object)("Bound " + this.container));
                }
                this.namingContext.setExceptionOnFailedWrite(this.getExceptionOnFailedWrite());
                ContextAccessController.setWritable(this.getName(), this.token);
                try {
                    this.createNamingContext();
                }
                catch (NamingException e) {
                    log.error((Object)sm.getString("naming.namingContextCreationFailed", new Object[]{e}));
                }
                this.namingResources.addPropertyChangeListener(this);
                if (this.container instanceof Context) {
                    ContextAccessController.setReadOnly(this.getName());
                    try {
                        ContextBindings.bindClassLoader(this.container, this.token, ((Context)this.container).getLoader().getClassLoader());
                    }
                    catch (NamingException e) {
                        log.error((Object)sm.getString("naming.bindFailed", new Object[]{e}));
                    }
                }
                if (!(this.container instanceof Server)) return;
                ResourceLinkFactory.setGlobalContext(this.namingContext);
                try {
                    ContextBindings.bindClassLoader(this.container, this.token, this.getClass().getClassLoader());
                }
                catch (NamingException e) {
                    log.error((Object)sm.getString("naming.bindFailed", new Object[]{e}));
                }
                if (!(this.container instanceof StandardServer)) return;
                ((StandardServer)this.container).setGlobalNamingContext(this.namingContext);
                return;
            }
            finally {
                this.initialized = true;
            }
        }
        if (!"configure_stop".equals(event.getType())) return;
        if (!this.initialized) {
            return;
        }
        try {
            javax.naming.Context global;
            ContextAccessController.setWritable(this.getName(), this.token);
            ContextBindings.unbindContext(this.container, this.token);
            if (this.container instanceof Context) {
                ContextBindings.unbindClassLoader(this.container, this.token, ((Context)this.container).getLoader().getClassLoader());
            }
            if (this.container instanceof Server) {
                ContextBindings.unbindClassLoader(this.container, this.token, this.getClass().getClassLoader());
            }
            this.namingResources.removePropertyChangeListener(this);
            ContextAccessController.unsetSecurityToken(this.getName(), this.token);
            ContextAccessController.unsetSecurityToken(this.container, this.token);
            if (!this.objectNames.isEmpty()) {
                Collection<ObjectName> names = this.objectNames.values();
                Registry registry = Registry.getRegistry(null, null);
                for (ObjectName objectName : names) {
                    registry.unregisterComponent(objectName);
                }
            }
            if ((global = this.getGlobalNamingContext()) == null) return;
            ResourceLinkFactory.deregisterGlobalResourceAccess(global);
            return;
        }
        finally {
            this.objectNames.clear();
            this.namingContext = null;
            this.envCtx = null;
            this.compCtx = null;
            this.initialized = false;
        }
    }

    @Override
    @Deprecated
    public void containerEvent(ContainerEvent event) {
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        if (!this.initialized) {
            return;
        }
        Object source = event.getSource();
        if (source == this.namingResources) {
            ContextAccessController.setWritable(this.getName(), this.token);
            this.processGlobalResourcesChange(event.getPropertyName(), event.getOldValue(), event.getNewValue());
            ContextAccessController.setReadOnly(this.getName());
        }
    }

    private void processGlobalResourcesChange(String name, Object oldValue, Object newValue) {
        if (name.equals("ejb")) {
            ContextEjb ejb;
            if (oldValue != null && (ejb = (ContextEjb)oldValue).getName() != null) {
                this.removeEjb(ejb.getName());
            }
            if (newValue != null && (ejb = (ContextEjb)newValue).getName() != null) {
                this.addEjb(ejb);
            }
        } else if (name.equals("environment")) {
            ContextEnvironment env;
            if (oldValue != null && (env = (ContextEnvironment)oldValue).getName() != null) {
                this.removeEnvironment(env.getName());
            }
            if (newValue != null && (env = (ContextEnvironment)newValue).getName() != null) {
                this.addEnvironment(env);
            }
        } else if (name.equals("localEjb")) {
            ContextLocalEjb ejb;
            if (oldValue != null && (ejb = (ContextLocalEjb)oldValue).getName() != null) {
                this.removeLocalEjb(ejb.getName());
            }
            if (newValue != null && (ejb = (ContextLocalEjb)newValue).getName() != null) {
                this.addLocalEjb(ejb);
            }
        } else if (name.equals("messageDestinationRef")) {
            MessageDestinationRef mdr;
            if (oldValue != null && (mdr = (MessageDestinationRef)oldValue).getName() != null) {
                this.removeMessageDestinationRef(mdr.getName());
            }
            if (newValue != null && (mdr = (MessageDestinationRef)newValue).getName() != null) {
                this.addMessageDestinationRef(mdr);
            }
        } else if (name.equals("resource")) {
            ContextResource resource;
            if (oldValue != null && (resource = (ContextResource)oldValue).getName() != null) {
                this.removeResource(resource.getName());
            }
            if (newValue != null && (resource = (ContextResource)newValue).getName() != null) {
                this.addResource(resource);
            }
        } else if (name.equals("resourceEnvRef")) {
            ContextResourceEnvRef resourceEnvRef;
            if (oldValue != null && (resourceEnvRef = (ContextResourceEnvRef)oldValue).getName() != null) {
                this.removeResourceEnvRef(resourceEnvRef.getName());
            }
            if (newValue != null && (resourceEnvRef = (ContextResourceEnvRef)newValue).getName() != null) {
                this.addResourceEnvRef(resourceEnvRef);
            }
        } else if (name.equals("resourceLink")) {
            ContextResourceLink rl;
            if (oldValue != null && (rl = (ContextResourceLink)oldValue).getName() != null) {
                this.removeResourceLink(rl.getName());
            }
            if (newValue != null && (rl = (ContextResourceLink)newValue).getName() != null) {
                this.addResourceLink(rl);
            }
        } else if (name.equals("service")) {
            ContextService service;
            if (oldValue != null && (service = (ContextService)oldValue).getName() != null) {
                this.removeService(service.getName());
            }
            if (newValue != null && (service = (ContextService)newValue).getName() != null) {
                this.addService(service);
            }
        }
    }

    private void createNamingContext() throws NamingException {
        int i;
        if (this.container instanceof Server) {
            this.compCtx = this.namingContext;
            this.envCtx = this.namingContext;
        } else {
            this.compCtx = this.namingContext.createSubcontext("comp");
            this.envCtx = this.compCtx.createSubcontext("env");
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)"Creating JNDI naming context");
        }
        if (this.namingResources == null) {
            this.namingResources = new NamingResourcesImpl();
            this.namingResources.setContainer(this.container);
        }
        ContextResourceLink[] resourceLinks = this.namingResources.findResourceLinks();
        for (i = 0; i < resourceLinks.length; ++i) {
            this.addResourceLink(resourceLinks[i]);
        }
        ContextResource[] resources = this.namingResources.findResources();
        for (i = 0; i < resources.length; ++i) {
            this.addResource(resources[i]);
        }
        ContextResourceEnvRef[] resourceEnvRefs = this.namingResources.findResourceEnvRefs();
        for (i = 0; i < resourceEnvRefs.length; ++i) {
            this.addResourceEnvRef(resourceEnvRefs[i]);
        }
        ContextEnvironment[] contextEnvironments = this.namingResources.findEnvironments();
        for (i = 0; i < contextEnvironments.length; ++i) {
            this.addEnvironment(contextEnvironments[i]);
        }
        ContextEjb[] ejbs = this.namingResources.findEjbs();
        for (i = 0; i < ejbs.length; ++i) {
            this.addEjb(ejbs[i]);
        }
        MessageDestinationRef[] mdrs = this.namingResources.findMessageDestinationRefs();
        for (i = 0; i < mdrs.length; ++i) {
            this.addMessageDestinationRef(mdrs[i]);
        }
        ContextService[] services = this.namingResources.findServices();
        for (i = 0; i < services.length; ++i) {
            this.addService(services[i]);
        }
        if (this.container instanceof Context) {
            try {
                TransactionRef ref = new TransactionRef();
                this.compCtx.bind("UserTransaction", (Object)ref);
                ContextTransaction transaction = this.namingResources.getTransaction();
                if (transaction != null) {
                    Iterator params = transaction.listProperties();
                    while (params.hasNext()) {
                        String paramName = (String)params.next();
                        String paramValue = (String)transaction.getProperty(paramName);
                        StringRefAddr refAddr = new StringRefAddr(paramName, paramValue);
                        ref.add(refAddr);
                    }
                }
            }
            catch (NameAlreadyBoundException ref) {
            }
            catch (NamingException e) {
                log.error((Object)sm.getString("naming.bindFailed", new Object[]{e}));
            }
        }
        if (this.container instanceof Context) {
            try {
                this.compCtx.bind("Resources", (Object)((Context)this.container).getResources());
            }
            catch (NamingException e) {
                log.error((Object)sm.getString("naming.bindFailed", new Object[]{e}));
            }
        }
    }

    protected ObjectName createObjectName(ContextResource resource) throws MalformedObjectNameException {
        String domain = null;
        if (this.container instanceof StandardServer) {
            domain = ((StandardServer)this.container).getDomain();
        } else if (this.container instanceof ContainerBase) {
            domain = ((ContainerBase)this.container).getDomain();
        }
        if (domain == null) {
            domain = "Catalina";
        }
        ObjectName name = null;
        String quotedResourceName = ObjectName.quote(resource.getName());
        if (this.container instanceof Server) {
            name = new ObjectName(domain + ":type=DataSource,class=" + resource.getType() + ",name=" + quotedResourceName);
        } else if (this.container instanceof Context) {
            String contextName = ((Context)this.container).getName();
            if (!contextName.startsWith("/")) {
                contextName = "/" + contextName;
            }
            Host host = (Host)((Context)this.container).getParent();
            name = new ObjectName(domain + ":type=DataSource,host=" + host.getName() + ",context=" + contextName + ",class=" + resource.getType() + ",name=" + quotedResourceName);
        }
        return name;
    }

    public void addEjb(ContextEjb ejb) {
        AbstractRef ref = this.lookForLookupRef((ResourceBase)ejb);
        if (ref == null) {
            ref = new EjbRef(ejb.getType(), ejb.getHome(), ejb.getRemote(), ejb.getLink());
            Iterator params = ejb.listProperties();
            while (params.hasNext()) {
                String paramName = (String)params.next();
                String paramValue = (String)ejb.getProperty(paramName);
                StringRefAddr refAddr = new StringRefAddr(paramName, paramValue);
                ref.add(refAddr);
            }
        }
        try {
            this.createSubcontexts(this.envCtx, ejb.getName());
            this.envCtx.bind(ejb.getName(), (Object)ref);
        }
        catch (NamingException e) {
            log.error((Object)sm.getString("naming.bindFailed", new Object[]{e}));
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public void addEnvironment(ContextEnvironment env) {
        Object value = this.lookForLookupRef((ResourceBase)env);
        if (value == null) {
            String type = env.getType();
            try {
                if (type.equals("java.lang.String")) {
                    value = env.getValue();
                } else if (type.equals("java.lang.Byte")) {
                    value = env.getValue() == null ? Byte.valueOf((byte)0) : Byte.decode(env.getValue());
                } else if (type.equals("java.lang.Short")) {
                    value = env.getValue() == null ? Short.valueOf((short)0) : Short.decode(env.getValue());
                } else if (type.equals("java.lang.Integer")) {
                    value = env.getValue() == null ? Integer.valueOf(0) : Integer.decode(env.getValue());
                } else if (type.equals("java.lang.Long")) {
                    value = env.getValue() == null ? Long.valueOf(0L) : Long.decode(env.getValue());
                } else if (type.equals("java.lang.Boolean")) {
                    value = Boolean.valueOf(env.getValue());
                } else if (type.equals("java.lang.Double")) {
                    value = env.getValue() == null ? Double.valueOf(0.0) : Double.valueOf(env.getValue());
                } else if (type.equals("java.lang.Float")) {
                    value = env.getValue() == null ? Float.valueOf(0.0f) : Float.valueOf(env.getValue());
                } else if (type.equals("java.lang.Character")) {
                    if (env.getValue() == null) {
                        value = Character.valueOf('\u0000');
                    } else {
                        if (env.getValue().length() != 1) throw new IllegalArgumentException();
                        value = Character.valueOf(env.getValue().charAt(0));
                    }
                } else {
                    value = this.constructEnvEntry(env.getType(), env.getValue());
                    if (value == null) {
                        log.error((Object)sm.getString("naming.invalidEnvEntryType", new Object[]{env.getName()}));
                    }
                }
            }
            catch (IllegalArgumentException e) {
                log.error((Object)sm.getString("naming.invalidEnvEntryValue", new Object[]{env.getName()}));
            }
        }
        if (value == null) return;
        try {
            if (log.isDebugEnabled()) {
                log.debug((Object)sm.getString("naming.addEnvEntry", new Object[]{env.getName()}));
            }
            this.createSubcontexts(this.envCtx, env.getName());
            this.envCtx.bind(env.getName(), value);
            return;
        }
        catch (NamingException e) {
            log.error((Object)sm.getString("naming.invalidEnvEntryValue", new Object[]{e}));
        }
    }

    private Object constructEnvEntry(String type, String value) {
        try {
            Class<?> clazz = Class.forName(type);
            Constructor<?> c = null;
            try {
                c = clazz.getConstructor(String.class);
                return c.newInstance(value);
            }
            catch (NoSuchMethodException noSuchMethodException) {
                if (value.length() != 1) {
                    return null;
                }
                try {
                    c = clazz.getConstructor(Character.TYPE);
                    return c.newInstance(Character.valueOf(value.charAt(0)));
                }
                catch (NoSuchMethodException noSuchMethodException2) {
                }
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return null;
    }

    public void addLocalEjb(ContextLocalEjb localEjb) {
    }

    public void addMessageDestinationRef(MessageDestinationRef mdr) {
    }

    public void addService(ContextService service) {
        AbstractRef ref = this.lookForLookupRef((ResourceBase)service);
        if (ref == null) {
            if (service.getWsdlfile() != null) {
                URL wsdlURL = null;
                try {
                    URI wsdlURI = new URI(service.getWsdlfile());
                    wsdlURL = wsdlURI.toURL();
                }
                catch (IllegalArgumentException | MalformedURLException | URISyntaxException wsdlURI) {
                    // empty catch block
                }
                if (wsdlURL == null) {
                    try {
                        wsdlURL = ((Context)this.container).getServletContext().getResource(service.getWsdlfile());
                    }
                    catch (MalformedURLException wsdlURI) {
                        // empty catch block
                    }
                }
                if (wsdlURL == null) {
                    try {
                        wsdlURL = ((Context)this.container).getServletContext().getResource("/" + service.getWsdlfile());
                        log.debug((Object)("  Changing service ref wsdl file for /" + service.getWsdlfile()));
                    }
                    catch (MalformedURLException e) {
                        log.error((Object)sm.getString("naming.wsdlFailed", new Object[]{e}));
                    }
                }
                if (wsdlURL == null) {
                    service.setWsdlfile(null);
                } else {
                    service.setWsdlfile(wsdlURL.toString());
                }
            }
            if (service.getJaxrpcmappingfile() != null) {
                URL jaxrpcURL = null;
                try {
                    URI jaxrpcURI = new URI(service.getJaxrpcmappingfile());
                    jaxrpcURL = jaxrpcURI.toURL();
                }
                catch (IllegalArgumentException | MalformedURLException | URISyntaxException jaxrpcURI) {
                    // empty catch block
                }
                if (jaxrpcURL == null) {
                    try {
                        jaxrpcURL = ((Context)this.container).getServletContext().getResource(service.getJaxrpcmappingfile());
                    }
                    catch (MalformedURLException jaxrpcURI) {
                        // empty catch block
                    }
                }
                if (jaxrpcURL == null) {
                    try {
                        jaxrpcURL = ((Context)this.container).getServletContext().getResource("/" + service.getJaxrpcmappingfile());
                        log.debug((Object)("  Changing service ref jaxrpc file for /" + service.getJaxrpcmappingfile()));
                    }
                    catch (MalformedURLException e) {
                        log.error((Object)sm.getString("naming.wsdlFailed", new Object[]{e}));
                    }
                }
                if (jaxrpcURL == null) {
                    service.setJaxrpcmappingfile(null);
                } else {
                    service.setJaxrpcmappingfile(jaxrpcURL.toString());
                }
            }
            ref = new ServiceRef(service.getName(), service.getInterface(), service.getServiceqname(), service.getWsdlfile(), service.getJaxrpcmappingfile());
            Iterator portcomponent = service.getServiceendpoints();
            while (portcomponent.hasNext()) {
                String serviceendpoint = (String)portcomponent.next();
                StringRefAddr refAddr = new StringRefAddr("serviceendpointinterface", serviceendpoint);
                ref.add(refAddr);
                String portlink = service.getPortlink(serviceendpoint);
                refAddr = new StringRefAddr("portcomponentlink", portlink);
                ref.add(refAddr);
            }
            Iterator handlers = service.getHandlers();
            while (handlers.hasNext()) {
                int i;
                String handlername = (String)handlers.next();
                ContextHandler handler = service.getHandler(handlername);
                HandlerRef handlerRef = new HandlerRef(handlername, handler.getHandlerclass());
                Iterator localParts = handler.getLocalparts();
                while (localParts.hasNext()) {
                    String localPart = (String)localParts.next();
                    String namespaceURI = handler.getNamespaceuri(localPart);
                    handlerRef.add(new StringRefAddr("handlerlocalpart", localPart));
                    handlerRef.add(new StringRefAddr("handlernamespace", namespaceURI));
                }
                Iterator params = handler.listProperties();
                while (params.hasNext()) {
                    String paramName = (String)params.next();
                    String paramValue = (String)handler.getProperty(paramName);
                    handlerRef.add(new StringRefAddr("handlerparamname", paramName));
                    handlerRef.add(new StringRefAddr("handlerparamvalue", paramValue));
                }
                for (i = 0; i < handler.getSoapRolesSize(); ++i) {
                    handlerRef.add(new StringRefAddr("handlersoaprole", handler.getSoapRole(i)));
                }
                for (i = 0; i < handler.getPortNamesSize(); ++i) {
                    handlerRef.add(new StringRefAddr("handlerportname", handler.getPortName(i)));
                }
                ((ServiceRef)ref).addHandler(handlerRef);
            }
        }
        try {
            if (log.isDebugEnabled()) {
                log.debug((Object)("  Adding service ref " + service.getName() + "  " + ref));
            }
            this.createSubcontexts(this.envCtx, service.getName());
            this.envCtx.bind(service.getName(), (Object)ref);
        }
        catch (NamingException e) {
            log.error((Object)sm.getString("naming.bindFailed", new Object[]{e}));
        }
    }

    public void addResource(ContextResource resource) {
        AbstractRef ref = this.lookForLookupRef((ResourceBase)resource);
        if (ref == null) {
            ref = new ResourceRef(resource.getType(), resource.getDescription(), resource.getScope(), resource.getAuth(), resource.getSingleton());
            Iterator params = resource.listProperties();
            while (params.hasNext()) {
                String paramName = (String)params.next();
                String paramValue = (String)resource.getProperty(paramName);
                StringRefAddr refAddr = new StringRefAddr(paramName, paramValue);
                ref.add(refAddr);
            }
        }
        try {
            if (log.isDebugEnabled()) {
                log.debug((Object)("  Adding resource ref " + resource.getName() + "  " + ref));
            }
            this.createSubcontexts(this.envCtx, resource.getName());
            this.envCtx.bind(resource.getName(), (Object)ref);
        }
        catch (NamingException e) {
            log.error((Object)sm.getString("naming.bindFailed", new Object[]{e}));
        }
        if (("javax.sql.DataSource".equals(ref.getClassName()) || "javax.sql.XADataSource".equals(ref.getClassName())) && resource.getSingleton()) {
            Object actualResource = null;
            try {
                ObjectName on = this.createObjectName(resource);
                actualResource = this.envCtx.lookup(resource.getName());
                Registry.getRegistry(null, null).registerComponent(actualResource, on, null);
                this.objectNames.put(resource.getName(), on);
            }
            catch (Exception e) {
                log.warn((Object)sm.getString("naming.jmxRegistrationFailed", new Object[]{e}));
            }
            if (actualResource instanceof AutoCloseable && !resource.getCloseMethodConfigured()) {
                resource.setCloseMethod("close");
            }
        }
    }

    public void addResourceEnvRef(ContextResourceEnvRef resourceEnvRef) {
        AbstractRef ref = this.lookForLookupRef((ResourceBase)resourceEnvRef);
        if (ref == null) {
            ref = new ResourceEnvRef(resourceEnvRef.getType());
            Iterator params = resourceEnvRef.listProperties();
            while (params.hasNext()) {
                String paramName = (String)params.next();
                String paramValue = (String)resourceEnvRef.getProperty(paramName);
                StringRefAddr refAddr = new StringRefAddr(paramName, paramValue);
                ref.add(refAddr);
            }
        }
        try {
            if (log.isDebugEnabled()) {
                log.debug((Object)sm.getString("naming.addResourceEnvRef", new Object[]{resourceEnvRef.getName()}));
            }
            this.createSubcontexts(this.envCtx, resourceEnvRef.getName());
            this.envCtx.bind(resourceEnvRef.getName(), (Object)ref);
        }
        catch (NamingException e) {
            log.error((Object)sm.getString("naming.bindFailed", new Object[]{e}));
        }
    }

    public void addResourceLink(ContextResourceLink resourceLink) {
        ResourceLinkRef ref = new ResourceLinkRef(resourceLink.getType(), resourceLink.getGlobal(), resourceLink.getFactory(), null);
        Iterator i = resourceLink.listProperties();
        while (i.hasNext()) {
            String key = (String)i.next();
            Object val = resourceLink.getProperty(key);
            if (val == null) continue;
            StringRefAddr refAddr = new StringRefAddr(key, val.toString());
            ref.add(refAddr);
        }
        javax.naming.Context ctx = "UserTransaction".equals(resourceLink.getName()) ? this.compCtx : this.envCtx;
        try {
            if (log.isDebugEnabled()) {
                log.debug((Object)("  Adding resource link " + resourceLink.getName()));
            }
            this.createSubcontexts(this.envCtx, resourceLink.getName());
            ctx.bind(resourceLink.getName(), (Object)ref);
        }
        catch (NamingException e) {
            log.error((Object)sm.getString("naming.bindFailed", new Object[]{e}));
        }
        ResourceLinkFactory.registerGlobalResourceAccess(this.getGlobalNamingContext(), resourceLink.getName(), resourceLink.getGlobal());
    }

    private javax.naming.Context getGlobalNamingContext() {
        Engine e;
        Server s;
        if (this.container instanceof Context && (s = (e = (Engine)((Context)this.container).getParent().getParent()).getService().getServer()) != null) {
            return s.getGlobalNamingContext();
        }
        return null;
    }

    public void removeEjb(String name) {
        try {
            this.envCtx.unbind(name);
        }
        catch (NamingException e) {
            log.error((Object)sm.getString("naming.unbindFailed", new Object[]{name}), (Throwable)e);
        }
    }

    public void removeEnvironment(String name) {
        try {
            this.envCtx.unbind(name);
        }
        catch (NamingException e) {
            log.error((Object)sm.getString("naming.unbindFailed", new Object[]{name}), (Throwable)e);
        }
    }

    public void removeLocalEjb(String name) {
        try {
            this.envCtx.unbind(name);
        }
        catch (NamingException e) {
            log.error((Object)sm.getString("naming.unbindFailed", new Object[]{name}), (Throwable)e);
        }
    }

    public void removeMessageDestinationRef(String name) {
        try {
            this.envCtx.unbind(name);
        }
        catch (NamingException e) {
            log.error((Object)sm.getString("naming.unbindFailed", new Object[]{name}), (Throwable)e);
        }
    }

    public void removeService(String name) {
        try {
            this.envCtx.unbind(name);
        }
        catch (NamingException e) {
            log.error((Object)sm.getString("naming.unbindFailed", new Object[]{name}), (Throwable)e);
        }
    }

    public void removeResource(String name) {
        try {
            this.envCtx.unbind(name);
        }
        catch (NamingException e) {
            log.error((Object)sm.getString("naming.unbindFailed", new Object[]{name}), (Throwable)e);
        }
        ObjectName on = this.objectNames.get(name);
        if (on != null) {
            Registry.getRegistry(null, null).unregisterComponent(on);
        }
    }

    public void removeResourceEnvRef(String name) {
        try {
            this.envCtx.unbind(name);
        }
        catch (NamingException e) {
            log.error((Object)sm.getString("naming.unbindFailed", new Object[]{name}), (Throwable)e);
        }
    }

    public void removeResourceLink(String name) {
        try {
            this.envCtx.unbind(name);
        }
        catch (NamingException e) {
            log.error((Object)sm.getString("naming.unbindFailed", new Object[]{name}), (Throwable)e);
        }
        ResourceLinkFactory.deregisterGlobalResourceAccess(this.getGlobalNamingContext(), name);
    }

    private void createSubcontexts(javax.naming.Context ctx, String name) throws NamingException {
        javax.naming.Context currentContext = ctx;
        StringTokenizer tokenizer = new StringTokenizer(name, "/");
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            if (token.equals("") || !tokenizer.hasMoreTokens()) continue;
            try {
                currentContext = currentContext.createSubcontext(token);
            }
            catch (NamingException e) {
                currentContext = (javax.naming.Context)currentContext.lookup(token);
            }
        }
    }

    private LookupRef lookForLookupRef(ResourceBase resourceBase) {
        String lookupName = resourceBase.getLookupName();
        if (lookupName != null && !lookupName.equals("")) {
            return new LookupRef(resourceBase.getType(), lookupName);
        }
        return null;
    }
}

