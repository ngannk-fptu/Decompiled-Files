/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.Nullable
 *  com.sun.xml.bind.api.TypeReference
 *  javax.jws.WebMethod
 *  javax.xml.ws.Action
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.model;

import com.sun.istack.Nullable;
import com.sun.xml.bind.api.TypeReference;
import com.sun.xml.ws.api.databinding.MetadataReader;
import com.sun.xml.ws.api.model.JavaMethod;
import com.sun.xml.ws.api.model.MEP;
import com.sun.xml.ws.api.model.SEIModel;
import com.sun.xml.ws.api.model.soap.SOAPBinding;
import com.sun.xml.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.ws.api.model.wsdl.WSDLFault;
import com.sun.xml.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.ws.model.AbstractSEIModelImpl;
import com.sun.xml.ws.model.CheckedExceptionImpl;
import com.sun.xml.ws.model.ParameterImpl;
import com.sun.xml.ws.model.WrapperParameter;
import com.sun.xml.ws.model.soap.SOAPBindingImpl;
import com.sun.xml.ws.spi.db.TypeInfo;
import com.sun.xml.ws.wsdl.ActionBasedOperationSignature;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import javax.jws.WebMethod;
import javax.xml.namespace.QName;
import javax.xml.ws.Action;
import javax.xml.ws.WebServiceException;

public final class JavaMethodImpl
implements JavaMethod {
    private String inputAction = "";
    private String outputAction = "";
    private final List<CheckedExceptionImpl> exceptions = new ArrayList<CheckedExceptionImpl>();
    private final Method method;
    final List<ParameterImpl> requestParams = new ArrayList<ParameterImpl>();
    final List<ParameterImpl> responseParams = new ArrayList<ParameterImpl>();
    private final List<ParameterImpl> unmReqParams = Collections.unmodifiableList(this.requestParams);
    private final List<ParameterImpl> unmResParams = Collections.unmodifiableList(this.responseParams);
    private SOAPBinding binding;
    private MEP mep;
    private QName operationName;
    private WSDLBoundOperation wsdlOperation;
    final AbstractSEIModelImpl owner;
    private final Method seiMethod;
    private QName requestPayloadName;
    private String soapAction;
    private static final Logger LOGGER = Logger.getLogger(JavaMethodImpl.class.getName());

    public JavaMethodImpl(AbstractSEIModelImpl owner, Method method, Method seiMethod, MetadataReader metadataReader) {
        this.owner = owner;
        this.method = method;
        this.seiMethod = seiMethod;
        this.setWsaActions(metadataReader);
    }

    private void setWsaActions(MetadataReader metadataReader) {
        Action action;
        Action action2 = action = metadataReader != null ? metadataReader.getAnnotation(Action.class, this.seiMethod) : this.seiMethod.getAnnotation(Action.class);
        if (action != null) {
            this.inputAction = action.input();
            this.outputAction = action.output();
        }
        WebMethod webMethod = metadataReader != null ? metadataReader.getAnnotation(WebMethod.class, this.seiMethod) : this.seiMethod.getAnnotation(WebMethod.class);
        this.soapAction = "";
        if (webMethod != null) {
            this.soapAction = webMethod.action();
        }
        if (!this.soapAction.equals("")) {
            if (this.inputAction.equals("")) {
                this.inputAction = this.soapAction;
            } else if (!this.inputAction.equals(this.soapAction)) {
                // empty if block
            }
        }
    }

    public ActionBasedOperationSignature getOperationSignature() {
        QName qname = this.getRequestPayloadName();
        if (qname == null) {
            qname = new QName("", "");
        }
        return new ActionBasedOperationSignature(this.getInputAction(), qname);
    }

    @Override
    public SEIModel getOwner() {
        return this.owner;
    }

    @Override
    public Method getMethod() {
        return this.method;
    }

    @Override
    public Method getSEIMethod() {
        return this.seiMethod;
    }

    @Override
    public MEP getMEP() {
        return this.mep;
    }

    void setMEP(MEP mep) {
        this.mep = mep;
    }

    @Override
    public SOAPBinding getBinding() {
        if (this.binding == null) {
            return new SOAPBindingImpl();
        }
        return this.binding;
    }

    void setBinding(SOAPBinding binding) {
        this.binding = binding;
    }

    public WSDLBoundOperation getOperation() {
        return this.wsdlOperation;
    }

    public void setOperationQName(QName name) {
        this.operationName = name;
    }

    public QName getOperationQName() {
        return this.wsdlOperation != null ? this.wsdlOperation.getName() : this.operationName;
    }

    public String getSOAPAction() {
        return this.wsdlOperation != null ? this.wsdlOperation.getSOAPAction() : this.soapAction;
    }

    @Override
    public String getOperationName() {
        return this.operationName.getLocalPart();
    }

    @Override
    public String getRequestMessageName() {
        return this.getOperationName();
    }

    @Override
    public String getResponseMessageName() {
        if (this.mep.isOneWay()) {
            return null;
        }
        return this.getOperationName() + "Response";
    }

    public void setRequestPayloadName(QName n) {
        this.requestPayloadName = n;
    }

    @Override
    @Nullable
    public QName getRequestPayloadName() {
        return this.wsdlOperation != null ? this.wsdlOperation.getRequestPayloadName() : this.requestPayloadName;
    }

    @Override
    @Nullable
    public QName getResponsePayloadName() {
        return this.mep == MEP.ONE_WAY ? null : this.wsdlOperation.getResponsePayloadName();
    }

    public List<ParameterImpl> getRequestParameters() {
        return this.unmReqParams;
    }

    public List<ParameterImpl> getResponseParameters() {
        return this.unmResParams;
    }

    void addParameter(ParameterImpl p) {
        if (p.isIN() || p.isINOUT()) {
            assert (!this.requestParams.contains(p));
            this.requestParams.add(p);
        }
        if (p.isOUT() || p.isINOUT()) {
            assert (!this.responseParams.contains(p));
            this.responseParams.add(p);
        }
    }

    void addRequestParameter(ParameterImpl p) {
        if (p.isIN() || p.isINOUT()) {
            this.requestParams.add(p);
        }
    }

    void addResponseParameter(ParameterImpl p) {
        if (p.isOUT() || p.isINOUT()) {
            this.responseParams.add(p);
        }
    }

    public int getInputParametersCount() {
        int count = 0;
        for (ParameterImpl param : this.requestParams) {
            if (param.isWrapperStyle()) {
                count += ((WrapperParameter)param).getWrapperChildren().size();
                continue;
            }
            ++count;
        }
        for (ParameterImpl param : this.responseParams) {
            if (param.isWrapperStyle()) {
                for (ParameterImpl wc : ((WrapperParameter)param).getWrapperChildren()) {
                    if (wc.isResponse() || !wc.isOUT()) continue;
                    ++count;
                }
                continue;
            }
            if (param.isResponse() || !param.isOUT()) continue;
            ++count;
        }
        return count;
    }

    void addException(CheckedExceptionImpl ce) {
        if (!this.exceptions.contains(ce)) {
            this.exceptions.add(ce);
        }
    }

    public CheckedExceptionImpl getCheckedException(Class exceptionClass) {
        for (CheckedExceptionImpl ce : this.exceptions) {
            if (ce.getExceptionClass() != exceptionClass) continue;
            return ce;
        }
        return null;
    }

    public List<CheckedExceptionImpl> getCheckedExceptions() {
        return Collections.unmodifiableList(this.exceptions);
    }

    public String getInputAction() {
        return this.inputAction;
    }

    public String getOutputAction() {
        return this.outputAction;
    }

    public CheckedExceptionImpl getCheckedException(TypeReference detailType) {
        for (CheckedExceptionImpl ce : this.exceptions) {
            TypeInfo actual = ce.getDetailType();
            if (!actual.tagName.equals(detailType.tagName) || actual.type != detailType.type) continue;
            return ce;
        }
        return null;
    }

    public boolean isAsync() {
        return this.mep.isAsync;
    }

    void freeze(WSDLPort portType) {
        this.wsdlOperation = portType.getBinding().get(new QName(portType.getBinding().getPortType().getName().getNamespaceURI(), this.getOperationName()));
        if (this.wsdlOperation == null) {
            throw new WebServiceException("Method " + this.seiMethod.getName() + " is exposed as WebMethod, but there is no corresponding wsdl operation with name " + this.operationName + " in the wsdl:portType" + portType.getBinding().getPortType().getName());
        }
        if (this.inputAction.equals("")) {
            this.inputAction = this.wsdlOperation.getOperation().getInput().getAction();
        } else if (!this.inputAction.equals(this.wsdlOperation.getOperation().getInput().getAction())) {
            LOGGER.warning("Input Action on WSDL operation " + this.wsdlOperation.getName().getLocalPart() + " and @Action on its associated Web Method " + this.seiMethod.getName() + " did not match and will cause problems in dispatching the requests");
        }
        if (!this.mep.isOneWay()) {
            if (this.outputAction.equals("")) {
                this.outputAction = this.wsdlOperation.getOperation().getOutput().getAction();
            }
            for (CheckedExceptionImpl ce : this.exceptions) {
                if (!ce.getFaultAction().equals("")) continue;
                QName detailQName = ce.getDetailType().tagName;
                WSDLFault wsdlfault = this.wsdlOperation.getOperation().getFault(detailQName);
                if (wsdlfault == null) {
                    LOGGER.warning("Mismatch between Java model and WSDL model found, For wsdl operation " + this.wsdlOperation.getName() + ",There is no matching wsdl fault with detail QName " + ce.getDetailType().tagName);
                    ce.setFaultAction(ce.getDefaultFaultAction());
                    continue;
                }
                ce.setFaultAction(wsdlfault.getAction());
            }
        }
    }

    final void fillTypes(List<TypeInfo> types) {
        this.fillTypes(this.requestParams, types);
        this.fillTypes(this.responseParams, types);
        for (CheckedExceptionImpl ce : this.exceptions) {
            types.add(ce.getDetailType());
        }
    }

    private void fillTypes(List<ParameterImpl> params, List<TypeInfo> types) {
        for (ParameterImpl p : params) {
            p.fillTypes(types);
        }
    }
}

