/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  javax.jws.WebParam$Mode
 *  javax.jws.soap.SOAPBinding$Style
 */
package com.sun.xml.ws.model.wsdl;

import com.sun.istack.NotNull;
import com.sun.xml.ws.api.BindingID;
import com.sun.xml.ws.api.model.ParameterBinding;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLBoundOperation;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLBoundPortType;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLModel;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLPortType;
import com.sun.xml.ws.model.wsdl.AbstractFeaturedObjectImpl;
import com.sun.xml.ws.resources.ClientMessages;
import com.sun.xml.ws.util.QNameMap;
import com.sun.xml.ws.util.exception.LocatableWebServiceException;
import javax.jws.WebParam;
import javax.jws.soap.SOAPBinding;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

public final class WSDLBoundPortTypeImpl
extends AbstractFeaturedObjectImpl
implements EditableWSDLBoundPortType {
    private final QName name;
    private final QName portTypeName;
    private EditableWSDLPortType portType;
    private BindingID bindingId;
    @NotNull
    private final EditableWSDLModel owner;
    private final QNameMap<EditableWSDLBoundOperation> bindingOperations = new QNameMap();
    private QNameMap<EditableWSDLBoundOperation> payloadMap;
    private EditableWSDLBoundOperation emptyPayloadOperation;
    private SOAPBinding.Style style = SOAPBinding.Style.DOCUMENT;

    public WSDLBoundPortTypeImpl(XMLStreamReader xsr, @NotNull EditableWSDLModel owner, QName name, QName portTypeName) {
        super(xsr);
        this.owner = owner;
        this.name = name;
        this.portTypeName = portTypeName;
        owner.addBinding(this);
    }

    @Override
    public QName getName() {
        return this.name;
    }

    @Override
    @NotNull
    public EditableWSDLModel getOwner() {
        return this.owner;
    }

    @Override
    public EditableWSDLBoundOperation get(QName operationName) {
        return this.bindingOperations.get(operationName);
    }

    @Override
    public void put(QName opName, EditableWSDLBoundOperation ptOp) {
        this.bindingOperations.put(opName, ptOp);
    }

    @Override
    public QName getPortTypeName() {
        return this.portTypeName;
    }

    @Override
    public EditableWSDLPortType getPortType() {
        return this.portType;
    }

    public Iterable<EditableWSDLBoundOperation> getBindingOperations() {
        return this.bindingOperations.values();
    }

    @Override
    public BindingID getBindingId() {
        return this.bindingId == null ? BindingID.SOAP11_HTTP : this.bindingId;
    }

    @Override
    public void setBindingId(BindingID bindingId) {
        this.bindingId = bindingId;
    }

    @Override
    public void setStyle(SOAPBinding.Style style) {
        this.style = style;
    }

    @Override
    public SOAPBinding.Style getStyle() {
        return this.style;
    }

    public boolean isRpcLit() {
        return SOAPBinding.Style.RPC == this.style;
    }

    public boolean isDoclit() {
        return SOAPBinding.Style.DOCUMENT == this.style;
    }

    @Override
    public ParameterBinding getBinding(QName operation, String part, WebParam.Mode mode) {
        EditableWSDLBoundOperation op = this.get(operation);
        if (op == null) {
            return null;
        }
        if (WebParam.Mode.IN == mode || WebParam.Mode.INOUT == mode) {
            return op.getInputBinding(part);
        }
        return op.getOutputBinding(part);
    }

    @Override
    public EditableWSDLBoundOperation getOperation(String namespaceUri, String localName) {
        if (namespaceUri == null && localName == null) {
            return this.emptyPayloadOperation;
        }
        return this.payloadMap.get(namespaceUri == null ? "" : namespaceUri, localName);
    }

    @Override
    public void freeze() {
        this.portType = this.owner.getPortType(this.portTypeName);
        if (this.portType == null) {
            throw new LocatableWebServiceException(ClientMessages.UNDEFINED_PORT_TYPE(this.portTypeName), this.getLocation());
        }
        this.portType.freeze();
        for (EditableWSDLBoundOperation op : this.bindingOperations.values()) {
            op.freeze(this.owner);
        }
        this.freezePayloadMap();
        this.owner.finalizeRpcLitBinding(this);
    }

    private void freezePayloadMap() {
        if (this.style == SOAPBinding.Style.RPC) {
            this.payloadMap = new QNameMap();
            for (EditableWSDLBoundOperation op : this.bindingOperations.values()) {
                this.payloadMap.put(op.getRequestPayloadName(), op);
            }
        } else {
            this.payloadMap = new QNameMap();
            for (EditableWSDLBoundOperation op : this.bindingOperations.values()) {
                QName name = op.getRequestPayloadName();
                if (name == null) {
                    this.emptyPayloadOperation = op;
                    continue;
                }
                this.payloadMap.put(name, op);
            }
        }
    }
}

