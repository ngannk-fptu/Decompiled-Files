/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.policy.jaxws;

import com.sun.xml.ws.api.model.wsdl.WSDLBoundFault;
import com.sun.xml.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.ws.api.model.wsdl.WSDLObject;
import org.xml.sax.Locator;

class WSDLBoundFaultContainer
implements WSDLObject {
    private final WSDLBoundFault boundFault;
    private final WSDLBoundOperation boundOperation;

    public WSDLBoundFaultContainer(WSDLBoundFault fault, WSDLBoundOperation operation) {
        this.boundFault = fault;
        this.boundOperation = operation;
    }

    @Override
    public Locator getLocation() {
        return null;
    }

    public WSDLBoundFault getBoundFault() {
        return this.boundFault;
    }

    public WSDLBoundOperation getBoundOperation() {
        return this.boundOperation;
    }
}

