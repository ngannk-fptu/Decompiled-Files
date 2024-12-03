/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 */
package com.sun.xml.ws.model.wsdl;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.ws.api.EndpointAddress;
import com.sun.xml.ws.api.addressing.WSEndpointReference;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLBoundPortType;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLModel;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLPort;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLService;
import com.sun.xml.ws.binding.WebServiceFeatureList;
import com.sun.xml.ws.model.wsdl.AbstractFeaturedObjectImpl;
import com.sun.xml.ws.resources.ClientMessages;
import com.sun.xml.ws.util.exception.LocatableWebServiceException;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

public final class WSDLPortImpl
extends AbstractFeaturedObjectImpl
implements EditableWSDLPort {
    private final QName name;
    private EndpointAddress address;
    private final QName bindingName;
    private final EditableWSDLService owner;
    private WSEndpointReference epr;
    private EditableWSDLBoundPortType boundPortType;

    public WSDLPortImpl(XMLStreamReader xsr, EditableWSDLService owner, QName name, QName binding) {
        super(xsr);
        this.owner = owner;
        this.name = name;
        this.bindingName = binding;
    }

    @Override
    public QName getName() {
        return this.name;
    }

    public QName getBindingName() {
        return this.bindingName;
    }

    @Override
    public EndpointAddress getAddress() {
        return this.address;
    }

    @Override
    public EditableWSDLService getOwner() {
        return this.owner;
    }

    @Override
    public void setAddress(EndpointAddress address) {
        assert (address != null);
        this.address = address;
    }

    @Override
    public void setEPR(@NotNull WSEndpointReference epr) {
        assert (epr != null);
        this.addExtension(epr);
        this.epr = epr;
    }

    @Override
    @Nullable
    public WSEndpointReference getEPR() {
        return this.epr;
    }

    @Override
    public EditableWSDLBoundPortType getBinding() {
        return this.boundPortType;
    }

    @Override
    public void freeze(EditableWSDLModel root) {
        this.boundPortType = root.getBinding(this.bindingName);
        if (this.boundPortType == null) {
            throw new LocatableWebServiceException(ClientMessages.UNDEFINED_BINDING(this.bindingName), this.getLocation());
        }
        if (this.features == null) {
            this.features = new WebServiceFeatureList();
        }
        this.features.setParentFeaturedObject(this.boundPortType);
        this.notUnderstoodExtensions.addAll(this.boundPortType.getNotUnderstoodExtensions());
    }
}

