/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.wsdl.parser;

import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLModel;
import com.sun.xml.ws.api.policy.PolicyResolver;
import com.sun.xml.ws.api.server.Container;
import com.sun.xml.ws.api.wsdl.parser.WSDLParserExtensionContext;

final class WSDLParserExtensionContextImpl
implements WSDLParserExtensionContext {
    private final boolean isClientSide;
    private final EditableWSDLModel wsdlModel;
    private final Container container;
    private final PolicyResolver policyResolver;

    protected WSDLParserExtensionContextImpl(EditableWSDLModel model, boolean isClientSide, Container container, PolicyResolver policyResolver) {
        this.wsdlModel = model;
        this.isClientSide = isClientSide;
        this.container = container;
        this.policyResolver = policyResolver;
    }

    @Override
    public boolean isClientSide() {
        return this.isClientSide;
    }

    @Override
    public EditableWSDLModel getWSDLModel() {
        return this.wsdlModel;
    }

    @Override
    public Container getContainer() {
        return this.container;
    }

    @Override
    public PolicyResolver getPolicyResolver() {
        return this.policyResolver;
    }
}

