/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 *  javax.jws.WebParam$Mode
 *  javax.jws.soap.SOAPBinding$Style
 */
package com.sun.xml.ws.model.wsdl;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.ws.api.model.ParameterBinding;
import com.sun.xml.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLBoundFault;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLBoundOperation;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLBoundPortType;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLMessage;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLModel;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLOperation;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLPart;
import com.sun.xml.ws.model.wsdl.AbstractExtensibleImpl;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.jws.WebParam;
import javax.jws.soap.SOAPBinding;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

public final class WSDLBoundOperationImpl
extends AbstractExtensibleImpl
implements EditableWSDLBoundOperation {
    private final QName name;
    private final Map<String, ParameterBinding> inputParts;
    private final Map<String, ParameterBinding> outputParts;
    private final Map<String, ParameterBinding> faultParts;
    private final Map<String, String> inputMimeTypes;
    private final Map<String, String> outputMimeTypes;
    private final Map<String, String> faultMimeTypes;
    private boolean explicitInputSOAPBodyParts = false;
    private boolean explicitOutputSOAPBodyParts = false;
    private boolean explicitFaultSOAPBodyParts = false;
    private Boolean emptyInputBody;
    private Boolean emptyOutputBody;
    private Boolean emptyFaultBody;
    private final Map<String, EditableWSDLPart> inParts;
    private final Map<String, EditableWSDLPart> outParts;
    private final List<EditableWSDLBoundFault> wsdlBoundFaults;
    private EditableWSDLOperation operation;
    private String soapAction;
    private WSDLBoundOperation.ANONYMOUS anonymous;
    private final EditableWSDLBoundPortType owner;
    private SOAPBinding.Style style = SOAPBinding.Style.DOCUMENT;
    private String reqNamespace;
    private String respNamespace;
    private QName requestPayloadName;
    private QName responsePayloadName;
    private boolean emptyRequestPayload;
    private boolean emptyResponsePayload;
    private Map<QName, ? extends EditableWSDLMessage> messages;

    public WSDLBoundOperationImpl(XMLStreamReader xsr, EditableWSDLBoundPortType owner, QName name) {
        super(xsr);
        this.name = name;
        this.inputParts = new HashMap<String, ParameterBinding>();
        this.outputParts = new HashMap<String, ParameterBinding>();
        this.faultParts = new HashMap<String, ParameterBinding>();
        this.inputMimeTypes = new HashMap<String, String>();
        this.outputMimeTypes = new HashMap<String, String>();
        this.faultMimeTypes = new HashMap<String, String>();
        this.inParts = new HashMap<String, EditableWSDLPart>();
        this.outParts = new HashMap<String, EditableWSDLPart>();
        this.wsdlBoundFaults = new ArrayList<EditableWSDLBoundFault>();
        this.owner = owner;
    }

    @Override
    public QName getName() {
        return this.name;
    }

    @Override
    public String getSOAPAction() {
        return this.soapAction;
    }

    @Override
    public void setSoapAction(String soapAction) {
        this.soapAction = soapAction != null ? soapAction : "";
    }

    @Override
    public EditableWSDLPart getPart(String partName, WebParam.Mode mode) {
        if (mode == WebParam.Mode.IN) {
            return this.inParts.get(partName);
        }
        if (mode == WebParam.Mode.OUT) {
            return this.outParts.get(partName);
        }
        return null;
    }

    @Override
    public void addPart(EditableWSDLPart part, WebParam.Mode mode) {
        if (mode == WebParam.Mode.IN) {
            this.inParts.put(part.getName(), part);
        } else if (mode == WebParam.Mode.OUT) {
            this.outParts.put(part.getName(), part);
        }
    }

    @Override
    public Map<String, ParameterBinding> getInputParts() {
        return this.inputParts;
    }

    @Override
    public Map<String, ParameterBinding> getOutputParts() {
        return this.outputParts;
    }

    @Override
    public Map<String, ParameterBinding> getFaultParts() {
        return this.faultParts;
    }

    @Override
    public Map<String, ? extends EditableWSDLPart> getInParts() {
        return Collections.unmodifiableMap(this.inParts);
    }

    @Override
    public Map<String, ? extends EditableWSDLPart> getOutParts() {
        return Collections.unmodifiableMap(this.outParts);
    }

    @NotNull
    public List<? extends EditableWSDLBoundFault> getFaults() {
        return this.wsdlBoundFaults;
    }

    @Override
    public void addFault(@NotNull EditableWSDLBoundFault fault) {
        this.wsdlBoundFaults.add(fault);
    }

    @Override
    public ParameterBinding getInputBinding(String part) {
        ParameterBinding block;
        if (this.emptyInputBody == null) {
            this.emptyInputBody = this.inputParts.get(" ") != null ? Boolean.valueOf(true) : Boolean.valueOf(false);
        }
        if ((block = this.inputParts.get(part)) == null) {
            if (this.explicitInputSOAPBodyParts || this.emptyInputBody.booleanValue()) {
                return ParameterBinding.UNBOUND;
            }
            return ParameterBinding.BODY;
        }
        return block;
    }

    @Override
    public ParameterBinding getOutputBinding(String part) {
        ParameterBinding block;
        if (this.emptyOutputBody == null) {
            this.emptyOutputBody = this.outputParts.get(" ") != null ? Boolean.valueOf(true) : Boolean.valueOf(false);
        }
        if ((block = this.outputParts.get(part)) == null) {
            if (this.explicitOutputSOAPBodyParts || this.emptyOutputBody.booleanValue()) {
                return ParameterBinding.UNBOUND;
            }
            return ParameterBinding.BODY;
        }
        return block;
    }

    @Override
    public ParameterBinding getFaultBinding(String part) {
        ParameterBinding block;
        if (this.emptyFaultBody == null) {
            this.emptyFaultBody = this.faultParts.get(" ") != null ? Boolean.valueOf(true) : Boolean.valueOf(false);
        }
        if ((block = this.faultParts.get(part)) == null) {
            if (this.explicitFaultSOAPBodyParts || this.emptyFaultBody.booleanValue()) {
                return ParameterBinding.UNBOUND;
            }
            return ParameterBinding.BODY;
        }
        return block;
    }

    @Override
    public String getMimeTypeForInputPart(String part) {
        return this.inputMimeTypes.get(part);
    }

    @Override
    public String getMimeTypeForOutputPart(String part) {
        return this.outputMimeTypes.get(part);
    }

    @Override
    public String getMimeTypeForFaultPart(String part) {
        return this.faultMimeTypes.get(part);
    }

    @Override
    public EditableWSDLOperation getOperation() {
        return this.operation;
    }

    @Override
    public EditableWSDLBoundPortType getBoundPortType() {
        return this.owner;
    }

    @Override
    public void setInputExplicitBodyParts(boolean b) {
        this.explicitInputSOAPBodyParts = b;
    }

    @Override
    public void setOutputExplicitBodyParts(boolean b) {
        this.explicitOutputSOAPBodyParts = b;
    }

    @Override
    public void setFaultExplicitBodyParts(boolean b) {
        this.explicitFaultSOAPBodyParts = b;
    }

    @Override
    public void setStyle(SOAPBinding.Style style) {
        this.style = style;
    }

    @Override
    @Nullable
    public QName getRequestPayloadName() {
        if (this.emptyRequestPayload) {
            return null;
        }
        if (this.requestPayloadName != null) {
            return this.requestPayloadName;
        }
        if (this.style.equals((Object)SOAPBinding.Style.RPC)) {
            String ns = this.getRequestNamespace() != null ? this.getRequestNamespace() : this.name.getNamespaceURI();
            this.requestPayloadName = new QName(ns, this.name.getLocalPart());
            return this.requestPayloadName;
        }
        QName inMsgName = this.operation.getInput().getMessage().getName();
        EditableWSDLMessage message = this.messages.get(inMsgName);
        for (EditableWSDLPart editableWSDLPart : message.parts()) {
            ParameterBinding binding = this.getInputBinding(editableWSDLPart.getName());
            if (!binding.isBody()) continue;
            this.requestPayloadName = editableWSDLPart.getDescriptor().name();
            return this.requestPayloadName;
        }
        this.emptyRequestPayload = true;
        return null;
    }

    @Override
    @Nullable
    public QName getResponsePayloadName() {
        if (this.emptyResponsePayload) {
            return null;
        }
        if (this.responsePayloadName != null) {
            return this.responsePayloadName;
        }
        if (this.style.equals((Object)SOAPBinding.Style.RPC)) {
            String ns = this.getResponseNamespace() != null ? this.getResponseNamespace() : this.name.getNamespaceURI();
            this.responsePayloadName = new QName(ns, this.name.getLocalPart() + "Response");
            return this.responsePayloadName;
        }
        QName outMsgName = this.operation.getOutput().getMessage().getName();
        EditableWSDLMessage message = this.messages.get(outMsgName);
        for (EditableWSDLPart editableWSDLPart : message.parts()) {
            ParameterBinding binding = this.getOutputBinding(editableWSDLPart.getName());
            if (!binding.isBody()) continue;
            this.responsePayloadName = editableWSDLPart.getDescriptor().name();
            return this.responsePayloadName;
        }
        this.emptyResponsePayload = true;
        return null;
    }

    @Override
    public String getRequestNamespace() {
        return this.reqNamespace != null ? this.reqNamespace : this.name.getNamespaceURI();
    }

    @Override
    public void setRequestNamespace(String ns) {
        this.reqNamespace = ns;
    }

    @Override
    public String getResponseNamespace() {
        return this.respNamespace != null ? this.respNamespace : this.name.getNamespaceURI();
    }

    @Override
    public void setResponseNamespace(String ns) {
        this.respNamespace = ns;
    }

    EditableWSDLBoundPortType getOwner() {
        return this.owner;
    }

    @Override
    public void freeze(EditableWSDLModel parent) {
        this.messages = parent.getMessages();
        this.operation = this.owner.getPortType().get(this.name.getLocalPart());
        for (EditableWSDLBoundFault bf : this.wsdlBoundFaults) {
            bf.freeze(this);
        }
    }

    @Override
    public void setAnonymous(WSDLBoundOperation.ANONYMOUS anonymous) {
        this.anonymous = anonymous;
    }

    @Override
    public WSDLBoundOperation.ANONYMOUS getAnonymous() {
        return this.anonymous;
    }
}

