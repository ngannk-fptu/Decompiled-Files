/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.xml.txw2.TypedXmlWriter
 *  com.sun.xml.ws.policy.Policy
 *  com.sun.xml.ws.policy.PolicyException
 *  com.sun.xml.ws.policy.PolicyMap
 *  com.sun.xml.ws.policy.PolicyMapExtender
 *  com.sun.xml.ws.policy.PolicyMapMutator
 *  com.sun.xml.ws.policy.PolicyMapUtil
 *  com.sun.xml.ws.policy.PolicyMerger
 *  com.sun.xml.ws.policy.PolicySubject
 *  com.sun.xml.ws.policy.privateutil.PolicyLogger
 *  com.sun.xml.ws.policy.sourcemodel.PolicyModelGenerator
 *  com.sun.xml.ws.policy.sourcemodel.PolicyModelMarshaller
 *  com.sun.xml.ws.policy.sourcemodel.PolicySourceModel
 *  com.sun.xml.ws.policy.sourcemodel.wspolicy.NamespaceVersion
 *  com.sun.xml.ws.policy.sourcemodel.wspolicy.XmlToken
 *  com.sun.xml.ws.policy.subject.WsdlBindingSubject
 *  com.sun.xml.ws.policy.subject.WsdlBindingSubject$WsdlMessageType
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.policy.jaxws;

import com.sun.xml.txw2.TypedXmlWriter;
import com.sun.xml.ws.addressing.policy.AddressingPolicyMapConfigurator;
import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.model.CheckedException;
import com.sun.xml.ws.api.model.JavaMethod;
import com.sun.xml.ws.api.model.SEIModel;
import com.sun.xml.ws.api.model.wsdl.WSDLBoundFault;
import com.sun.xml.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.ws.api.model.wsdl.WSDLBoundPortType;
import com.sun.xml.ws.api.model.wsdl.WSDLFault;
import com.sun.xml.ws.api.model.wsdl.WSDLInput;
import com.sun.xml.ws.api.model.wsdl.WSDLMessage;
import com.sun.xml.ws.api.model.wsdl.WSDLOperation;
import com.sun.xml.ws.api.model.wsdl.WSDLOutput;
import com.sun.xml.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.ws.api.model.wsdl.WSDLPortType;
import com.sun.xml.ws.api.model.wsdl.WSDLService;
import com.sun.xml.ws.api.policy.ModelGenerator;
import com.sun.xml.ws.api.policy.PolicyResolver;
import com.sun.xml.ws.api.policy.PolicyResolverFactory;
import com.sun.xml.ws.api.wsdl.writer.WSDLGenExtnContext;
import com.sun.xml.ws.api.wsdl.writer.WSDLGeneratorExtension;
import com.sun.xml.ws.encoding.policy.MtomPolicyMapConfigurator;
import com.sun.xml.ws.policy.Policy;
import com.sun.xml.ws.policy.PolicyException;
import com.sun.xml.ws.policy.PolicyMap;
import com.sun.xml.ws.policy.PolicyMapExtender;
import com.sun.xml.ws.policy.PolicyMapMutator;
import com.sun.xml.ws.policy.PolicyMapUtil;
import com.sun.xml.ws.policy.PolicyMerger;
import com.sun.xml.ws.policy.PolicySubject;
import com.sun.xml.ws.policy.jaxws.PolicyUtil;
import com.sun.xml.ws.policy.jaxws.WSDLBoundFaultContainer;
import com.sun.xml.ws.policy.jaxws.spi.PolicyMapConfigurator;
import com.sun.xml.ws.policy.privateutil.PolicyLogger;
import com.sun.xml.ws.policy.sourcemodel.PolicyModelGenerator;
import com.sun.xml.ws.policy.sourcemodel.PolicyModelMarshaller;
import com.sun.xml.ws.policy.sourcemodel.PolicySourceModel;
import com.sun.xml.ws.policy.sourcemodel.wspolicy.NamespaceVersion;
import com.sun.xml.ws.policy.sourcemodel.wspolicy.XmlToken;
import com.sun.xml.ws.policy.subject.WsdlBindingSubject;
import com.sun.xml.ws.resources.PolicyMessages;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceException;

public class PolicyWSDLGeneratorExtension
extends WSDLGeneratorExtension {
    private static final PolicyLogger LOGGER = PolicyLogger.getLogger(PolicyWSDLGeneratorExtension.class);
    private PolicyMap policyMap;
    private SEIModel seiModel;
    private final Collection<PolicySubject> subjects = new LinkedList<PolicySubject>();
    private final PolicyModelMarshaller marshaller = PolicyModelMarshaller.getXmlMarshaller((boolean)true);
    private final PolicyMerger merger = PolicyMerger.getMerger();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void start(WSDLGenExtnContext context) {
        LOGGER.entering();
        try {
            this.seiModel = context.getModel();
            PolicyMapConfigurator[] policyMapConfigurators = this.loadConfigurators();
            PolicyMapExtender[] extenders = new PolicyMapExtender[policyMapConfigurators.length];
            for (int i = 0; i < policyMapConfigurators.length; ++i) {
                extenders[i] = PolicyMapExtender.createPolicyMapExtender();
            }
            this.policyMap = PolicyResolverFactory.create().resolve(new PolicyResolver.ServerContext(this.policyMap, context.getContainer(), context.getEndpointClass(), false, (PolicyMapMutator[])extenders));
            if (this.policyMap == null) {
                LOGGER.fine(PolicyMessages.WSP_1019_CREATE_EMPTY_POLICY_MAP());
                this.policyMap = PolicyMap.createPolicyMap(Arrays.asList(extenders));
            }
            WSBinding binding = context.getBinding();
            try {
                LinkedList<PolicySubject> policySubjects = new LinkedList<PolicySubject>();
                for (int i = 0; i < policyMapConfigurators.length; ++i) {
                    policySubjects.addAll(policyMapConfigurators[i].update(this.policyMap, this.seiModel, binding));
                    extenders[i].disconnect();
                }
                PolicyMapUtil.insertPolicies((PolicyMap)this.policyMap, policySubjects, (QName)this.seiModel.getServiceQName(), (QName)this.seiModel.getPortName());
            }
            catch (PolicyException e) {
                throw (WebServiceException)LOGGER.logSevereException((Throwable)new WebServiceException(PolicyMessages.WSP_1017_MAP_UPDATE_FAILED(), (Throwable)e));
            }
            TypedXmlWriter root = context.getRoot();
            root._namespace(NamespaceVersion.v1_2.toString(), NamespaceVersion.v1_2.getDefaultNamespacePrefix());
            root._namespace(NamespaceVersion.v1_5.toString(), NamespaceVersion.v1_5.getDefaultNamespacePrefix());
            root._namespace("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd", "wsu");
        }
        finally {
            LOGGER.exiting();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addDefinitionsExtension(TypedXmlWriter definitions) {
        block11: {
            try {
                LOGGER.entering();
                if (this.policyMap == null) {
                    LOGGER.fine(PolicyMessages.WSP_1009_NOT_MARSHALLING_ANY_POLICIES_POLICY_MAP_IS_NULL());
                    break block11;
                }
                this.subjects.addAll(this.policyMap.getPolicySubjects());
                PolicyModelGenerator generator = ModelGenerator.getGenerator();
                HashSet<String> policyIDsOrNamesWritten = new HashSet<String>();
                for (PolicySubject subject : this.subjects) {
                    Policy policy;
                    if (subject.getSubject() == null) {
                        LOGGER.fine(PolicyMessages.WSP_1008_NOT_MARSHALLING_WSDL_SUBJ_NULL(subject));
                        continue;
                    }
                    try {
                        policy = subject.getEffectivePolicy(this.merger);
                    }
                    catch (PolicyException e) {
                        throw (WebServiceException)LOGGER.logSevereException((Throwable)new WebServiceException(PolicyMessages.WSP_1011_FAILED_TO_RETRIEVE_EFFECTIVE_POLICY_FOR_SUBJECT(subject.toString()), (Throwable)e));
                    }
                    if (null == policy.getIdOrName() || policyIDsOrNamesWritten.contains(policy.getIdOrName())) {
                        LOGGER.fine(PolicyMessages.WSP_1016_POLICY_ID_NULL_OR_DUPLICATE(policy));
                        continue;
                    }
                    try {
                        PolicySourceModel policyInfoset = generator.translate(policy);
                        this.marshaller.marshal(policyInfoset, (Object)definitions);
                    }
                    catch (PolicyException e) {
                        throw (WebServiceException)LOGGER.logSevereException((Throwable)new WebServiceException(PolicyMessages.WSP_1018_FAILED_TO_MARSHALL_POLICY(policy.getIdOrName()), (Throwable)e));
                    }
                    policyIDsOrNamesWritten.add(policy.getIdOrName());
                }
            }
            finally {
                LOGGER.exiting();
            }
        }
    }

    @Override
    public void addServiceExtension(TypedXmlWriter service) {
        LOGGER.entering();
        String serviceName = null == this.seiModel ? null : this.seiModel.getServiceQName().getLocalPart();
        this.selectAndProcessSubject(service, WSDLService.class, ScopeType.SERVICE, serviceName);
        LOGGER.exiting();
    }

    @Override
    public void addPortExtension(TypedXmlWriter port) {
        LOGGER.entering();
        String portName = null == this.seiModel ? null : this.seiModel.getPortName().getLocalPart();
        this.selectAndProcessSubject(port, WSDLPort.class, ScopeType.ENDPOINT, portName);
        LOGGER.exiting();
    }

    @Override
    public void addPortTypeExtension(TypedXmlWriter portType) {
        LOGGER.entering();
        String portTypeName = null == this.seiModel ? null : this.seiModel.getPortTypeName().getLocalPart();
        this.selectAndProcessSubject(portType, WSDLPortType.class, ScopeType.ENDPOINT, portTypeName);
        LOGGER.exiting();
    }

    @Override
    public void addBindingExtension(TypedXmlWriter binding) {
        LOGGER.entering();
        QName bindingName = null == this.seiModel ? null : this.seiModel.getBoundPortTypeName();
        this.selectAndProcessBindingSubject(binding, WSDLBoundPortType.class, ScopeType.ENDPOINT, bindingName);
        LOGGER.exiting();
    }

    @Override
    public void addOperationExtension(TypedXmlWriter operation, JavaMethod method) {
        LOGGER.entering();
        this.selectAndProcessSubject(operation, WSDLOperation.class, ScopeType.OPERATION, (String)null);
        LOGGER.exiting();
    }

    @Override
    public void addBindingOperationExtension(TypedXmlWriter operation, JavaMethod method) {
        LOGGER.entering();
        QName operationName = method == null ? null : new QName(method.getOwner().getTargetNamespace(), method.getOperationName());
        this.selectAndProcessBindingSubject(operation, WSDLBoundOperation.class, ScopeType.OPERATION, operationName);
        LOGGER.exiting();
    }

    @Override
    public void addInputMessageExtension(TypedXmlWriter message, JavaMethod method) {
        LOGGER.entering();
        String messageName = null == method ? null : method.getRequestMessageName();
        this.selectAndProcessSubject(message, WSDLMessage.class, ScopeType.INPUT_MESSAGE, messageName);
        LOGGER.exiting();
    }

    @Override
    public void addOutputMessageExtension(TypedXmlWriter message, JavaMethod method) {
        LOGGER.entering();
        String messageName = null == method ? null : method.getResponseMessageName();
        this.selectAndProcessSubject(message, WSDLMessage.class, ScopeType.OUTPUT_MESSAGE, messageName);
        LOGGER.exiting();
    }

    @Override
    public void addFaultMessageExtension(TypedXmlWriter message, JavaMethod method, CheckedException exception) {
        LOGGER.entering();
        String messageName = null == exception ? null : exception.getMessageName();
        this.selectAndProcessSubject(message, WSDLMessage.class, ScopeType.FAULT_MESSAGE, messageName);
        LOGGER.exiting();
    }

    @Override
    public void addOperationInputExtension(TypedXmlWriter input, JavaMethod method) {
        LOGGER.entering();
        String messageName = null == method ? null : method.getRequestMessageName();
        this.selectAndProcessSubject(input, WSDLInput.class, ScopeType.INPUT_MESSAGE, messageName);
        LOGGER.exiting();
    }

    @Override
    public void addOperationOutputExtension(TypedXmlWriter output, JavaMethod method) {
        LOGGER.entering();
        String messageName = null == method ? null : method.getResponseMessageName();
        this.selectAndProcessSubject(output, WSDLOutput.class, ScopeType.OUTPUT_MESSAGE, messageName);
        LOGGER.exiting();
    }

    @Override
    public void addOperationFaultExtension(TypedXmlWriter fault, JavaMethod method, CheckedException exception) {
        LOGGER.entering();
        String messageName = null == exception ? null : exception.getMessageName();
        this.selectAndProcessSubject(fault, WSDLFault.class, ScopeType.FAULT_MESSAGE, messageName);
        LOGGER.exiting();
    }

    @Override
    public void addBindingOperationInputExtension(TypedXmlWriter input, JavaMethod method) {
        LOGGER.entering();
        QName operationName = new QName(method.getOwner().getTargetNamespace(), method.getOperationName());
        this.selectAndProcessBindingSubject(input, WSDLBoundOperation.class, ScopeType.INPUT_MESSAGE, operationName);
        LOGGER.exiting();
    }

    @Override
    public void addBindingOperationOutputExtension(TypedXmlWriter output, JavaMethod method) {
        LOGGER.entering();
        QName operationName = new QName(method.getOwner().getTargetNamespace(), method.getOperationName());
        this.selectAndProcessBindingSubject(output, WSDLBoundOperation.class, ScopeType.OUTPUT_MESSAGE, operationName);
        LOGGER.exiting();
    }

    @Override
    public void addBindingOperationFaultExtension(TypedXmlWriter writer, JavaMethod method, CheckedException exception) {
        LOGGER.entering(new Object[]{writer, method, exception});
        if (this.subjects != null) {
            for (PolicySubject subject : this.subjects) {
                WsdlBindingSubject wsdlSubject;
                String exceptionName;
                Object concreteSubject;
                if (!this.policyMap.isFaultMessageSubject(subject) || (concreteSubject = subject.getSubject()) == null) continue;
                String string = exceptionName = exception == null ? null : exception.getMessageName();
                if (exceptionName == null) {
                    this.writePolicyOrReferenceIt(subject, writer);
                }
                if (WSDLBoundFaultContainer.class.isInstance(concreteSubject)) {
                    WSDLBoundFaultContainer faultContainer = (WSDLBoundFaultContainer)concreteSubject;
                    WSDLBoundFault fault = faultContainer.getBoundFault();
                    WSDLBoundOperation operation = faultContainer.getBoundOperation();
                    if (!exceptionName.equals(fault.getName()) || !operation.getName().getLocalPart().equals(method.getOperationName())) continue;
                    this.writePolicyOrReferenceIt(subject, writer);
                    continue;
                }
                if (!WsdlBindingSubject.class.isInstance(concreteSubject) || (wsdlSubject = (WsdlBindingSubject)concreteSubject).getMessageType() != WsdlBindingSubject.WsdlMessageType.FAULT || !exception.getOwner().getTargetNamespace().equals(wsdlSubject.getName().getNamespaceURI()) || !exceptionName.equals(wsdlSubject.getName().getLocalPart())) continue;
                this.writePolicyOrReferenceIt(subject, writer);
            }
        }
        LOGGER.exiting();
    }

    private void selectAndProcessSubject(TypedXmlWriter xmlWriter, Class clazz, ScopeType scopeType, QName bindingName) {
        LOGGER.entering(new Object[]{xmlWriter, clazz, scopeType, bindingName});
        if (bindingName == null) {
            this.selectAndProcessSubject(xmlWriter, clazz, scopeType, (String)null);
        } else {
            if (this.subjects != null) {
                for (PolicySubject subject : this.subjects) {
                    if (!bindingName.equals(subject.getSubject())) continue;
                    this.writePolicyOrReferenceIt(subject, xmlWriter);
                }
            }
            this.selectAndProcessSubject(xmlWriter, clazz, scopeType, bindingName.getLocalPart());
        }
        LOGGER.exiting();
    }

    private void selectAndProcessBindingSubject(TypedXmlWriter xmlWriter, Class clazz, ScopeType scopeType, QName bindingName) {
        LOGGER.entering(new Object[]{xmlWriter, clazz, scopeType, bindingName});
        if (this.subjects != null && bindingName != null) {
            for (PolicySubject subject : this.subjects) {
                WsdlBindingSubject wsdlSubject;
                if (!(subject.getSubject() instanceof WsdlBindingSubject) || !bindingName.equals((wsdlSubject = (WsdlBindingSubject)subject.getSubject()).getName())) continue;
                this.writePolicyOrReferenceIt(subject, xmlWriter);
            }
        }
        this.selectAndProcessSubject(xmlWriter, clazz, scopeType, bindingName);
        LOGGER.exiting();
    }

    private void selectAndProcessSubject(TypedXmlWriter xmlWriter, Class clazz, ScopeType scopeType, String wsdlName) {
        LOGGER.entering(new Object[]{xmlWriter, clazz, scopeType, wsdlName});
        if (this.subjects != null) {
            for (PolicySubject subject : this.subjects) {
                Object concreteSubject;
                if (!PolicyWSDLGeneratorExtension.isCorrectType(this.policyMap, subject, scopeType) || (concreteSubject = subject.getSubject()) == null || !clazz.isInstance(concreteSubject)) continue;
                if (null == wsdlName) {
                    this.writePolicyOrReferenceIt(subject, xmlWriter);
                    continue;
                }
                try {
                    Method getNameMethod = clazz.getDeclaredMethod("getName", new Class[0]);
                    if (!this.stringEqualsToStringOrQName(wsdlName, getNameMethod.invoke(concreteSubject, new Object[0]))) continue;
                    this.writePolicyOrReferenceIt(subject, xmlWriter);
                }
                catch (NoSuchMethodException e) {
                    throw (WebServiceException)LOGGER.logSevereException((Throwable)new WebServiceException(PolicyMessages.WSP_1003_UNABLE_TO_CHECK_ELEMENT_NAME(clazz.getName(), wsdlName), (Throwable)e));
                }
                catch (IllegalAccessException e) {
                    throw (WebServiceException)LOGGER.logSevereException((Throwable)new WebServiceException(PolicyMessages.WSP_1003_UNABLE_TO_CHECK_ELEMENT_NAME(clazz.getName(), wsdlName), (Throwable)e));
                }
                catch (InvocationTargetException e) {
                    throw (WebServiceException)LOGGER.logSevereException((Throwable)new WebServiceException(PolicyMessages.WSP_1003_UNABLE_TO_CHECK_ELEMENT_NAME(clazz.getName(), wsdlName), (Throwable)e));
                }
            }
        }
        LOGGER.exiting();
    }

    private static boolean isCorrectType(PolicyMap map, PolicySubject subject, ScopeType type) {
        switch (type) {
            case OPERATION: {
                return !map.isInputMessageSubject(subject) && !map.isOutputMessageSubject(subject) && !map.isFaultMessageSubject(subject);
            }
            case INPUT_MESSAGE: {
                return map.isInputMessageSubject(subject);
            }
            case OUTPUT_MESSAGE: {
                return map.isOutputMessageSubject(subject);
            }
            case FAULT_MESSAGE: {
                return map.isFaultMessageSubject(subject);
            }
        }
        return true;
    }

    private boolean stringEqualsToStringOrQName(String first, Object second) {
        return second instanceof QName ? first.equals(((QName)second).getLocalPart()) : first.equals(second);
    }

    private void writePolicyOrReferenceIt(PolicySubject subject, TypedXmlWriter writer) {
        Policy policy;
        try {
            policy = subject.getEffectivePolicy(this.merger);
        }
        catch (PolicyException e) {
            throw (WebServiceException)LOGGER.logSevereException((Throwable)new WebServiceException(PolicyMessages.WSP_1011_FAILED_TO_RETRIEVE_EFFECTIVE_POLICY_FOR_SUBJECT(subject.toString()), (Throwable)e));
        }
        if (policy != null) {
            if (null == policy.getIdOrName()) {
                PolicyModelGenerator generator = ModelGenerator.getGenerator();
                try {
                    PolicySourceModel policyInfoset = generator.translate(policy);
                    this.marshaller.marshal(policyInfoset, (Object)writer);
                }
                catch (PolicyException pe) {
                    throw (WebServiceException)LOGGER.logSevereException((Throwable)new WebServiceException(PolicyMessages.WSP_1002_UNABLE_TO_MARSHALL_POLICY_OR_POLICY_REFERENCE(), (Throwable)pe));
                }
            } else {
                TypedXmlWriter policyReference = writer._element(policy.getNamespaceVersion().asQName(XmlToken.PolicyReference), TypedXmlWriter.class);
                policyReference._attribute(XmlToken.Uri.toString(), (Object)('#' + policy.getIdOrName()));
            }
        }
    }

    private PolicyMapConfigurator[] loadConfigurators() {
        LinkedList<PolicyMapConfigurator> configurators = new LinkedList<PolicyMapConfigurator>();
        configurators.add(new AddressingPolicyMapConfigurator());
        configurators.add(new MtomPolicyMapConfigurator());
        PolicyUtil.addServiceProviders(configurators, PolicyMapConfigurator.class);
        return configurators.toArray(new PolicyMapConfigurator[configurators.size()]);
    }

    static enum ScopeType {
        SERVICE,
        ENDPOINT,
        OPERATION,
        INPUT_MESSAGE,
        OUTPUT_MESSAGE,
        FAULT_MESSAGE;

    }
}

