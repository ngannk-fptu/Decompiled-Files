/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 */
package com.sun.xml.ws.api.wsdl.parser;

import com.sun.istack.NotNull;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLModel;
import com.sun.xml.ws.api.policy.PolicyResolver;
import com.sun.xml.ws.api.server.Container;

public interface WSDLParserExtensionContext {
    public boolean isClientSide();

    public EditableWSDLModel getWSDLModel();

    @NotNull
    public Container getContainer();

    @NotNull
    public PolicyResolver getPolicyResolver();
}

