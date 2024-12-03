/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.localization.Localizable
 *  javax.jws.Oneway
 *  javax.jws.WebMethod
 *  javax.jws.WebParam
 *  javax.jws.WebParam$Mode
 *  javax.jws.WebResult
 *  javax.jws.WebService
 *  javax.jws.soap.SOAPBinding
 *  javax.jws.soap.SOAPBinding$ParameterStyle
 *  javax.jws.soap.SOAPBinding$Style
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlSeeAlso
 *  javax.xml.bind.annotation.XmlTransient
 *  javax.xml.ws.Action
 *  javax.xml.ws.AsyncHandler
 *  javax.xml.ws.BindingType
 *  javax.xml.ws.FaultAction
 *  javax.xml.ws.Holder
 *  javax.xml.ws.RequestWrapper
 *  javax.xml.ws.Response
 *  javax.xml.ws.ResponseWrapper
 *  javax.xml.ws.WebFault
 *  javax.xml.ws.soap.MTOM
 *  javax.xml.ws.soap.MTOMFeature
 */
package com.sun.xml.ws.model;

import com.oracle.webservices.api.EnvelopeStyle;
import com.oracle.webservices.api.EnvelopeStyleFeature;
import com.oracle.webservices.api.databinding.DatabindingMode;
import com.sun.istack.NotNull;
import com.sun.istack.localization.Localizable;
import com.sun.xml.ws.api.BindingID;
import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.databinding.DatabindingConfig;
import com.sun.xml.ws.api.databinding.MetadataReader;
import com.sun.xml.ws.api.model.ExceptionType;
import com.sun.xml.ws.api.model.MEP;
import com.sun.xml.ws.api.model.Parameter;
import com.sun.xml.ws.api.model.ParameterBinding;
import com.sun.xml.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.ws.api.model.wsdl.WSDLInput;
import com.sun.xml.ws.api.model.wsdl.WSDLPart;
import com.sun.xml.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.ws.binding.WebServiceFeatureList;
import com.sun.xml.ws.model.AbstractSEIModelImpl;
import com.sun.xml.ws.model.CheckedExceptionImpl;
import com.sun.xml.ws.model.JavaMethodImpl;
import com.sun.xml.ws.model.ParameterImpl;
import com.sun.xml.ws.model.ReflectAnnotationReader;
import com.sun.xml.ws.model.RuntimeModelerException;
import com.sun.xml.ws.model.SOAPSEIModel;
import com.sun.xml.ws.model.Utils;
import com.sun.xml.ws.model.WrapperBeanGenerator;
import com.sun.xml.ws.model.WrapperParameter;
import com.sun.xml.ws.model.soap.SOAPBindingImpl;
import com.sun.xml.ws.resources.ModelerMessages;
import com.sun.xml.ws.resources.ServerMessages;
import com.sun.xml.ws.spi.db.BindingContext;
import com.sun.xml.ws.spi.db.TypeInfo;
import com.sun.xml.ws.spi.db.WrapperComposite;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.concurrent.Future;
import java.util.logging.Logger;
import javax.jws.Oneway;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.namespace.QName;
import javax.xml.ws.Action;
import javax.xml.ws.AsyncHandler;
import javax.xml.ws.BindingType;
import javax.xml.ws.FaultAction;
import javax.xml.ws.Holder;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.Response;
import javax.xml.ws.ResponseWrapper;
import javax.xml.ws.WebFault;
import javax.xml.ws.soap.MTOM;
import javax.xml.ws.soap.MTOMFeature;

public class RuntimeModeler {
    private final WebServiceFeatureList features;
    private BindingID bindingId;
    private WSBinding wsBinding;
    private final Class portClass;
    private AbstractSEIModelImpl model;
    private SOAPBindingImpl defaultBinding;
    private String packageName;
    private String targetNamespace;
    private boolean isWrapped = true;
    private ClassLoader classLoader;
    private final WSDLPort binding;
    private QName serviceName;
    private QName portName;
    private Set<Class> classUsesWebMethod;
    private DatabindingConfig config;
    private MetadataReader metadataReader;
    public static final String PD_JAXWS_PACKAGE_PD = ".jaxws.";
    public static final String JAXWS_PACKAGE_PD = "jaxws.";
    public static final String RESPONSE = "Response";
    public static final String RETURN = "return";
    public static final String BEAN = "Bean";
    public static final String SERVICE = "Service";
    public static final String PORT = "Port";
    public static final Class HOLDER_CLASS = Holder.class;
    public static final String REMOTE_EXCEPTION_CLASS = "java.rmi.RemoteException";
    public static final Class<RuntimeException> RUNTIME_EXCEPTION_CLASS = RuntimeException.class;
    public static final Class<Exception> EXCEPTION_CLASS = Exception.class;
    public static final String DecapitalizeExceptionBeanProperties = "com.sun.xml.ws.api.model.DecapitalizeExceptionBeanProperties";
    public static final String SuppressDocLitWrapperGeneration = "com.sun.xml.ws.api.model.SuppressDocLitWrapperGeneration";
    public static final String DocWrappeeNamespapceQualified = "com.sun.xml.ws.api.model.DocWrappeeNamespapceQualified";
    private static final Logger logger = Logger.getLogger("com.sun.xml.ws.server");

    public RuntimeModeler(@NotNull DatabindingConfig config) {
        this.portClass = config.getEndpointClass() != null ? config.getEndpointClass() : config.getContractClass();
        this.serviceName = config.getMappingInfo().getServiceName();
        this.binding = config.getWsdlPort();
        this.classLoader = config.getClassLoader();
        this.portName = config.getMappingInfo().getPortName();
        this.config = config;
        this.wsBinding = config.getWSBinding();
        this.metadataReader = config.getMetadataReader();
        this.targetNamespace = config.getMappingInfo().getTargetNamespace();
        if (this.metadataReader == null) {
            this.metadataReader = new ReflectAnnotationReader();
        }
        if (this.wsBinding != null) {
            this.bindingId = this.wsBinding.getBindingId();
            if (config.getFeatures() != null) {
                this.wsBinding.getFeatures().mergeFeatures(config.getFeatures(), false);
            }
            if (this.binding != null) {
                this.wsBinding.getFeatures().mergeFeatures(this.binding.getFeatures(), false);
            }
            this.features = WebServiceFeatureList.toList(this.wsBinding.getFeatures());
        } else {
            EnvelopeStyle es;
            MTOM mtomAn;
            this.bindingId = config.getMappingInfo().getBindingID();
            this.features = WebServiceFeatureList.toList(config.getFeatures());
            if (this.binding != null) {
                this.bindingId = this.binding.getBinding().getBindingId();
            }
            if (this.bindingId == null) {
                this.bindingId = this.getDefaultBindingID();
            }
            if (!this.features.contains(MTOMFeature.class) && (mtomAn = this.getAnnotation(this.portClass, MTOM.class)) != null) {
                this.features.add(WebServiceFeatureList.getFeature((Annotation)mtomAn));
            }
            if (!this.features.contains(EnvelopeStyleFeature.class) && (es = this.getAnnotation(this.portClass, EnvelopeStyle.class)) != null) {
                this.features.add(WebServiceFeatureList.getFeature(es));
            }
            this.wsBinding = this.bindingId.createBinding(this.features);
        }
    }

    private BindingID getDefaultBindingID() {
        BindingType bt = this.getAnnotation(this.portClass, BindingType.class);
        if (bt != null) {
            return BindingID.parse(bt.value());
        }
        SOAPVersion ver = WebServiceFeatureList.getSoapVersion(this.features);
        boolean mtomEnabled = this.features.isEnabled(MTOMFeature.class);
        if (SOAPVersion.SOAP_12.equals((Object)ver)) {
            return mtomEnabled ? BindingID.SOAP12_HTTP_MTOM : BindingID.SOAP12_HTTP;
        }
        return mtomEnabled ? BindingID.SOAP11_HTTP_MTOM : BindingID.SOAP11_HTTP;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public void setPortName(QName portName) {
        this.portName = portName;
    }

    private <T extends Annotation> T getAnnotation(Class<?> clazz, Class<T> T) {
        return this.metadataReader.getAnnotation(T, clazz);
    }

    private <T extends Annotation> T getAnnotation(Method method, Class<T> T) {
        return this.metadataReader.getAnnotation(T, method);
    }

    private Annotation[] getAnnotations(Method method) {
        return this.metadataReader.getAnnotations(method);
    }

    private Annotation[] getAnnotations(Class<?> c) {
        return this.metadataReader.getAnnotations(c);
    }

    private Annotation[][] getParamAnnotations(Method method) {
        return this.metadataReader.getParameterAnnotations(method);
    }

    public AbstractSEIModelImpl buildRuntimeModel() {
        this.model = new SOAPSEIModel(this.features);
        this.model.contractClass = this.config.getContractClass();
        this.model.endpointClass = this.config.getEndpointClass();
        this.model.classLoader = this.classLoader;
        this.model.wsBinding = this.wsBinding;
        this.model.databindingInfo.setWsdlURL(this.config.getWsdlURL());
        this.model.databindingInfo.properties().putAll(this.config.properties());
        if (this.model.contractClass == null) {
            this.model.contractClass = this.portClass;
        }
        if (this.model.endpointClass == null && !this.portClass.isInterface()) {
            this.model.endpointClass = this.portClass;
        }
        Class seiClass = this.portClass;
        this.metadataReader.getProperties(this.model.databindingInfo.properties(), this.portClass);
        WebService webService = this.getAnnotation(this.portClass, WebService.class);
        if (webService == null) {
            throw new RuntimeModelerException("runtime.modeler.no.webservice.annotation", this.portClass.getCanonicalName());
        }
        Class seiFromConfig = this.configEndpointInterface();
        if (webService.endpointInterface().length() > 0 || seiFromConfig != null) {
            seiClass = seiFromConfig != null ? seiFromConfig : this.getClass(webService.endpointInterface(), ModelerMessages.localizableRUNTIME_MODELER_CLASS_NOT_FOUND(webService.endpointInterface()));
            this.model.contractClass = seiClass;
            this.model.endpointClass = this.portClass;
            WebService seiService = this.getAnnotation(seiClass, WebService.class);
            if (seiService == null) {
                throw new RuntimeModelerException("runtime.modeler.endpoint.interface.no.webservice", webService.endpointInterface());
            }
            SOAPBinding sbPortClass = this.getAnnotation(this.portClass, SOAPBinding.class);
            SOAPBinding sbSei = this.getAnnotation(seiClass, SOAPBinding.class);
            if (sbPortClass != null && (sbSei == null || sbSei.style() != sbPortClass.style() || sbSei.use() != sbPortClass.use())) {
                logger.warning(ServerMessages.RUNTIMEMODELER_INVALIDANNOTATION_ON_IMPL("@SOAPBinding", this.portClass.getName(), seiClass.getName()));
            }
        }
        if (this.serviceName == null) {
            this.serviceName = RuntimeModeler.getServiceName(this.portClass, this.metadataReader);
        }
        this.model.setServiceQName(this.serviceName);
        if (this.portName == null) {
            this.portName = RuntimeModeler.getPortName(this.portClass, this.metadataReader, this.serviceName.getNamespaceURI());
        }
        this.model.setPortName(this.portName);
        DatabindingMode dbm2 = this.getAnnotation(this.portClass, DatabindingMode.class);
        if (dbm2 != null) {
            this.model.databindingInfo.setDatabindingMode(dbm2.value());
        }
        this.processClass(seiClass);
        if (this.model.getJavaMethods().size() == 0) {
            throw new RuntimeModelerException("runtime.modeler.no.operations", this.portClass.getName());
        }
        this.model.postProcess();
        this.config.properties().put(BindingContext.class.getName(), this.model.bindingContext);
        if (this.binding != null) {
            this.model.freeze(this.binding);
        }
        return this.model;
    }

    private Class configEndpointInterface() {
        if (this.config.getEndpointClass() == null || this.config.getEndpointClass().isInterface()) {
            return null;
        }
        return this.config.getContractClass();
    }

    private Class getClass(String className, Localizable errorMessage) {
        try {
            if (this.classLoader == null) {
                return Thread.currentThread().getContextClassLoader().loadClass(className);
            }
            return this.classLoader.loadClass(className);
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeModelerException(errorMessage);
        }
    }

    private boolean noWrapperGen() {
        Object o = this.config.properties().get(SuppressDocLitWrapperGeneration);
        return o != null && o instanceof Boolean ? (Boolean)o : false;
    }

    private Class getRequestWrapperClass(String className, Method method, QName reqElemName) {
        ClassLoader loader = this.classLoader == null ? Thread.currentThread().getContextClassLoader() : this.classLoader;
        try {
            return loader.loadClass(className);
        }
        catch (ClassNotFoundException e) {
            if (this.noWrapperGen()) {
                return WrapperComposite.class;
            }
            logger.fine("Dynamically creating request wrapper Class " + className);
            return WrapperBeanGenerator.createRequestWrapperBean(className, method, reqElemName, loader);
        }
    }

    private Class getResponseWrapperClass(String className, Method method, QName resElemName) {
        ClassLoader loader = this.classLoader == null ? Thread.currentThread().getContextClassLoader() : this.classLoader;
        try {
            return loader.loadClass(className);
        }
        catch (ClassNotFoundException e) {
            if (this.noWrapperGen()) {
                return WrapperComposite.class;
            }
            logger.fine("Dynamically creating response wrapper bean Class " + className);
            return WrapperBeanGenerator.createResponseWrapperBean(className, method, resElemName, loader);
        }
    }

    private Class getExceptionBeanClass(String className, Class exception, String name, String namespace) {
        boolean decapitalizeExceptionBeanProperties = true;
        Object o = this.config.properties().get(DecapitalizeExceptionBeanProperties);
        if (o != null && o instanceof Boolean) {
            decapitalizeExceptionBeanProperties = (Boolean)o;
        }
        ClassLoader loader = this.classLoader == null ? Thread.currentThread().getContextClassLoader() : this.classLoader;
        try {
            return loader.loadClass(className);
        }
        catch (ClassNotFoundException e) {
            if (this.noWrapperGen()) {
                return exception;
            }
            logger.fine("Dynamically creating exception bean Class " + className);
            return WrapperBeanGenerator.createExceptionBean(className, exception, this.targetNamespace, name, namespace, loader, decapitalizeExceptionBeanProperties);
        }
    }

    protected void determineWebMethodUse(Class clazz) {
        if (clazz == null) {
            return;
        }
        if (!clazz.isInterface()) {
            if (clazz == Object.class) {
                return;
            }
            for (Method method : clazz.getMethods()) {
                WebMethod webMethod;
                if (method.getDeclaringClass() != clazz || (webMethod = this.getAnnotation(method, WebMethod.class)) == null || webMethod.exclude()) continue;
                this.classUsesWebMethod.add(clazz);
                break;
            }
        }
        this.determineWebMethodUse(clazz.getSuperclass());
    }

    void processClass(Class clazz) {
        this.classUsesWebMethod = new HashSet<Class>();
        this.determineWebMethodUse(clazz);
        WebService webService = this.getAnnotation(clazz, WebService.class);
        QName portTypeName = RuntimeModeler.getPortTypeName(clazz, this.targetNamespace, this.metadataReader);
        this.packageName = "";
        if (clazz.getPackage() != null) {
            this.packageName = clazz.getPackage().getName();
        }
        this.targetNamespace = portTypeName.getNamespaceURI();
        this.model.setPortTypeName(portTypeName);
        this.model.setTargetNamespace(this.targetNamespace);
        this.model.defaultSchemaNamespaceSuffix = this.config.getMappingInfo().getDefaultSchemaNamespaceSuffix();
        this.model.setWSDLLocation(webService.wsdlLocation());
        SOAPBinding soapBinding = this.getAnnotation(clazz, SOAPBinding.class);
        if (soapBinding != null) {
            if (soapBinding.style() == SOAPBinding.Style.RPC && soapBinding.parameterStyle() == SOAPBinding.ParameterStyle.BARE) {
                throw new RuntimeModelerException("runtime.modeler.invalid.soapbinding.parameterstyle", soapBinding, clazz);
            }
            this.isWrapped = soapBinding.parameterStyle() == SOAPBinding.ParameterStyle.WRAPPED;
        }
        this.defaultBinding = this.createBinding(soapBinding);
        for (Method method : clazz.getMethods()) {
            if (!clazz.isInterface() && (method.getDeclaringClass() == Object.class || (RuntimeModeler.getBooleanSystemProperty("com.sun.xml.ws.legacyWebMethod") == false ? !this.isWebMethodBySpec(method, clazz) : !this.isWebMethod(method)))) continue;
            this.processMethod(method);
        }
        XmlSeeAlso xmlSeeAlso = this.getAnnotation(clazz, XmlSeeAlso.class);
        if (xmlSeeAlso != null) {
            this.model.addAdditionalClasses(xmlSeeAlso.value());
        }
    }

    private boolean isWebMethodBySpec(Method method, Class clazz) {
        boolean staticFinal;
        int modifiers = method.getModifiers();
        boolean bl = staticFinal = Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers);
        assert (Modifier.isPublic(modifiers));
        assert (!clazz.isInterface());
        WebMethod webMethod = this.getAnnotation(method, WebMethod.class);
        if (webMethod != null) {
            if (webMethod.exclude()) {
                return false;
            }
            if (staticFinal) {
                throw new RuntimeModelerException(ModelerMessages.localizableRUNTIME_MODELER_WEBMETHOD_MUST_BE_NONSTATICFINAL(method));
            }
            return true;
        }
        if (staticFinal) {
            return false;
        }
        Class<?> declClass = method.getDeclaringClass();
        return this.getAnnotation(declClass, WebService.class) != null;
    }

    private boolean isWebMethod(Method method) {
        int modifiers = method.getModifiers();
        if (Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers)) {
            return false;
        }
        Class<?> clazz = method.getDeclaringClass();
        boolean declHasWebService = this.getAnnotation(clazz, WebService.class) != null;
        WebMethod webMethod = this.getAnnotation(method, WebMethod.class);
        if (webMethod != null && !webMethod.exclude() && declHasWebService) {
            return true;
        }
        return declHasWebService && !this.classUsesWebMethod.contains(clazz);
    }

    protected SOAPBindingImpl createBinding(SOAPBinding soapBinding) {
        SOAPBindingImpl rtSOAPBinding = new SOAPBindingImpl();
        SOAPBinding.Style style = soapBinding != null ? soapBinding.style() : SOAPBinding.Style.DOCUMENT;
        rtSOAPBinding.setStyle(style);
        assert (this.bindingId != null);
        this.model.bindingId = this.bindingId;
        SOAPVersion soapVersion = this.bindingId.getSOAPVersion();
        rtSOAPBinding.setSOAPVersion(soapVersion);
        return rtSOAPBinding;
    }

    public static String getNamespace(@NotNull String packageName) {
        String[] tokens;
        if (packageName.length() == 0) {
            return null;
        }
        StringTokenizer tokenizer = new StringTokenizer(packageName, ".");
        if (tokenizer.countTokens() == 0) {
            tokens = new String[]{};
        } else {
            tokens = new String[tokenizer.countTokens()];
            for (int i = tokenizer.countTokens() - 1; i >= 0; --i) {
                tokens[i] = tokenizer.nextToken();
            }
        }
        StringBuilder namespace = new StringBuilder("http://");
        for (int i = 0; i < tokens.length; ++i) {
            if (i != 0) {
                namespace.append('.');
            }
            namespace.append(tokens[i]);
        }
        namespace.append('/');
        return namespace.toString();
    }

    private boolean isServiceException(Class<?> exception) {
        return EXCEPTION_CLASS.isAssignableFrom(exception) && !RUNTIME_EXCEPTION_CLASS.isAssignableFrom(exception) && !this.isRemoteException(exception);
    }

    private void processMethod(Method method) {
        WSDLBoundOperation bo;
        JavaMethodImpl javaMethod;
        boolean isOneway;
        WebMethod webMethod = this.getAnnotation(method, WebMethod.class);
        if (webMethod != null && webMethod.exclude()) {
            return;
        }
        String methodName = method.getName();
        boolean bl = isOneway = this.getAnnotation(method, Oneway.class) != null;
        if (isOneway) {
            for (Class<?> exception : method.getExceptionTypes()) {
                if (!this.isServiceException(exception)) continue;
                throw new RuntimeModelerException("runtime.modeler.oneway.operation.no.checked.exceptions", this.portClass.getCanonicalName(), methodName, exception.getName());
            }
        }
        if (method.getDeclaringClass() == this.portClass) {
            javaMethod = new JavaMethodImpl(this.model, method, method, this.metadataReader);
        } else {
            try {
                Method tmpMethod = this.portClass.getMethod(method.getName(), method.getParameterTypes());
                javaMethod = new JavaMethodImpl(this.model, tmpMethod, method, this.metadataReader);
            }
            catch (NoSuchMethodException e) {
                throw new RuntimeModelerException("runtime.modeler.method.not.found", method.getName(), this.portClass.getName());
            }
        }
        MEP mep = this.getMEP(method);
        javaMethod.setMEP(mep);
        String action = null;
        String operationName = method.getName();
        if (webMethod != null) {
            action = webMethod.action();
            String string = operationName = webMethod.operationName().length() > 0 ? webMethod.operationName() : operationName;
        }
        if (this.binding != null && (bo = this.binding.getBinding().get(new QName(this.targetNamespace, operationName))) != null) {
            WSDLInput wsdlInput = bo.getOperation().getInput();
            String wsaAction = wsdlInput.getAction();
            action = wsaAction != null && !wsdlInput.isDefaultAction() ? wsaAction : bo.getSOAPAction();
        }
        javaMethod.setOperationQName(new QName(this.targetNamespace, operationName));
        SOAPBinding methodBinding = this.getAnnotation(method, SOAPBinding.class);
        if (methodBinding != null && methodBinding.style() == SOAPBinding.Style.RPC) {
            logger.warning(ModelerMessages.RUNTIMEMODELER_INVALID_SOAPBINDING_ON_METHOD(methodBinding, method.getName(), method.getDeclaringClass().getName()));
        } else if (methodBinding == null && !method.getDeclaringClass().equals(this.portClass) && (methodBinding = this.getAnnotation(method.getDeclaringClass(), SOAPBinding.class)) != null && methodBinding.style() == SOAPBinding.Style.RPC && methodBinding.parameterStyle() == SOAPBinding.ParameterStyle.BARE) {
            throw new RuntimeModelerException("runtime.modeler.invalid.soapbinding.parameterstyle", methodBinding, method.getDeclaringClass());
        }
        if (methodBinding != null && this.defaultBinding.getStyle() != methodBinding.style()) {
            throw new RuntimeModelerException("runtime.modeler.soapbinding.conflict", methodBinding.style(), method.getName(), this.defaultBinding.getStyle());
        }
        boolean methodIsWrapped = this.isWrapped;
        SOAPBinding.Style style = this.defaultBinding.getStyle();
        if (methodBinding != null) {
            SOAPBindingImpl mySOAPBinding = this.createBinding(methodBinding);
            style = mySOAPBinding.getStyle();
            if (action != null) {
                mySOAPBinding.setSOAPAction(action);
            }
            methodIsWrapped = methodBinding.parameterStyle().equals((Object)SOAPBinding.ParameterStyle.WRAPPED);
            javaMethod.setBinding(mySOAPBinding);
        } else {
            SOAPBindingImpl sb = new SOAPBindingImpl(this.defaultBinding);
            if (action != null) {
                sb.setSOAPAction(action);
            } else {
                String defaults = SOAPVersion.SOAP_11 == sb.getSOAPVersion() ? "" : null;
                sb.setSOAPAction(defaults);
            }
            javaMethod.setBinding(sb);
        }
        if (!methodIsWrapped) {
            this.processDocBareMethod(javaMethod, operationName, method);
        } else if (style.equals((Object)SOAPBinding.Style.DOCUMENT)) {
            this.processDocWrappedMethod(javaMethod, methodName, operationName, method);
        } else {
            this.processRpcMethod(javaMethod, methodName, operationName, method);
        }
        this.model.addJavaMethod(javaMethod);
    }

    private MEP getMEP(Method m) {
        if (this.getAnnotation(m, Oneway.class) != null) {
            return MEP.ONE_WAY;
        }
        if (Response.class.isAssignableFrom(m.getReturnType())) {
            return MEP.ASYNC_POLL;
        }
        if (Future.class.isAssignableFrom(m.getReturnType())) {
            return MEP.ASYNC_CALLBACK;
        }
        return MEP.REQUEST_RESPONSE;
    }

    /*
     * WARNING - void declaration
     */
    protected void processDocWrappedMethod(JavaMethodImpl javaMethod, String methodName, String operationName, Method method) {
        boolean methodHasHeaderParams = false;
        boolean isOneway = this.getAnnotation(method, Oneway.class) != null;
        RequestWrapper reqWrapper = this.getAnnotation(method, RequestWrapper.class);
        ResponseWrapper resWrapper = this.getAnnotation(method, ResponseWrapper.class);
        String beanPackage = this.packageName + PD_JAXWS_PACKAGE_PD;
        if (this.packageName == null || this.packageName.length() == 0) {
            beanPackage = JAXWS_PACKAGE_PD;
        }
        String requestClassName = reqWrapper != null && reqWrapper.className().length() > 0 ? reqWrapper.className() : beanPackage + RuntimeModeler.capitalize(method.getName());
        String responseClassName = resWrapper != null && resWrapper.className().length() > 0 ? resWrapper.className() : beanPackage + RuntimeModeler.capitalize(method.getName()) + RESPONSE;
        String reqName = operationName;
        String reqNamespace = this.targetNamespace;
        String reqPartName = "parameters";
        if (reqWrapper != null) {
            if (reqWrapper.targetNamespace().length() > 0) {
                reqNamespace = reqWrapper.targetNamespace();
            }
            if (reqWrapper.localName().length() > 0) {
                reqName = reqWrapper.localName();
            }
            try {
                if (reqWrapper.partName().length() > 0) {
                    reqPartName = reqWrapper.partName();
                }
            }
            catch (LinkageError linkageError) {
                // empty catch block
            }
        }
        QName reqElementName = new QName(reqNamespace, reqName);
        javaMethod.setRequestPayloadName(reqElementName);
        Class requestClass = this.getRequestWrapperClass(requestClassName, method, reqElementName);
        Class responseClass = null;
        String resName = operationName + RESPONSE;
        String resNamespace = this.targetNamespace;
        QName resElementName = null;
        String resPartName = "parameters";
        if (!isOneway) {
            if (resWrapper != null) {
                if (resWrapper.targetNamespace().length() > 0) {
                    resNamespace = resWrapper.targetNamespace();
                }
                if (resWrapper.localName().length() > 0) {
                    resName = resWrapper.localName();
                }
                try {
                    if (resWrapper.partName().length() > 0) {
                        resPartName = resWrapper.partName();
                    }
                }
                catch (LinkageError linkageError) {
                    // empty catch block
                }
            }
            resElementName = new QName(resNamespace, resName);
            responseClass = this.getResponseWrapperClass(responseClassName, method, resElementName);
        }
        TypeInfo typeRef = new TypeInfo(reqElementName, requestClass, new Annotation[0]);
        typeRef.setNillable(false);
        WrapperParameter requestWrapper = new WrapperParameter(javaMethod, typeRef, WebParam.Mode.IN, 0);
        requestWrapper.setPartName(reqPartName);
        requestWrapper.setBinding(ParameterBinding.BODY);
        javaMethod.addParameter(requestWrapper);
        WrapperParameter responseWrapper = null;
        if (!isOneway) {
            typeRef = new TypeInfo(resElementName, responseClass, new Annotation[0]);
            typeRef.setNillable(false);
            responseWrapper = new WrapperParameter(javaMethod, typeRef, WebParam.Mode.OUT, -1);
            javaMethod.addParameter(responseWrapper);
            responseWrapper.setBinding(ParameterBinding.BODY);
        }
        WebResult webResult = this.getAnnotation(method, WebResult.class);
        XmlElement xmlElem = this.getAnnotation(method, XmlElement.class);
        QName resultQName = RuntimeModeler.getReturnQName(method, webResult, xmlElem);
        Class returnType = method.getReturnType();
        boolean isResultHeader = false;
        if (webResult != null) {
            isResultHeader = webResult.header();
            boolean bl = methodHasHeaderParams = isResultHeader || methodHasHeaderParams;
            if (isResultHeader && xmlElem != null) {
                throw new RuntimeModelerException("@XmlElement cannot be specified on method " + method + " as the return value is bound to header", new Object[0]);
            }
            if (resultQName.getNamespaceURI().length() == 0 && webResult.header()) {
                resultQName = new QName(this.targetNamespace, resultQName.getLocalPart());
            }
        }
        if (javaMethod.isAsync()) {
            returnType = this.getAsyncReturnType(method, returnType);
            resultQName = new QName(RETURN);
        }
        resultQName = this.qualifyWrappeeIfNeeded(resultQName, resNamespace);
        if (!isOneway && returnType != null && !returnType.getName().equals("void")) {
            Annotation[] rann = this.getAnnotations(method);
            if (resultQName.getLocalPart() != null) {
                TypeInfo rTypeReference = new TypeInfo(resultQName, returnType, rann);
                this.metadataReader.getProperties(rTypeReference.properties(), method);
                rTypeReference.setGenericType(method.getGenericReturnType());
                ParameterImpl returnParameter = new ParameterImpl(javaMethod, rTypeReference, WebParam.Mode.OUT, -1);
                if (isResultHeader) {
                    returnParameter.setBinding(ParameterBinding.HEADER);
                    javaMethod.addParameter(returnParameter);
                } else {
                    returnParameter.setBinding(ParameterBinding.BODY);
                    responseWrapper.addWrapperChild(returnParameter);
                }
            }
        }
        Class<?>[] parameterTypes = method.getParameterTypes();
        Type[] genericParameterTypes = method.getGenericParameterTypes();
        Annotation[][] pannotations = this.getParamAnnotations(method);
        int pos = 0;
        for (Class<?> clazz : parameterTypes) {
            void var37_39;
            String partName = null;
            String paramName = "arg" + pos;
            boolean isHeader = false;
            if (javaMethod.isAsync() && AsyncHandler.class.isAssignableFrom(clazz)) continue;
            boolean isHolder = HOLDER_CLASS.isAssignableFrom(clazz);
            if (isHolder && clazz == Holder.class) {
                Class clazz2 = RuntimeModeler.erasure(((ParameterizedType)genericParameterTypes[pos]).getActualTypeArguments()[0]);
            }
            WebParam.Mode paramMode = isHolder ? WebParam.Mode.INOUT : WebParam.Mode.IN;
            WebParam webParam = null;
            xmlElem = null;
            for (Annotation annotation : pannotations[pos]) {
                if (annotation.annotationType() == WebParam.class) {
                    webParam = (WebParam)annotation;
                    continue;
                }
                if (annotation.annotationType() != XmlElement.class) continue;
                xmlElem = (XmlElement)annotation;
            }
            QName paramQName = RuntimeModeler.getParameterQName(method, webParam, xmlElem, paramName);
            if (webParam != null) {
                isHeader = webParam.header();
                boolean bl = methodHasHeaderParams = isHeader || methodHasHeaderParams;
                if (isHeader && xmlElem != null) {
                    throw new RuntimeModelerException("@XmlElement cannot be specified on method " + method + " parameter that is bound to header", new Object[0]);
                }
                partName = webParam.partName().length() > 0 ? webParam.partName() : paramQName.getLocalPart();
                if (isHeader && paramQName.getNamespaceURI().equals("")) {
                    paramQName = new QName(this.targetNamespace, paramQName.getLocalPart());
                }
                paramMode = webParam.mode();
                if (isHolder && paramMode == WebParam.Mode.IN) {
                    paramMode = WebParam.Mode.INOUT;
                }
            }
            paramQName = this.qualifyWrappeeIfNeeded(paramQName, reqNamespace);
            typeRef = new TypeInfo(paramQName, (Type)var37_39, pannotations[pos]);
            this.metadataReader.getProperties(typeRef.properties(), method, pos);
            typeRef.setGenericType(genericParameterTypes[pos]);
            ParameterImpl param = new ParameterImpl(javaMethod, typeRef, paramMode, pos++);
            if (isHeader) {
                param.setBinding(ParameterBinding.HEADER);
                javaMethod.addParameter(param);
                param.setPartName(partName);
                continue;
            }
            param.setBinding(ParameterBinding.BODY);
            if (paramMode != WebParam.Mode.OUT) {
                requestWrapper.addWrapperChild(param);
            }
            if (paramMode == WebParam.Mode.IN) continue;
            if (isOneway) {
                throw new RuntimeModelerException("runtime.modeler.oneway.operation.no.out.parameters", this.portClass.getCanonicalName(), methodName);
            }
            responseWrapper.addWrapperChild(param);
        }
        if (methodHasHeaderParams) {
            resPartName = "result";
        }
        if (responseWrapper != null) {
            responseWrapper.setPartName(resPartName);
        }
        this.processExceptions(javaMethod, method);
    }

    private QName qualifyWrappeeIfNeeded(QName resultQName, String ns) {
        boolean qualified;
        Object o = this.config.properties().get(DocWrappeeNamespapceQualified);
        boolean bl = qualified = o != null && o instanceof Boolean ? (Boolean)o : false;
        if (qualified && (resultQName.getNamespaceURI() == null || "".equals(resultQName.getNamespaceURI()))) {
            return new QName(ns, resultQName.getLocalPart());
        }
        return resultQName;
    }

    /*
     * WARNING - void declaration
     */
    protected void processRpcMethod(JavaMethodImpl javaMethod, String methodName, String operationName, Method method) {
        boolean isOneway = this.getAnnotation(method, Oneway.class) != null;
        TreeMap<Integer, ParameterImpl> resRpcParams = new TreeMap<Integer, ParameterImpl>();
        TreeMap<Integer, ParameterImpl> reqRpcParams = new TreeMap<Integer, ParameterImpl>();
        String reqNamespace = this.targetNamespace;
        String respNamespace = this.targetNamespace;
        if (this.binding != null && SOAPBinding.Style.RPC.equals((Object)this.binding.getBinding().getStyle())) {
            QName opQName = new QName(this.binding.getBinding().getPortTypeName().getNamespaceURI(), operationName);
            WSDLBoundOperation op = this.binding.getBinding().get(opQName);
            if (op != null) {
                if (op.getRequestNamespace() != null) {
                    reqNamespace = op.getRequestNamespace();
                }
                if (op.getResponseNamespace() != null) {
                    respNamespace = op.getResponseNamespace();
                }
            }
        }
        QName reqElementName = new QName(reqNamespace, operationName);
        javaMethod.setRequestPayloadName(reqElementName);
        QName resElementName = null;
        if (!isOneway) {
            resElementName = new QName(respNamespace, operationName + RESPONSE);
        }
        Class<WrapperComposite> wrapperType = WrapperComposite.class;
        TypeInfo typeRef = new TypeInfo(reqElementName, (Type)((Object)wrapperType), new Annotation[0]);
        WrapperParameter requestWrapper = new WrapperParameter(javaMethod, typeRef, WebParam.Mode.IN, 0);
        requestWrapper.setInBinding(ParameterBinding.BODY);
        javaMethod.addParameter(requestWrapper);
        WrapperParameter responseWrapper = null;
        if (!isOneway) {
            typeRef = new TypeInfo(resElementName, (Type)((Object)wrapperType), new Annotation[0]);
            responseWrapper = new WrapperParameter(javaMethod, typeRef, WebParam.Mode.OUT, -1);
            responseWrapper.setOutBinding(ParameterBinding.BODY);
            javaMethod.addParameter(responseWrapper);
        }
        Class returnType = method.getReturnType();
        String resultName = RETURN;
        String resultTNS = this.targetNamespace;
        String resultPartName = resultName;
        boolean isResultHeader = false;
        WebResult webResult = this.getAnnotation(method, WebResult.class);
        if (webResult != null) {
            isResultHeader = webResult.header();
            if (webResult.name().length() > 0) {
                resultName = webResult.name();
            }
            if (webResult.partName().length() > 0) {
                resultPartName = webResult.partName();
                if (!isResultHeader) {
                    resultName = resultPartName;
                }
            } else {
                resultPartName = resultName;
            }
            if (webResult.targetNamespace().length() > 0) {
                resultTNS = webResult.targetNamespace();
            }
            isResultHeader = webResult.header();
        }
        QName resultQName = isResultHeader ? new QName(resultTNS, resultName) : new QName(resultName);
        if (javaMethod.isAsync()) {
            returnType = this.getAsyncReturnType(method, returnType);
        }
        if (!isOneway && returnType != null && returnType != Void.TYPE) {
            Annotation[] rann = this.getAnnotations(method);
            TypeInfo rTypeReference = new TypeInfo(resultQName, returnType, rann);
            this.metadataReader.getProperties(rTypeReference.properties(), method);
            rTypeReference.setGenericType(method.getGenericReturnType());
            ParameterImpl returnParameter = new ParameterImpl(javaMethod, rTypeReference, WebParam.Mode.OUT, -1);
            returnParameter.setPartName(resultPartName);
            if (isResultHeader) {
                returnParameter.setBinding(ParameterBinding.HEADER);
                javaMethod.addParameter(returnParameter);
                rTypeReference.setGlobalElement(true);
            } else {
                ParameterBinding rb = this.getBinding(operationName, resultPartName, false, WebParam.Mode.OUT);
                returnParameter.setBinding(rb);
                if (rb.isBody()) {
                    rTypeReference.setGlobalElement(false);
                    WSDLPart p = this.getPart(new QName(this.targetNamespace, operationName), resultPartName, WebParam.Mode.OUT);
                    if (p == null) {
                        resRpcParams.put(resRpcParams.size() + 10000, returnParameter);
                    } else {
                        resRpcParams.put(p.getIndex(), returnParameter);
                    }
                } else {
                    javaMethod.addParameter(returnParameter);
                }
            }
        }
        Class<?>[] parameterTypes = method.getParameterTypes();
        Type[] genericParameterTypes = method.getGenericParameterTypes();
        Annotation[][] pannotations = this.getParamAnnotations(method);
        int pos = 0;
        for (Class<?> clazz : parameterTypes) {
            void var30_33;
            QName paramQName;
            String paramName = "";
            String paramNamespace = "";
            String partName = "";
            boolean isHeader = false;
            if (javaMethod.isAsync() && AsyncHandler.class.isAssignableFrom(clazz)) continue;
            boolean isHolder = HOLDER_CLASS.isAssignableFrom(clazz);
            if (isHolder && clazz == Holder.class) {
                Class clazz2 = RuntimeModeler.erasure(((ParameterizedType)genericParameterTypes[pos]).getActualTypeArguments()[0]);
            }
            WebParam.Mode paramMode = isHolder ? WebParam.Mode.INOUT : WebParam.Mode.IN;
            for (Annotation annotation : pannotations[pos]) {
                if (annotation.annotationType() != WebParam.class) continue;
                WebParam webParam = (WebParam)annotation;
                paramName = webParam.name();
                partName = webParam.partName();
                isHeader = webParam.header();
                WebParam.Mode mode = webParam.mode();
                paramNamespace = webParam.targetNamespace();
                if (isHolder && mode == WebParam.Mode.IN) {
                    mode = WebParam.Mode.INOUT;
                }
                paramMode = mode;
                break;
            }
            if (paramName.length() == 0) {
                paramName = "arg" + pos;
            }
            if (partName.length() == 0) {
                partName = paramName;
            } else if (!isHeader) {
                paramName = partName;
            }
            if (partName.length() == 0) {
                partName = paramName;
            }
            if (!isHeader) {
                paramQName = new QName("", paramName);
            } else {
                if (paramNamespace.length() == 0) {
                    paramNamespace = this.targetNamespace;
                }
                paramQName = new QName(paramNamespace, paramName);
            }
            typeRef = new TypeInfo(paramQName, (Type)var30_33, pannotations[pos]);
            this.metadataReader.getProperties(typeRef.properties(), method, pos);
            typeRef.setGenericType(genericParameterTypes[pos]);
            ParameterImpl param = new ParameterImpl(javaMethod, typeRef, paramMode, pos++);
            param.setPartName(partName);
            if (paramMode == WebParam.Mode.INOUT) {
                ParameterBinding pb = this.getBinding(operationName, partName, isHeader, WebParam.Mode.IN);
                param.setInBinding(pb);
                pb = this.getBinding(operationName, partName, isHeader, WebParam.Mode.OUT);
                param.setOutBinding(pb);
            } else if (isHeader) {
                typeRef.setGlobalElement(true);
                param.setBinding(ParameterBinding.HEADER);
            } else {
                ParameterBinding pb = this.getBinding(operationName, partName, false, paramMode);
                param.setBinding(pb);
            }
            if (param.getInBinding().isBody()) {
                typeRef.setGlobalElement(false);
                if (!param.isOUT()) {
                    WSDLPart p = this.getPart(new QName(this.targetNamespace, operationName), partName, WebParam.Mode.IN);
                    if (p == null) {
                        reqRpcParams.put(reqRpcParams.size() + 10000, param);
                    } else {
                        reqRpcParams.put(param.getIndex(), param);
                    }
                }
                if (param.isIN()) continue;
                if (isOneway) {
                    throw new RuntimeModelerException("runtime.modeler.oneway.operation.no.out.parameters", this.portClass.getCanonicalName(), methodName);
                }
                WSDLPart p = this.getPart(new QName(this.targetNamespace, operationName), partName, WebParam.Mode.OUT);
                if (p == null) {
                    resRpcParams.put(resRpcParams.size() + 10000, param);
                    continue;
                }
                resRpcParams.put(p.getIndex(), param);
                continue;
            }
            javaMethod.addParameter(param);
        }
        for (ParameterImpl p : reqRpcParams.values()) {
            requestWrapper.addWrapperChild(p);
        }
        for (ParameterImpl p : resRpcParams.values()) {
            responseWrapper.addWrapperChild(p);
        }
        this.processExceptions(javaMethod, method);
    }

    protected void processExceptions(JavaMethodImpl javaMethod, Method method) {
        Action actionAnn = this.getAnnotation(method, Action.class);
        FaultAction[] faultActions = new FaultAction[]{};
        if (actionAnn != null) {
            faultActions = actionAnn.fault();
        }
        for (Class<?> exception : method.getExceptionTypes()) {
            Annotation[] anns;
            Class exceptionBean;
            if (!EXCEPTION_CLASS.isAssignableFrom(exception) || RUNTIME_EXCEPTION_CLASS.isAssignableFrom(exception) || this.isRemoteException(exception) || this.getAnnotation(exception, XmlTransient.class) != null) continue;
            WebFault webFault = this.getAnnotation(exception, WebFault.class);
            Method faultInfoMethod = this.getWSDLExceptionFaultInfo(exception);
            ExceptionType exceptionType = ExceptionType.WSDLException;
            String namespace = this.targetNamespace;
            String name = exception.getSimpleName();
            String beanPackage = this.packageName + PD_JAXWS_PACKAGE_PD;
            if (this.packageName.length() == 0) {
                beanPackage = JAXWS_PACKAGE_PD;
            }
            String className = beanPackage + name + BEAN;
            String messageName = exception.getSimpleName();
            if (webFault != null) {
                if (webFault.faultBean().length() > 0) {
                    className = webFault.faultBean();
                }
                if (webFault.name().length() > 0) {
                    name = webFault.name();
                }
                if (webFault.targetNamespace().length() > 0) {
                    namespace = webFault.targetNamespace();
                }
                if (webFault.messageName().length() > 0) {
                    messageName = webFault.messageName();
                }
            }
            if (faultInfoMethod == null) {
                exceptionBean = this.getExceptionBeanClass(className, exception, name, namespace);
                exceptionType = ExceptionType.UserDefined;
                anns = this.getAnnotations(exceptionBean);
            } else {
                exceptionBean = faultInfoMethod.getReturnType();
                anns = this.getAnnotations(faultInfoMethod);
            }
            QName faultName = new QName(namespace, name);
            TypeInfo typeRef = new TypeInfo(faultName, exceptionBean, anns);
            CheckedExceptionImpl checkedException = new CheckedExceptionImpl(javaMethod, exception, typeRef, exceptionType);
            checkedException.setMessageName(messageName);
            checkedException.setFaultInfoGetter(faultInfoMethod);
            for (FaultAction fa : faultActions) {
                if (!fa.className().equals(exception) || fa.value().equals("")) continue;
                checkedException.setFaultAction(fa.value());
                break;
            }
            javaMethod.addException(checkedException);
        }
    }

    protected Method getWSDLExceptionFaultInfo(Class exception) {
        if (this.getAnnotation(exception, WebFault.class) == null) {
            return null;
        }
        try {
            return exception.getMethod("getFaultInfo", new Class[0]);
        }
        catch (NoSuchMethodException e) {
            return null;
        }
    }

    /*
     * WARNING - void declaration
     */
    protected void processDocBareMethod(JavaMethodImpl javaMethod, String operationName, Method method) {
        String resultName = operationName + RESPONSE;
        String resultTNS = this.targetNamespace;
        String resultPartName = null;
        boolean isResultHeader = false;
        WebResult webResult = this.getAnnotation(method, WebResult.class);
        if (webResult != null) {
            if (webResult.name().length() > 0) {
                resultName = webResult.name();
            }
            if (webResult.targetNamespace().length() > 0) {
                resultTNS = webResult.targetNamespace();
            }
            resultPartName = webResult.partName();
            isResultHeader = webResult.header();
        }
        Class returnType = method.getReturnType();
        Type gReturnType = method.getGenericReturnType();
        if (javaMethod.isAsync()) {
            returnType = this.getAsyncReturnType(method, returnType);
        }
        if (returnType != null && !returnType.getName().equals("void")) {
            Annotation[] rann = this.getAnnotations(method);
            if (resultName != null) {
                QName responseQName = new QName(resultTNS, resultName);
                TypeInfo rTypeReference = new TypeInfo(responseQName, returnType, rann);
                rTypeReference.setGenericType(gReturnType);
                this.metadataReader.getProperties(rTypeReference.properties(), method);
                ParameterImpl returnParameter = new ParameterImpl(javaMethod, rTypeReference, WebParam.Mode.OUT, -1);
                if (resultPartName == null || resultPartName.length() == 0) {
                    resultPartName = resultName;
                }
                returnParameter.setPartName(resultPartName);
                if (isResultHeader) {
                    returnParameter.setBinding(ParameterBinding.HEADER);
                } else {
                    ParameterBinding rb = this.getBinding(operationName, resultPartName, false, WebParam.Mode.OUT);
                    returnParameter.setBinding(rb);
                }
                javaMethod.addParameter(returnParameter);
            }
        }
        Class<?>[] parameterTypes = method.getParameterTypes();
        Type[] genericParameterTypes = method.getGenericParameterTypes();
        Annotation[][] pannotations = this.getParamAnnotations(method);
        int pos = 0;
        for (Class<?> clazz : parameterTypes) {
            ParameterBinding pb;
            void var18_19;
            String paramName = operationName;
            String partName = null;
            String requestNamespace = this.targetNamespace;
            boolean isHeader = false;
            if (javaMethod.isAsync() && AsyncHandler.class.isAssignableFrom(clazz)) continue;
            boolean isHolder = HOLDER_CLASS.isAssignableFrom(clazz);
            if (isHolder && clazz == Holder.class) {
                Class clazz2 = RuntimeModeler.erasure(((ParameterizedType)genericParameterTypes[pos]).getActualTypeArguments()[0]);
            }
            WebParam.Mode paramMode = isHolder ? WebParam.Mode.INOUT : WebParam.Mode.IN;
            for (Annotation annotation : pannotations[pos]) {
                if (annotation.annotationType() != WebParam.class) continue;
                WebParam webParam = (WebParam)annotation;
                paramMode = webParam.mode();
                if (isHolder && paramMode == WebParam.Mode.IN) {
                    paramMode = WebParam.Mode.INOUT;
                }
                if (isHeader = webParam.header()) {
                    paramName = "arg" + pos;
                }
                if (paramMode == WebParam.Mode.OUT && !isHeader) {
                    paramName = operationName + RESPONSE;
                }
                if (webParam.name().length() > 0) {
                    paramName = webParam.name();
                }
                partName = webParam.partName();
                if (webParam.targetNamespace().equals("")) break;
                requestNamespace = webParam.targetNamespace();
                break;
            }
            QName requestQName = new QName(requestNamespace, paramName);
            if (!isHeader && paramMode != WebParam.Mode.OUT) {
                javaMethod.setRequestPayloadName(requestQName);
            }
            TypeInfo typeRef = new TypeInfo(requestQName, (Type)var18_19, pannotations[pos]);
            this.metadataReader.getProperties(typeRef.properties(), method, pos);
            typeRef.setGenericType(genericParameterTypes[pos]);
            ParameterImpl param = new ParameterImpl(javaMethod, typeRef, paramMode, pos++);
            if (partName == null || partName.length() == 0) {
                partName = paramName;
            }
            param.setPartName(partName);
            if (paramMode == WebParam.Mode.INOUT) {
                pb = this.getBinding(operationName, partName, isHeader, WebParam.Mode.IN);
                param.setInBinding(pb);
                pb = this.getBinding(operationName, partName, isHeader, WebParam.Mode.OUT);
                param.setOutBinding(pb);
            } else if (isHeader) {
                param.setBinding(ParameterBinding.HEADER);
            } else {
                pb = this.getBinding(operationName, partName, false, paramMode);
                param.setBinding(pb);
            }
            javaMethod.addParameter(param);
        }
        this.validateDocBare(javaMethod);
        this.processExceptions(javaMethod, method);
    }

    private void validateDocBare(JavaMethodImpl javaMethod) {
        int numInBodyBindings = 0;
        for (Parameter parameter : javaMethod.getRequestParameters()) {
            if (parameter.getBinding().equals(ParameterBinding.BODY) && parameter.isIN()) {
                ++numInBodyBindings;
            }
            if (numInBodyBindings <= true) continue;
            throw new RuntimeModelerException(ModelerMessages.localizableNOT_A_VALID_BARE_METHOD(this.portClass.getName(), javaMethod.getMethod().getName()));
        }
        int numOutBodyBindings = 0;
        for (Parameter parameter : javaMethod.getResponseParameters()) {
            if (parameter.getBinding().equals(ParameterBinding.BODY) && parameter.isOUT()) {
                ++numOutBodyBindings;
            }
            if (numOutBodyBindings <= true) continue;
            throw new RuntimeModelerException(ModelerMessages.localizableNOT_A_VALID_BARE_METHOD(this.portClass.getName(), javaMethod.getMethod().getName()));
        }
    }

    private Class getAsyncReturnType(Method method, Class returnType) {
        if (Response.class.isAssignableFrom(returnType)) {
            Type ret = method.getGenericReturnType();
            return RuntimeModeler.erasure(((ParameterizedType)ret).getActualTypeArguments()[0]);
        }
        Type[] types = method.getGenericParameterTypes();
        Class<?>[] params = method.getParameterTypes();
        int i = 0;
        for (Class<?> cls : params) {
            if (AsyncHandler.class.isAssignableFrom(cls)) {
                return RuntimeModeler.erasure(((ParameterizedType)types[i]).getActualTypeArguments()[0]);
            }
            ++i;
        }
        return returnType;
    }

    public static String capitalize(String name) {
        if (name == null || name.length() == 0) {
            return name;
        }
        char[] chars = name.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        return new String(chars);
    }

    public static QName getServiceName(Class<?> implClass) {
        return RuntimeModeler.getServiceName(implClass, null);
    }

    public static QName getServiceName(Class<?> implClass, boolean isStandard) {
        return RuntimeModeler.getServiceName(implClass, null, isStandard);
    }

    public static QName getServiceName(Class<?> implClass, MetadataReader reader) {
        return RuntimeModeler.getServiceName(implClass, reader, true);
    }

    public static QName getServiceName(Class<?> implClass, MetadataReader reader, boolean isStandard) {
        if (implClass.isInterface()) {
            throw new RuntimeModelerException("runtime.modeler.cannot.get.serviceName.from.interface", implClass.getCanonicalName());
        }
        String name = implClass.getSimpleName() + SERVICE;
        String packageName = "";
        if (implClass.getPackage() != null) {
            packageName = implClass.getPackage().getName();
        }
        WebService webService = RuntimeModeler.getAnnotation(WebService.class, implClass, reader);
        if (isStandard && webService == null) {
            throw new RuntimeModelerException("runtime.modeler.no.webservice.annotation", implClass.getCanonicalName());
        }
        if (webService != null && webService.serviceName().length() > 0) {
            name = webService.serviceName();
        }
        String targetNamespace = RuntimeModeler.getNamespace(packageName);
        if (webService != null && webService.targetNamespace().length() > 0) {
            targetNamespace = webService.targetNamespace();
        } else if (targetNamespace == null) {
            throw new RuntimeModelerException("runtime.modeler.no.package", implClass.getName());
        }
        return new QName(targetNamespace, name);
    }

    public static QName getPortName(Class<?> implClass, String targetNamespace) {
        return RuntimeModeler.getPortName(implClass, null, targetNamespace);
    }

    public static QName getPortName(Class<?> implClass, String targetNamespace, boolean isStandard) {
        return RuntimeModeler.getPortName(implClass, null, targetNamespace, isStandard);
    }

    public static QName getPortName(Class<?> implClass, MetadataReader reader, String targetNamespace) {
        return RuntimeModeler.getPortName(implClass, reader, targetNamespace, true);
    }

    public static QName getPortName(Class<?> implClass, MetadataReader reader, String targetNamespace, boolean isStandard) {
        WebService webService = RuntimeModeler.getAnnotation(WebService.class, implClass, reader);
        if (isStandard && webService == null) {
            throw new RuntimeModelerException("runtime.modeler.no.webservice.annotation", implClass.getCanonicalName());
        }
        String name = webService != null && webService.portName().length() > 0 ? webService.portName() : (webService != null && webService.name().length() > 0 ? webService.name() + PORT : implClass.getSimpleName() + PORT);
        if (targetNamespace == null) {
            if (webService != null && webService.targetNamespace().length() > 0) {
                targetNamespace = webService.targetNamespace();
            } else {
                String packageName = null;
                if (implClass.getPackage() != null) {
                    packageName = implClass.getPackage().getName();
                }
                if (packageName != null) {
                    targetNamespace = RuntimeModeler.getNamespace(packageName);
                }
                if (targetNamespace == null) {
                    throw new RuntimeModelerException("runtime.modeler.no.package", implClass.getName());
                }
            }
        }
        return new QName(targetNamespace, name);
    }

    static <A extends Annotation> A getAnnotation(Class<A> t, Class<?> cls, MetadataReader reader) {
        return reader == null ? cls.getAnnotation(t) : reader.getAnnotation(t, cls);
    }

    public static QName getPortTypeName(Class<?> implOrSeiClass) {
        return RuntimeModeler.getPortTypeName(implOrSeiClass, null, null);
    }

    public static QName getPortTypeName(Class<?> implOrSeiClass, MetadataReader metadataReader) {
        return RuntimeModeler.getPortTypeName(implOrSeiClass, null, metadataReader);
    }

    public static QName getPortTypeName(Class<?> implOrSeiClass, String tns, MetadataReader reader) {
        String name;
        String epi;
        assert (implOrSeiClass != null);
        WebService webService = RuntimeModeler.getAnnotation(WebService.class, implOrSeiClass, reader);
        Class<?> clazz = implOrSeiClass;
        if (webService == null) {
            throw new RuntimeModelerException("runtime.modeler.no.webservice.annotation", implOrSeiClass.getCanonicalName());
        }
        if (!implOrSeiClass.isInterface() && (epi = webService.endpointInterface()).length() > 0) {
            try {
                clazz = Thread.currentThread().getContextClassLoader().loadClass(epi);
            }
            catch (ClassNotFoundException e) {
                throw new RuntimeModelerException("runtime.modeler.class.not.found", epi);
            }
            WebService ws = RuntimeModeler.getAnnotation(WebService.class, clazz, reader);
            if (ws == null) {
                throw new RuntimeModelerException("runtime.modeler.endpoint.interface.no.webservice", webService.endpointInterface());
            }
        }
        if ((name = (webService = RuntimeModeler.getAnnotation(WebService.class, clazz, reader)).name()).length() == 0) {
            name = clazz.getSimpleName();
        }
        if (tns == null || "".equals(tns.trim())) {
            tns = webService.targetNamespace();
        }
        if (tns.length() == 0) {
            tns = RuntimeModeler.getNamespace(clazz.getPackage().getName());
        }
        if (tns == null) {
            throw new RuntimeModelerException("runtime.modeler.no.package", clazz.getName());
        }
        return new QName(tns, name);
    }

    private ParameterBinding getBinding(String operation, String part, boolean isHeader, WebParam.Mode mode) {
        if (this.binding == null) {
            if (isHeader) {
                return ParameterBinding.HEADER;
            }
            return ParameterBinding.BODY;
        }
        QName opName = new QName(this.binding.getBinding().getPortType().getName().getNamespaceURI(), operation);
        return this.binding.getBinding().getBinding(opName, part, mode);
    }

    private WSDLPart getPart(QName opName, String partName, WebParam.Mode mode) {
        WSDLBoundOperation bo;
        if (this.binding != null && (bo = this.binding.getBinding().get(opName)) != null) {
            return bo.getPart(partName, mode);
        }
        return null;
    }

    private boolean isRemoteException(Class<?> exception) {
        Class<?> c;
        for (c = exception; c != null && !REMOTE_EXCEPTION_CLASS.equals(c.getName()); c = c.getSuperclass()) {
        }
        return c != null;
    }

    private static Boolean getBooleanSystemProperty(final String prop) {
        return AccessController.doPrivileged(new PrivilegedAction<Boolean>(){

            @Override
            public Boolean run() {
                String value = System.getProperty(prop);
                return value != null ? Boolean.valueOf(value) : Boolean.FALSE;
            }
        });
    }

    private static QName getReturnQName(Method method, WebResult webResult, XmlElement xmlElem) {
        String webResultName = null;
        if (webResult != null && webResult.name().length() > 0) {
            webResultName = webResult.name();
        }
        String xmlElemName = null;
        if (xmlElem != null && !xmlElem.name().equals("##default")) {
            xmlElemName = xmlElem.name();
        }
        if (xmlElemName != null && webResultName != null && !xmlElemName.equals(webResultName)) {
            throw new RuntimeModelerException("@XmlElement(name)=" + xmlElemName + " and @WebResult(name)=" + webResultName + " are different for method " + method, new Object[0]);
        }
        String localPart = RETURN;
        if (webResultName != null) {
            localPart = webResultName;
        } else if (xmlElemName != null) {
            localPart = xmlElemName;
        }
        String webResultNS = null;
        if (webResult != null && webResult.targetNamespace().length() > 0) {
            webResultNS = webResult.targetNamespace();
        }
        String xmlElemNS = null;
        if (xmlElem != null && !xmlElem.namespace().equals("##default")) {
            xmlElemNS = xmlElem.namespace();
        }
        if (xmlElemNS != null && webResultNS != null && !xmlElemNS.equals(webResultNS)) {
            throw new RuntimeModelerException("@XmlElement(namespace)=" + xmlElemNS + " and @WebResult(targetNamespace)=" + webResultNS + " are different for method " + method, new Object[0]);
        }
        String ns = "";
        if (webResultNS != null) {
            ns = webResultNS;
        } else if (xmlElemNS != null) {
            ns = xmlElemNS;
        }
        return new QName(ns, localPart);
    }

    private static QName getParameterQName(Method method, WebParam webParam, XmlElement xmlElem, String paramDefault) {
        String webParamName = null;
        if (webParam != null && webParam.name().length() > 0) {
            webParamName = webParam.name();
        }
        String xmlElemName = null;
        if (xmlElem != null && !xmlElem.name().equals("##default")) {
            xmlElemName = xmlElem.name();
        }
        if (xmlElemName != null && webParamName != null && !xmlElemName.equals(webParamName)) {
            throw new RuntimeModelerException("@XmlElement(name)=" + xmlElemName + " and @WebParam(name)=" + webParamName + " are different for method " + method, new Object[0]);
        }
        String localPart = paramDefault;
        if (webParamName != null) {
            localPart = webParamName;
        } else if (xmlElemName != null) {
            localPart = xmlElemName;
        }
        String webParamNS = null;
        if (webParam != null && webParam.targetNamespace().length() > 0) {
            webParamNS = webParam.targetNamespace();
        }
        String xmlElemNS = null;
        if (xmlElem != null && !xmlElem.namespace().equals("##default")) {
            xmlElemNS = xmlElem.namespace();
        }
        if (xmlElemNS != null && webParamNS != null && !xmlElemNS.equals(webParamNS)) {
            throw new RuntimeModelerException("@XmlElement(namespace)=" + xmlElemNS + " and @WebParam(targetNamespace)=" + webParamNS + " are different for method " + method, new Object[0]);
        }
        String ns = "";
        if (webParamNS != null) {
            ns = webParamNS;
        } else if (xmlElemNS != null) {
            ns = xmlElemNS;
        }
        return new QName(ns, localPart);
    }

    public static Class erasure(Type type) {
        return (Class)Utils.REFLECTION_NAVIGATOR.erasure((Object)type);
    }
}

