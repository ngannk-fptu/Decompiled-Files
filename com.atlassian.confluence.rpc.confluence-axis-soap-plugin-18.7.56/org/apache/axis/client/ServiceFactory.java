/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.client;

import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;
import javax.xml.namespace.QName;
import javax.xml.rpc.ServiceException;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.client.Service;
import org.apache.axis.configuration.EngineConfigurationFactoryFinder;
import org.apache.axis.utils.ClassUtils;
import org.apache.axis.utils.Messages;

public class ServiceFactory
extends javax.xml.rpc.ServiceFactory
implements ObjectFactory {
    public static final String SERVICE_CLASSNAME = "service classname";
    public static final String WSDL_LOCATION = "WSDL location";
    public static final String MAINTAIN_SESSION = "maintain session";
    public static final String SERVICE_NAMESPACE = "service namespace";
    public static final String SERVICE_LOCAL_PART = "service local part";
    public static final String SERVICE_IMPLEMENTATION_NAME_PROPERTY = "serviceImplementationName";
    private static final String SERVICE_IMPLEMENTATION_SUFFIX = "Locator";
    private static EngineConfiguration _defaultEngineConfig = null;
    private static ThreadLocal threadDefaultConfig = new ThreadLocal();
    static /* synthetic */ Class class$java$net$URL;
    static /* synthetic */ Class class$javax$xml$namespace$QName;
    static /* synthetic */ Class class$org$apache$axis$client$Service;
    static /* synthetic */ Class class$javax$xml$rpc$Service;

    public static void setThreadDefaultConfig(EngineConfiguration config) {
        threadDefaultConfig.set(config);
    }

    private static EngineConfiguration getDefaultEngineConfig() {
        if (_defaultEngineConfig == null) {
            _defaultEngineConfig = EngineConfigurationFactoryFinder.newFactory().getClientEngineConfig();
        }
        return _defaultEngineConfig;
    }

    public static Service getService(Map environment) {
        Service service = null;
        InitialContext context = null;
        EngineConfiguration configProvider = (EngineConfiguration)environment.get("engineConfig");
        if (configProvider == null) {
            configProvider = (EngineConfiguration)threadDefaultConfig.get();
        }
        if (configProvider == null) {
            configProvider = ServiceFactory.getDefaultEngineConfig();
        }
        try {
            context = new InitialContext();
        }
        catch (NamingException e) {
            // empty catch block
        }
        if (context != null) {
            String name = (String)environment.get("jndiName");
            if (name == null) {
                name = "axisServiceName";
            }
            try {
                service = (Service)context.lookup(name);
            }
            catch (NamingException e) {
                service = new Service(configProvider);
                try {
                    context.bind(name, (Object)service);
                }
                catch (NamingException e1) {}
            }
        } else {
            service = new Service(configProvider);
        }
        return service;
    }

    public Object getObjectInstance(Object refObject, Name name, Context nameCtx, Hashtable environment) throws Exception {
        Object instance = null;
        if (refObject instanceof Reference) {
            Reference ref = (Reference)refObject;
            RefAddr addr = ref.get(SERVICE_CLASSNAME);
            Object obj = null;
            if (addr != null && (obj = addr.getContent()) instanceof String) {
                instance = ClassUtils.forName((String)obj).newInstance();
            } else {
                addr = ref.get(WSDL_LOCATION);
                if (addr != null && (obj = addr.getContent()) instanceof String) {
                    URL wsdlLocation = new URL((String)obj);
                    addr = ref.get(SERVICE_NAMESPACE);
                    if (addr != null && (obj = addr.getContent()) instanceof String) {
                        String namespace = (String)obj;
                        addr = ref.get(SERVICE_LOCAL_PART);
                        if (addr != null && (obj = addr.getContent()) instanceof String) {
                            String localPart = (String)obj;
                            QName serviceName = new QName(namespace, localPart);
                            Class[] formalArgs = new Class[]{class$java$net$URL == null ? (class$java$net$URL = ServiceFactory.class$("java.net.URL")) : class$java$net$URL, class$javax$xml$namespace$QName == null ? (class$javax$xml$namespace$QName = ServiceFactory.class$("javax.xml.namespace.QName")) : class$javax$xml$namespace$QName};
                            Object[] actualArgs = new Object[]{wsdlLocation, serviceName};
                            Constructor ctor = (class$org$apache$axis$client$Service == null ? (class$org$apache$axis$client$Service = ServiceFactory.class$("org.apache.axis.client.Service")) : class$org$apache$axis$client$Service).getDeclaredConstructor(formalArgs);
                            instance = ctor.newInstance(actualArgs);
                        }
                    }
                }
            }
            addr = ref.get(MAINTAIN_SESSION);
            if (addr != null && instance instanceof Service) {
                ((Service)instance).setMaintainSession(true);
            }
        }
        return instance;
    }

    public javax.xml.rpc.Service createService(URL wsdlDocumentLocation, QName serviceName) throws ServiceException {
        return new Service(wsdlDocumentLocation, serviceName);
    }

    public javax.xml.rpc.Service createService(QName serviceName) throws ServiceException {
        return new Service(serviceName);
    }

    public javax.xml.rpc.Service loadService(Class serviceInterface) throws ServiceException {
        if (serviceInterface == null) {
            throw new IllegalArgumentException(Messages.getMessage("serviceFactoryIllegalServiceInterface"));
        }
        if (!(class$javax$xml$rpc$Service == null ? (class$javax$xml$rpc$Service = ServiceFactory.class$("javax.xml.rpc.Service")) : class$javax$xml$rpc$Service).isAssignableFrom(serviceInterface)) {
            throw new ServiceException(Messages.getMessage("serviceFactoryServiceInterfaceRequirement", serviceInterface.getName()));
        }
        String serviceImplementationName = serviceInterface.getName() + SERVICE_IMPLEMENTATION_SUFFIX;
        Service service = this.createService(serviceImplementationName);
        return service;
    }

    public javax.xml.rpc.Service loadService(URL wsdlDocumentLocation, Class serviceInterface, Properties properties) throws ServiceException {
        if (serviceInterface == null) {
            throw new IllegalArgumentException(Messages.getMessage("serviceFactoryIllegalServiceInterface"));
        }
        if (!(class$javax$xml$rpc$Service == null ? (class$javax$xml$rpc$Service = ServiceFactory.class$("javax.xml.rpc.Service")) : class$javax$xml$rpc$Service).isAssignableFrom(serviceInterface)) {
            throw new ServiceException(Messages.getMessage("serviceFactoryServiceInterfaceRequirement", serviceInterface.getName()));
        }
        String serviceImplementationName = serviceInterface.getName() + SERVICE_IMPLEMENTATION_SUFFIX;
        Service service = this.createService(serviceImplementationName);
        return service;
    }

    public javax.xml.rpc.Service loadService(URL wsdlDocumentLocation, QName serviceName, Properties properties) throws ServiceException {
        String serviceImplementationName = properties.getProperty(SERVICE_IMPLEMENTATION_NAME_PROPERTY);
        Service service = this.createService(serviceImplementationName);
        if (service.getServiceName().equals(serviceName)) {
            return service;
        }
        throw new ServiceException(Messages.getMessage("serviceFactoryServiceImplementationNotFound", serviceImplementationName));
    }

    private Service createService(String serviceImplementationName) throws ServiceException {
        if (serviceImplementationName == null) {
            throw new IllegalArgumentException(Messages.getMessage("serviceFactoryInvalidServiceName"));
        }
        try {
            Class<?> serviceImplementationClass = Thread.currentThread().getContextClassLoader().loadClass(serviceImplementationName);
            if (!(class$org$apache$axis$client$Service == null ? (class$org$apache$axis$client$Service = ServiceFactory.class$("org.apache.axis.client.Service")) : class$org$apache$axis$client$Service).isAssignableFrom(serviceImplementationClass)) {
                throw new ServiceException(Messages.getMessage("serviceFactoryServiceImplementationRequirement", serviceImplementationName));
            }
            Service service = (Service)serviceImplementationClass.newInstance();
            if (service.getServiceName() != null) {
                return service;
            }
            throw new ServiceException(Messages.getMessage("serviceFactoryInvalidServiceName"));
        }
        catch (ServiceException e) {
            throw e;
        }
        catch (Exception e) {
            throw new ServiceException(e);
        }
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

