/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.xml.ws.policy.PolicyMap
 *  javax.jws.WebParam$Mode
 */
package com.sun.xml.ws.model.wsdl;

import com.sun.istack.NotNull;
import com.sun.xml.ws.api.model.ParameterBinding;
import com.sun.xml.ws.api.model.wsdl.WSDLMessage;
import com.sun.xml.ws.api.model.wsdl.WSDLOperation;
import com.sun.xml.ws.api.model.wsdl.WSDLPortType;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLBoundOperation;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLBoundPortType;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLMessage;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLModel;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLPart;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLPort;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLPortType;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLService;
import com.sun.xml.ws.model.wsdl.AbstractExtensibleImpl;
import com.sun.xml.ws.policy.PolicyMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.jws.WebParam;
import javax.xml.namespace.QName;

public final class WSDLModelImpl
extends AbstractExtensibleImpl
implements EditableWSDLModel {
    private final Map<QName, EditableWSDLMessage> messages = new HashMap<QName, EditableWSDLMessage>();
    private final Map<QName, EditableWSDLPortType> portTypes = new HashMap<QName, EditableWSDLPortType>();
    private final Map<QName, EditableWSDLBoundPortType> bindings = new HashMap<QName, EditableWSDLBoundPortType>();
    private final Map<QName, EditableWSDLService> services = new LinkedHashMap<QName, EditableWSDLService>();
    private PolicyMap policyMap;
    private final Map<QName, EditableWSDLBoundPortType> unmBindings = Collections.unmodifiableMap(this.bindings);

    public WSDLModelImpl(@NotNull String systemId) {
        super(systemId, -1);
    }

    public WSDLModelImpl() {
        super(null, -1);
    }

    @Override
    public void addMessage(EditableWSDLMessage msg) {
        this.messages.put(msg.getName(), msg);
    }

    @Override
    public EditableWSDLMessage getMessage(QName name) {
        return this.messages.get(name);
    }

    @Override
    public void addPortType(EditableWSDLPortType pt) {
        this.portTypes.put(pt.getName(), pt);
    }

    @Override
    public EditableWSDLPortType getPortType(QName name) {
        return this.portTypes.get(name);
    }

    @Override
    public void addBinding(EditableWSDLBoundPortType boundPortType) {
        assert (!this.bindings.containsValue(boundPortType));
        this.bindings.put(boundPortType.getName(), boundPortType);
    }

    @Override
    public EditableWSDLBoundPortType getBinding(QName name) {
        return this.bindings.get(name);
    }

    @Override
    public void addService(EditableWSDLService svc) {
        this.services.put(svc.getName(), svc);
    }

    @Override
    public EditableWSDLService getService(QName name) {
        return this.services.get(name);
    }

    public Map<QName, EditableWSDLMessage> getMessages() {
        return this.messages;
    }

    @NotNull
    public Map<QName, EditableWSDLPortType> getPortTypes() {
        return this.portTypes;
    }

    @Override
    @NotNull
    public Map<QName, ? extends EditableWSDLBoundPortType> getBindings() {
        return this.unmBindings;
    }

    @NotNull
    public Map<QName, EditableWSDLService> getServices() {
        return this.services;
    }

    @Override
    public QName getFirstServiceName() {
        if (this.services.isEmpty()) {
            return null;
        }
        return this.services.values().iterator().next().getName();
    }

    @Override
    public EditableWSDLBoundPortType getBinding(QName serviceName, QName portName) {
        EditableWSDLPort port;
        EditableWSDLService service = this.services.get(serviceName);
        if (service != null && (port = service.get(portName)) != null) {
            return port.getBinding();
        }
        return null;
    }

    @Override
    public void finalizeRpcLitBinding(EditableWSDLBoundPortType boundPortType) {
        assert (boundPortType != null);
        QName portTypeName = boundPortType.getPortTypeName();
        if (portTypeName == null) {
            return;
        }
        WSDLPortType pt = this.portTypes.get(portTypeName);
        if (pt == null) {
            return;
        }
        for (EditableWSDLBoundOperation editableWSDLBoundOperation : boundPortType.getBindingOperations()) {
            EditableWSDLMessage editableWSDLMessage;
            WSDLMessage outMsgName;
            WSDLOperation pto = pt.get(editableWSDLBoundOperation.getName().getLocalPart());
            WSDLMessage inMsgName = pto.getInput().getMessage();
            if (inMsgName == null) continue;
            EditableWSDLMessage inMsg = this.messages.get(inMsgName.getName());
            int bodyindex = 0;
            if (inMsg != null) {
                for (EditableWSDLPart editableWSDLPart : inMsg.parts()) {
                    String name = editableWSDLPart.getName();
                    ParameterBinding parameterBinding = editableWSDLBoundOperation.getInputBinding(name);
                    if (!parameterBinding.isBody()) continue;
                    editableWSDLPart.setIndex(bodyindex++);
                    editableWSDLPart.setBinding(parameterBinding);
                    editableWSDLBoundOperation.addPart(editableWSDLPart, WebParam.Mode.IN);
                }
            }
            bodyindex = 0;
            if (pto.isOneWay() || (outMsgName = pto.getOutput().getMessage()) == null || (editableWSDLMessage = this.messages.get(outMsgName.getName())) == null) continue;
            for (EditableWSDLPart editableWSDLPart : editableWSDLMessage.parts()) {
                String name = editableWSDLPart.getName();
                ParameterBinding pb = editableWSDLBoundOperation.getOutputBinding(name);
                if (!pb.isBody()) continue;
                editableWSDLPart.setIndex(bodyindex++);
                editableWSDLPart.setBinding(pb);
                editableWSDLBoundOperation.addPart(editableWSDLPart, WebParam.Mode.OUT);
            }
        }
    }

    @Override
    public PolicyMap getPolicyMap() {
        return this.policyMap;
    }

    @Override
    public void setPolicyMap(PolicyMap policyMap) {
        this.policyMap = policyMap;
    }

    @Override
    public void freeze() {
        for (EditableWSDLService service : this.services.values()) {
            service.freeze(this);
        }
        for (EditableWSDLBoundPortType bp : this.bindings.values()) {
            bp.freeze();
        }
        for (EditableWSDLPortType pt : this.portTypes.values()) {
            pt.freeze();
        }
    }
}

