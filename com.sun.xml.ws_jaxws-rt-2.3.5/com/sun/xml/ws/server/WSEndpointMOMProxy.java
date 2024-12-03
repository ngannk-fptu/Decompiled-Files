/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.xml.ws.policy.PolicyMap
 *  javax.xml.ws.EndpointReference
 *  org.glassfish.gmbal.AMXClient
 *  org.glassfish.gmbal.GmbalMBean
 *  org.glassfish.gmbal.ManagedObjectManager
 *  org.glassfish.gmbal.ManagedObjectManager$RegistrationDebugLevel
 *  org.glassfish.pfl.tf.timer.spi.ObjectRegistrationManager
 */
package com.sun.xml.ws.server;

import com.sun.istack.NotNull;
import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.model.SEIModel;
import com.sun.xml.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.ws.api.pipe.Codec;
import com.sun.xml.ws.api.pipe.FiberContextSwitchInterceptor;
import com.sun.xml.ws.api.pipe.ServerTubeAssemblerContext;
import com.sun.xml.ws.api.pipe.ThrowableContainerPropertySet;
import com.sun.xml.ws.api.server.Container;
import com.sun.xml.ws.api.server.ServiceDefinition;
import com.sun.xml.ws.api.server.WSEndpoint;
import com.sun.xml.ws.policy.PolicyMap;
import com.sun.xml.ws.server.WSEndpointImpl;
import com.sun.xml.ws.wsdl.OperationDispatcher;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Executor;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.xml.namespace.QName;
import javax.xml.ws.EndpointReference;
import org.glassfish.gmbal.AMXClient;
import org.glassfish.gmbal.GmbalMBean;
import org.glassfish.gmbal.ManagedObjectManager;
import org.glassfish.pfl.tf.timer.spi.ObjectRegistrationManager;
import org.w3c.dom.Element;

public class WSEndpointMOMProxy
extends WSEndpoint
implements ManagedObjectManager {
    @NotNull
    private final WSEndpointImpl wsEndpoint;
    private ManagedObjectManager managedObjectManager;

    WSEndpointMOMProxy(@NotNull WSEndpointImpl wsEndpoint) {
        this.wsEndpoint = wsEndpoint;
    }

    @Override
    public ManagedObjectManager getManagedObjectManager() {
        if (this.managedObjectManager == null) {
            this.managedObjectManager = this.wsEndpoint.obtainManagedObjectManager();
        }
        return this.managedObjectManager;
    }

    void setManagedObjectManager(ManagedObjectManager managedObjectManager) {
        this.managedObjectManager = managedObjectManager;
    }

    public boolean isInitialized() {
        return this.managedObjectManager != null;
    }

    public WSEndpointImpl getWsEndpoint() {
        return this.wsEndpoint;
    }

    public void suspendJMXRegistration() {
        this.getManagedObjectManager().suspendJMXRegistration();
    }

    public void resumeJMXRegistration() {
        this.getManagedObjectManager().resumeJMXRegistration();
    }

    public boolean isManagedObject(Object obj) {
        return this.getManagedObjectManager().isManagedObject(obj);
    }

    public GmbalMBean createRoot() {
        return this.getManagedObjectManager().createRoot();
    }

    public GmbalMBean createRoot(Object root) {
        return this.getManagedObjectManager().createRoot(root);
    }

    public GmbalMBean createRoot(Object root, String name) {
        return this.getManagedObjectManager().createRoot(root, name);
    }

    public Object getRoot() {
        return this.getManagedObjectManager().getRoot();
    }

    public GmbalMBean register(Object parent, Object obj, String name) {
        return this.getManagedObjectManager().register(parent, obj, name);
    }

    public GmbalMBean register(Object parent, Object obj) {
        return this.getManagedObjectManager().register(parent, obj);
    }

    public GmbalMBean registerAtRoot(Object obj, String name) {
        return this.getManagedObjectManager().registerAtRoot(obj, name);
    }

    public GmbalMBean registerAtRoot(Object obj) {
        return this.getManagedObjectManager().registerAtRoot(obj);
    }

    public void unregister(Object obj) {
        this.getManagedObjectManager().unregister(obj);
    }

    public ObjectName getObjectName(Object obj) {
        return this.getManagedObjectManager().getObjectName(obj);
    }

    public AMXClient getAMXClient(Object obj) {
        return this.getManagedObjectManager().getAMXClient(obj);
    }

    public Object getObject(ObjectName oname) {
        return this.getManagedObjectManager().getObject(oname);
    }

    public void stripPrefix(String ... str) {
        this.getManagedObjectManager().stripPrefix(str);
    }

    public void stripPackagePrefix() {
        this.getManagedObjectManager().stripPackagePrefix();
    }

    public String getDomain() {
        return this.getManagedObjectManager().getDomain();
    }

    public void setMBeanServer(MBeanServer server) {
        this.getManagedObjectManager().setMBeanServer(server);
    }

    public MBeanServer getMBeanServer() {
        return this.getManagedObjectManager().getMBeanServer();
    }

    public void setResourceBundle(ResourceBundle rb) {
        this.getManagedObjectManager().setResourceBundle(rb);
    }

    public ResourceBundle getResourceBundle() {
        return this.getManagedObjectManager().getResourceBundle();
    }

    public void addAnnotation(AnnotatedElement element, Annotation annotation) {
        this.getManagedObjectManager().addAnnotation(element, annotation);
    }

    public void addInheritedAnnotations(Class<?> cls) {
        this.getManagedObjectManager().addInheritedAnnotations(cls);
    }

    public void setRegistrationDebug(ManagedObjectManager.RegistrationDebugLevel level) {
        this.getManagedObjectManager().setRegistrationDebug(level);
    }

    public void setRuntimeDebug(boolean flag) {
        this.getManagedObjectManager().setRuntimeDebug(flag);
    }

    public void setTypelibDebug(int level) {
        this.getManagedObjectManager().setTypelibDebug(level);
    }

    public void setJMXRegistrationDebug(boolean flag) {
        this.getManagedObjectManager().setJMXRegistrationDebug(flag);
    }

    public String dumpSkeleton(Object obj) {
        return this.getManagedObjectManager().dumpSkeleton(obj);
    }

    public void suppressDuplicateRootReport(boolean suppressReport) {
        this.getManagedObjectManager().suppressDuplicateRootReport(suppressReport);
    }

    public void close() throws IOException {
        this.getManagedObjectManager().close();
    }

    @Override
    public boolean equalsProxiedInstance(WSEndpoint endpoint) {
        if (this.wsEndpoint == null) {
            return endpoint == null;
        }
        return this.wsEndpoint.equals(endpoint);
    }

    @Override
    public Codec createCodec() {
        return this.wsEndpoint.createCodec();
    }

    @Override
    public QName getServiceName() {
        return this.wsEndpoint.getServiceName();
    }

    @Override
    public QName getPortName() {
        return this.wsEndpoint.getPortName();
    }

    public Class getImplementationClass() {
        return this.wsEndpoint.getImplementationClass();
    }

    @Override
    public WSBinding getBinding() {
        return this.wsEndpoint.getBinding();
    }

    @Override
    public Container getContainer() {
        return this.wsEndpoint.getContainer();
    }

    @Override
    public WSDLPort getPort() {
        return this.wsEndpoint.getPort();
    }

    @Override
    public void setExecutor(Executor exec) {
        this.wsEndpoint.setExecutor(exec);
    }

    @Override
    public void schedule(Packet request, WSEndpoint.CompletionCallback callback, FiberContextSwitchInterceptor interceptor) {
        this.wsEndpoint.schedule(request, callback, interceptor);
    }

    @Override
    public WSEndpoint.PipeHead createPipeHead() {
        return this.wsEndpoint.createPipeHead();
    }

    @Override
    public void dispose() {
        if (this.wsEndpoint != null) {
            this.wsEndpoint.dispose();
        }
    }

    @Override
    public ServiceDefinition getServiceDefinition() {
        return this.wsEndpoint.getServiceDefinition();
    }

    @Override
    public SEIModel getSEIModel() {
        return this.wsEndpoint.getSEIModel();
    }

    @Override
    public PolicyMap getPolicyMap() {
        return this.wsEndpoint.getPolicyMap();
    }

    @Override
    public void closeManagedObjectManager() {
        this.wsEndpoint.closeManagedObjectManager();
    }

    @Override
    public ServerTubeAssemblerContext getAssemblerContext() {
        return this.wsEndpoint.getAssemblerContext();
    }

    public EndpointReference getEndpointReference(Class clazz, String address, String wsdlAddress, Element ... referenceParameters) {
        return this.wsEndpoint.getEndpointReference(clazz, address, wsdlAddress, referenceParameters);
    }

    public EndpointReference getEndpointReference(Class clazz, String address, String wsdlAddress, List metadata, List referenceParameters) {
        return this.wsEndpoint.getEndpointReference(clazz, address, wsdlAddress, metadata, referenceParameters);
    }

    @Override
    public OperationDispatcher getOperationDispatcher() {
        return this.wsEndpoint.getOperationDispatcher();
    }

    @Override
    public Packet createServiceResponseForException(ThrowableContainerPropertySet tc, Packet responsePacket, SOAPVersion soapVersion, WSDLPort wsdlPort, SEIModel seiModel, WSBinding binding) {
        return this.wsEndpoint.createServiceResponseForException(tc, responsePacket, soapVersion, wsdlPort, seiModel, binding);
    }

    public ObjectRegistrationManager getObjectRegistrationManager() {
        return this.getManagedObjectManager().getObjectRegistrationManager();
    }
}

