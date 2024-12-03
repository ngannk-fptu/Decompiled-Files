/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.jws.WebService
 *  javax.xml.ws.BindingProvider
 *  javax.xml.ws.ProtocolException
 *  javax.xml.ws.Service
 *  javax.xml.ws.WebServiceException
 *  javax.xml.ws.WebServiceFeature
 *  javax.xml.ws.soap.SOAPFaultException
 *  org.aopalliance.intercept.MethodInterceptor
 *  org.aopalliance.intercept.MethodInvocation
 *  org.springframework.aop.support.AopUtils
 *  org.springframework.beans.factory.BeanClassLoaderAware
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.lang.Nullable
 *  org.springframework.remoting.RemoteAccessException
 *  org.springframework.remoting.RemoteConnectFailureException
 *  org.springframework.remoting.RemoteLookupFailureException
 *  org.springframework.remoting.RemoteProxyFailureException
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.StringUtils
 */
package org.springframework.remoting.jaxws;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.jws.WebService;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.ProtocolException;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.soap.SOAPFaultException;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.Nullable;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.remoting.RemoteConnectFailureException;
import org.springframework.remoting.RemoteLookupFailureException;
import org.springframework.remoting.RemoteProxyFailureException;
import org.springframework.remoting.jaxws.JaxWsSoapFaultException;
import org.springframework.remoting.jaxws.LocalJaxWsServiceFactory;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

public class JaxWsPortClientInterceptor
extends LocalJaxWsServiceFactory
implements MethodInterceptor,
BeanClassLoaderAware,
InitializingBean {
    @Nullable
    private Service jaxWsService;
    @Nullable
    private String portName;
    @Nullable
    private String username;
    @Nullable
    private String password;
    @Nullable
    private String endpointAddress;
    private boolean maintainSession;
    private boolean useSoapAction;
    @Nullable
    private String soapActionUri;
    @Nullable
    private Map<String, Object> customProperties;
    @Nullable
    private WebServiceFeature[] portFeatures;
    @Nullable
    private Class<?> serviceInterface;
    private boolean lookupServiceOnStartup = true;
    @Nullable
    private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();
    @Nullable
    private QName portQName;
    @Nullable
    private Object portStub;
    private final Object preparationMonitor = new Object();

    public void setJaxWsService(@Nullable Service jaxWsService) {
        this.jaxWsService = jaxWsService;
    }

    @Nullable
    public Service getJaxWsService() {
        return this.jaxWsService;
    }

    public void setPortName(@Nullable String portName) {
        this.portName = portName;
    }

    @Nullable
    public String getPortName() {
        return this.portName;
    }

    public void setUsername(@Nullable String username) {
        this.username = username;
    }

    @Nullable
    public String getUsername() {
        return this.username;
    }

    public void setPassword(@Nullable String password) {
        this.password = password;
    }

    @Nullable
    public String getPassword() {
        return this.password;
    }

    public void setEndpointAddress(@Nullable String endpointAddress) {
        this.endpointAddress = endpointAddress;
    }

    @Nullable
    public String getEndpointAddress() {
        return this.endpointAddress;
    }

    public void setMaintainSession(boolean maintainSession) {
        this.maintainSession = maintainSession;
    }

    public boolean isMaintainSession() {
        return this.maintainSession;
    }

    public void setUseSoapAction(boolean useSoapAction) {
        this.useSoapAction = useSoapAction;
    }

    public boolean isUseSoapAction() {
        return this.useSoapAction;
    }

    public void setSoapActionUri(@Nullable String soapActionUri) {
        this.soapActionUri = soapActionUri;
    }

    @Nullable
    public String getSoapActionUri() {
        return this.soapActionUri;
    }

    public void setCustomProperties(Map<String, Object> customProperties) {
        this.customProperties = customProperties;
    }

    public Map<String, Object> getCustomProperties() {
        if (this.customProperties == null) {
            this.customProperties = new HashMap<String, Object>();
        }
        return this.customProperties;
    }

    public void addCustomProperty(String name, Object value) {
        this.getCustomProperties().put(name, value);
    }

    public void setPortFeatures(WebServiceFeature ... features) {
        this.portFeatures = features;
    }

    public void setServiceInterface(@Nullable Class<?> serviceInterface) {
        if (serviceInterface != null) {
            Assert.isTrue((boolean)serviceInterface.isInterface(), (String)"'serviceInterface' must be an interface");
        }
        this.serviceInterface = serviceInterface;
    }

    @Nullable
    public Class<?> getServiceInterface() {
        return this.serviceInterface;
    }

    public void setLookupServiceOnStartup(boolean lookupServiceOnStartup) {
        this.lookupServiceOnStartup = lookupServiceOnStartup;
    }

    public void setBeanClassLoader(@Nullable ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }

    @Nullable
    protected ClassLoader getBeanClassLoader() {
        return this.beanClassLoader;
    }

    public void afterPropertiesSet() {
        if (this.lookupServiceOnStartup) {
            this.prepare();
        }
    }

    public void prepare() {
        Service serviceToUse;
        Class<?> ifc = this.getServiceInterface();
        Assert.notNull(ifc, (String)"Property 'serviceInterface' is required");
        WebService ann = ifc.getAnnotation(WebService.class);
        if (ann != null) {
            this.applyDefaultsFromAnnotation(ann);
        }
        if ((serviceToUse = this.getJaxWsService()) == null) {
            serviceToUse = this.createJaxWsService();
        }
        this.portQName = this.getQName(this.getPortName() != null ? this.getPortName() : ifc.getName());
        Object stub = this.getPortStub(serviceToUse, this.getPortName() != null ? this.portQName : null);
        this.preparePortStub(stub);
        this.portStub = stub;
    }

    protected void applyDefaultsFromAnnotation(WebService ann) {
        String pn;
        String sn;
        String ns;
        String wsdl;
        if (this.getWsdlDocumentUrl() == null && StringUtils.hasText((String)(wsdl = ann.wsdlLocation()))) {
            try {
                this.setWsdlDocumentUrl(new URL(wsdl));
            }
            catch (MalformedURLException ex) {
                throw new IllegalStateException("Encountered invalid @Service wsdlLocation value [" + wsdl + "]", ex);
            }
        }
        if (this.getNamespaceUri() == null && StringUtils.hasText((String)(ns = ann.targetNamespace()))) {
            this.setNamespaceUri(ns);
        }
        if (this.getServiceName() == null && StringUtils.hasText((String)(sn = ann.serviceName()))) {
            this.setServiceName(sn);
        }
        if (this.getPortName() == null && StringUtils.hasText((String)(pn = ann.portName()))) {
            this.setPortName(pn);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected boolean isPrepared() {
        Object object = this.preparationMonitor;
        synchronized (object) {
            return this.portStub != null;
        }
    }

    @Nullable
    protected final QName getPortQName() {
        return this.portQName;
    }

    protected Object getPortStub(Service service, @Nullable QName portQName) {
        if (this.portFeatures != null) {
            return portQName != null ? service.getPort(portQName, this.getServiceInterface(), this.portFeatures) : service.getPort(this.getServiceInterface(), this.portFeatures);
        }
        return portQName != null ? service.getPort(portQName, this.getServiceInterface()) : service.getPort(this.getServiceInterface());
    }

    protected void preparePortStub(Object stub) {
        String soapActionUri;
        String endpointAddress;
        String password;
        HashMap<String, Object> stubProperties = new HashMap<String, Object>();
        String username = this.getUsername();
        if (username != null) {
            stubProperties.put("javax.xml.ws.security.auth.username", username);
        }
        if ((password = this.getPassword()) != null) {
            stubProperties.put("javax.xml.ws.security.auth.password", password);
        }
        if ((endpointAddress = this.getEndpointAddress()) != null) {
            stubProperties.put("javax.xml.ws.service.endpoint.address", endpointAddress);
        }
        if (this.isMaintainSession()) {
            stubProperties.put("javax.xml.ws.session.maintain", Boolean.TRUE);
        }
        if (this.isUseSoapAction()) {
            stubProperties.put("javax.xml.ws.soap.http.soapaction.use", Boolean.TRUE);
        }
        if ((soapActionUri = this.getSoapActionUri()) != null) {
            stubProperties.put("javax.xml.ws.soap.http.soapaction.uri", soapActionUri);
        }
        stubProperties.putAll(this.getCustomProperties());
        if (!stubProperties.isEmpty()) {
            if (!(stub instanceof BindingProvider)) {
                throw new RemoteLookupFailureException("Port stub of class [" + stub.getClass().getName() + "] is not a customizable JAX-WS stub: it does not implement interface [javax.xml.ws.BindingProvider]");
            }
            ((BindingProvider)stub).getRequestContext().putAll(stubProperties);
        }
    }

    @Nullable
    protected Object getPortStub() {
        return this.portStub;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Nullable
    public Object invoke(MethodInvocation invocation) throws Throwable {
        if (AopUtils.isToStringMethod((Method)invocation.getMethod())) {
            return "JAX-WS proxy for port [" + this.getPortName() + "] of service [" + this.getServiceName() + "]";
        }
        Object object = this.preparationMonitor;
        synchronized (object) {
            if (!this.isPrepared()) {
                this.prepare();
            }
        }
        return this.doInvoke(invocation);
    }

    @Nullable
    protected Object doInvoke(MethodInvocation invocation) throws Throwable {
        try {
            return this.doInvoke(invocation, this.getPortStub());
        }
        catch (SOAPFaultException ex) {
            throw new JaxWsSoapFaultException(ex);
        }
        catch (ProtocolException ex) {
            throw new RemoteConnectFailureException("Could not connect to remote service [" + this.getEndpointAddress() + "]", (Throwable)ex);
        }
        catch (WebServiceException ex) {
            throw new RemoteAccessException("Could not access remote service at [" + this.getEndpointAddress() + "]", (Throwable)ex);
        }
    }

    @Nullable
    protected Object doInvoke(MethodInvocation invocation, @Nullable Object portStub) throws Throwable {
        Method method = invocation.getMethod();
        try {
            return method.invoke(portStub, invocation.getArguments());
        }
        catch (InvocationTargetException ex) {
            throw ex.getTargetException();
        }
        catch (Throwable ex) {
            throw new RemoteProxyFailureException("Invocation of stub method failed: " + method, ex);
        }
    }
}

