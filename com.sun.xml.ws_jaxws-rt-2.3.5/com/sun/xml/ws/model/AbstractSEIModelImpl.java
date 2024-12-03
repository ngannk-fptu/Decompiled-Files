/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.xml.bind.api.Bridge
 *  com.sun.xml.bind.api.JAXBRIContext
 *  com.sun.xml.bind.api.TypeReference
 *  javax.jws.WebParam$Mode
 *  javax.xml.bind.JAXBContext
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.model;

import com.oracle.webservices.api.databinding.DatabindingModeFeature;
import com.sun.istack.NotNull;
import com.sun.xml.bind.api.Bridge;
import com.sun.xml.bind.api.JAXBRIContext;
import com.sun.xml.bind.api.TypeReference;
import com.sun.xml.ws.api.BindingID;
import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.databinding.Databinding;
import com.sun.xml.ws.api.model.JavaMethod;
import com.sun.xml.ws.api.model.ParameterBinding;
import com.sun.xml.ws.api.model.SEIModel;
import com.sun.xml.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.ws.api.model.wsdl.WSDLBoundPortType;
import com.sun.xml.ws.api.model.wsdl.WSDLPart;
import com.sun.xml.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.ws.binding.WebServiceFeatureList;
import com.sun.xml.ws.db.DatabindingImpl;
import com.sun.xml.ws.developer.JAXBContextFactory;
import com.sun.xml.ws.developer.UsesJAXBContextFeature;
import com.sun.xml.ws.model.CheckedExceptionImpl;
import com.sun.xml.ws.model.JavaMethodImpl;
import com.sun.xml.ws.model.ParameterImpl;
import com.sun.xml.ws.model.WrapperParameter;
import com.sun.xml.ws.resources.ModelerMessages;
import com.sun.xml.ws.spi.db.BindingContext;
import com.sun.xml.ws.spi.db.BindingContextFactory;
import com.sun.xml.ws.spi.db.BindingInfo;
import com.sun.xml.ws.spi.db.TypeInfo;
import com.sun.xml.ws.spi.db.XMLBridge;
import com.sun.xml.ws.util.Pool;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jws.WebParam;
import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceException;

public abstract class AbstractSEIModelImpl
implements SEIModel {
    private List<Class> additionalClasses = new ArrayList<Class>();
    private Pool.Marshaller marshallers;
    protected JAXBRIContext jaxbContext;
    protected BindingContext bindingContext;
    private String wsdlLocation;
    private QName serviceName;
    private QName portName;
    private QName portTypeName;
    private Map<Method, JavaMethodImpl> methodToJM = new HashMap<Method, JavaMethodImpl>();
    private Map<QName, JavaMethodImpl> nameToJM = new HashMap<QName, JavaMethodImpl>();
    private Map<QName, JavaMethodImpl> wsdlOpToJM = new HashMap<QName, JavaMethodImpl>();
    private List<JavaMethodImpl> javaMethods = new ArrayList<JavaMethodImpl>();
    private final Map<TypeReference, Bridge> bridgeMap = new HashMap<TypeReference, Bridge>();
    private final Map<TypeInfo, XMLBridge> xmlBridgeMap = new HashMap<TypeInfo, XMLBridge>();
    protected final QName emptyBodyName = new QName("");
    private String targetNamespace = "";
    private List<String> knownNamespaceURIs = null;
    private WSDLPort port;
    private final WebServiceFeatureList features;
    private Databinding databinding;
    BindingID bindingId;
    protected Class contractClass;
    protected Class endpointClass;
    protected ClassLoader classLoader = null;
    protected WSBinding wsBinding;
    protected BindingInfo databindingInfo;
    protected String defaultSchemaNamespaceSuffix;
    private static final Logger LOGGER = Logger.getLogger(AbstractSEIModelImpl.class.getName());

    protected AbstractSEIModelImpl(WebServiceFeatureList features) {
        this.features = features;
        this.databindingInfo = new BindingInfo();
        this.databindingInfo.setSEIModel(this);
    }

    void postProcess() {
        if (this.jaxbContext != null) {
            return;
        }
        this.populateMaps();
        this.createJAXBContext();
    }

    public BindingInfo databindingInfo() {
        return this.databindingInfo;
    }

    public void freeze(WSDLPort port) {
        this.port = port;
        for (JavaMethodImpl m : this.javaMethods) {
            m.freeze(port);
            this.putOp(m.getOperationQName(), m);
        }
        if (this.databinding != null) {
            ((DatabindingImpl)this.databinding).freeze(port);
        }
    }

    protected abstract void populateMaps();

    @Override
    public Pool.Marshaller getMarshallerPool() {
        return this.marshallers;
    }

    @Override
    @Deprecated
    public JAXBContext getJAXBContext() {
        JAXBContext jc = this.bindingContext.getJAXBContext();
        if (jc != null) {
            return jc;
        }
        return this.jaxbContext;
    }

    public BindingContext getBindingContext() {
        return this.bindingContext;
    }

    public List<String> getKnownNamespaceURIs() {
        return this.knownNamespaceURIs;
    }

    public final Bridge getBridge(TypeReference type) {
        Bridge b = this.bridgeMap.get(type);
        assert (b != null);
        return b;
    }

    public final XMLBridge getXMLBridge(TypeInfo type) {
        XMLBridge b = this.xmlBridgeMap.get(type);
        assert (b != null);
        return b;
    }

    private void createJAXBContext() {
        final List<TypeInfo> types = this.getAllTypeInfos();
        final ArrayList<Class> cls = new ArrayList<Class>(types.size() + this.additionalClasses.size());
        cls.addAll(this.additionalClasses);
        for (TypeInfo type : types) {
            cls.add((Class)type.type);
        }
        try {
            this.bindingContext = AccessController.doPrivileged(new PrivilegedExceptionAction<BindingContext>(){

                @Override
                public BindingContext run() throws Exception {
                    JAXBContextFactory factory;
                    if (LOGGER.isLoggable(Level.FINEST)) {
                        LOGGER.log(Level.FINEST, "Creating JAXBContext with classes={0} and types={1}", new Object[]{cls, types});
                    }
                    UsesJAXBContextFeature f = AbstractSEIModelImpl.this.features.get(UsesJAXBContextFeature.class);
                    DatabindingModeFeature dmf = AbstractSEIModelImpl.this.features.get(DatabindingModeFeature.class);
                    JAXBContextFactory jAXBContextFactory = factory = f != null ? f.getFactory() : null;
                    if (factory == null) {
                        factory = JAXBContextFactory.DEFAULT;
                    }
                    AbstractSEIModelImpl.this.databindingInfo.properties().put(JAXBContextFactory.class.getName(), factory);
                    if (dmf != null) {
                        if (LOGGER.isLoggable(Level.FINE)) {
                            LOGGER.log(Level.FINE, "DatabindingModeFeature in SEI specifies mode: {0}", dmf.getMode());
                        }
                        AbstractSEIModelImpl.this.databindingInfo.setDatabindingMode(dmf.getMode());
                    }
                    if (f != null) {
                        AbstractSEIModelImpl.this.databindingInfo.setDatabindingMode("glassfish.jaxb");
                    }
                    AbstractSEIModelImpl.this.databindingInfo.setClassLoader(AbstractSEIModelImpl.this.classLoader);
                    AbstractSEIModelImpl.this.databindingInfo.contentClasses().addAll(cls);
                    AbstractSEIModelImpl.this.databindingInfo.typeInfos().addAll(types);
                    AbstractSEIModelImpl.this.databindingInfo.properties().put("c14nSupport", Boolean.FALSE);
                    AbstractSEIModelImpl.this.databindingInfo.setDefaultNamespace(AbstractSEIModelImpl.this.getDefaultSchemaNamespace());
                    BindingContext bc = BindingContextFactory.create(AbstractSEIModelImpl.this.databindingInfo);
                    if (LOGGER.isLoggable(Level.FINE)) {
                        LOGGER.log(Level.FINE, "Created binding context: {0}", bc.getClass().getName());
                    }
                    return bc;
                }
            });
            this.createBondMap(types);
        }
        catch (PrivilegedActionException e) {
            throw new WebServiceException(ModelerMessages.UNABLE_TO_CREATE_JAXB_CONTEXT(), (Throwable)e);
        }
        this.knownNamespaceURIs = new ArrayList<String>();
        for (String namespace : this.bindingContext.getKnownNamespaceURIs()) {
            if (namespace.length() <= 0 || namespace.equals("http://www.w3.org/2001/XMLSchema") || namespace.equals("http://www.w3.org/XML/1998/namespace")) continue;
            this.knownNamespaceURIs.add(namespace);
        }
        this.marshallers = new Pool.Marshaller((JAXBContext)this.jaxbContext);
    }

    private List<TypeInfo> getAllTypeInfos() {
        ArrayList<TypeInfo> types = new ArrayList<TypeInfo>();
        Collection<JavaMethodImpl> methods = this.methodToJM.values();
        for (JavaMethodImpl m : methods) {
            m.fillTypes(types);
        }
        return types;
    }

    private void createBridgeMap(List<TypeReference> types) {
        for (TypeReference type : types) {
            Bridge bridge = this.jaxbContext.createBridge(type);
            this.bridgeMap.put(type, bridge);
        }
    }

    private void createBondMap(List<TypeInfo> types) {
        for (TypeInfo type : types) {
            XMLBridge binding = this.bindingContext.createBridge(type);
            this.xmlBridgeMap.put(type, binding);
        }
    }

    public boolean isKnownFault(QName name, Method method) {
        JavaMethodImpl m = this.getJavaMethod(method);
        for (CheckedExceptionImpl ce : m.getCheckedExceptions()) {
            if (!ce.getDetailType().tagName.equals(name)) continue;
            return true;
        }
        return false;
    }

    public boolean isCheckedException(Method m, Class ex) {
        JavaMethodImpl jm = this.getJavaMethod(m);
        for (CheckedExceptionImpl ce : jm.getCheckedExceptions()) {
            if (!ce.getExceptionClass().equals(ex)) continue;
            return true;
        }
        return false;
    }

    @Override
    public JavaMethodImpl getJavaMethod(Method method) {
        return this.methodToJM.get(method);
    }

    @Override
    public JavaMethodImpl getJavaMethod(QName name) {
        return this.nameToJM.get(name);
    }

    @Override
    public JavaMethod getJavaMethodForWsdlOperation(QName operationName) {
        return this.wsdlOpToJM.get(operationName);
    }

    @Deprecated
    public QName getQNameForJM(JavaMethodImpl jm) {
        for (Map.Entry<QName, JavaMethodImpl> entry : this.nameToJM.entrySet()) {
            JavaMethodImpl jmethod = entry.getValue();
            if (!jmethod.getOperationName().equals(jm.getOperationName())) continue;
            return entry.getKey();
        }
        return null;
    }

    public final Collection<JavaMethodImpl> getJavaMethods() {
        return Collections.unmodifiableList(this.javaMethods);
    }

    void addJavaMethod(JavaMethodImpl jm) {
        if (jm != null) {
            this.javaMethods.add(jm);
        }
    }

    private List<ParameterImpl> applyRpcLitParamBinding(JavaMethodImpl method, WrapperParameter wrapperParameter, WSDLBoundPortType boundPortType, WebParam.Mode mode) {
        QName opName = new QName(boundPortType.getPortTypeName().getNamespaceURI(), method.getOperationName());
        WSDLBoundOperation bo = boundPortType.get(opName);
        HashMap<Integer, ParameterImpl> bodyParams = new HashMap<Integer, ParameterImpl>();
        ArrayList<ParameterImpl> unboundParams = new ArrayList<ParameterImpl>();
        ArrayList<ParameterImpl> attachParams = new ArrayList<ParameterImpl>();
        for (ParameterImpl param : wrapperParameter.wrapperChildren) {
            ParameterBinding paramBinding;
            String partName = param.getPartName();
            if (partName == null || (paramBinding = boundPortType.getBinding(opName, partName, mode)) == null) continue;
            if (mode == WebParam.Mode.IN) {
                param.setInBinding(paramBinding);
            } else if (mode == WebParam.Mode.OUT || mode == WebParam.Mode.INOUT) {
                param.setOutBinding(paramBinding);
            }
            if (paramBinding.isUnbound()) {
                unboundParams.add(param);
                continue;
            }
            if (paramBinding.isAttachment()) {
                attachParams.add(param);
                continue;
            }
            if (!paramBinding.isBody()) continue;
            if (bo != null) {
                WSDLPart p = bo.getPart(param.getPartName(), mode);
                if (p != null) {
                    bodyParams.put(p.getIndex(), param);
                    continue;
                }
                bodyParams.put(bodyParams.size(), param);
                continue;
            }
            bodyParams.put(bodyParams.size(), param);
        }
        wrapperParameter.clear();
        for (int i = 0; i < bodyParams.size(); ++i) {
            ParameterImpl p = (ParameterImpl)bodyParams.get(i);
            wrapperParameter.addWrapperChild(p);
        }
        for (ParameterImpl p : unboundParams) {
            wrapperParameter.addWrapperChild(p);
        }
        return attachParams;
    }

    void put(QName name, JavaMethodImpl jm) {
        this.nameToJM.put(name, jm);
    }

    void put(Method method, JavaMethodImpl jm) {
        this.methodToJM.put(method, jm);
    }

    void putOp(QName opName, JavaMethodImpl jm) {
        this.wsdlOpToJM.put(opName, jm);
    }

    @Override
    public String getWSDLLocation() {
        return this.wsdlLocation;
    }

    void setWSDLLocation(String location) {
        this.wsdlLocation = location;
    }

    @Override
    public QName getServiceQName() {
        return this.serviceName;
    }

    @Override
    public WSDLPort getPort() {
        return this.port;
    }

    @Override
    public QName getPortName() {
        return this.portName;
    }

    @Override
    public QName getPortTypeName() {
        return this.portTypeName;
    }

    void setServiceQName(QName name) {
        this.serviceName = name;
    }

    void setPortName(QName name) {
        this.portName = name;
    }

    void setPortTypeName(QName name) {
        this.portTypeName = name;
    }

    void setTargetNamespace(String namespace) {
        this.targetNamespace = namespace;
    }

    @Override
    public String getTargetNamespace() {
        return this.targetNamespace;
    }

    String getDefaultSchemaNamespace() {
        String defaultNamespace = this.getTargetNamespace();
        if (this.defaultSchemaNamespaceSuffix == null) {
            return defaultNamespace;
        }
        if (!defaultNamespace.endsWith("/")) {
            defaultNamespace = defaultNamespace + "/";
        }
        return defaultNamespace + this.defaultSchemaNamespaceSuffix;
    }

    @Override
    @NotNull
    public QName getBoundPortTypeName() {
        assert (this.portName != null);
        return new QName(this.portName.getNamespaceURI(), this.portName.getLocalPart() + "Binding");
    }

    public void addAdditionalClasses(Class ... additionalClasses) {
        this.additionalClasses.addAll(Arrays.asList(additionalClasses));
    }

    public Databinding getDatabinding() {
        return this.databinding;
    }

    public void setDatabinding(Databinding wsRuntime) {
        this.databinding = wsRuntime;
    }

    public WSBinding getWSBinding() {
        return this.wsBinding;
    }

    public Class getContractClass() {
        return this.contractClass;
    }

    public Class getEndpointClass() {
        return this.endpointClass;
    }
}

