/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.api.model.wsdl;

import com.sun.xml.ws.api.model.ParameterBinding;
import com.sun.xml.ws.api.model.wsdl.WSDLObject;
import com.sun.xml.ws.api.model.wsdl.WSDLPartDescriptor;

public interface WSDLPart
extends WSDLObject {
    public String getName();

    public ParameterBinding getBinding();

    public int getIndex();

    public WSDLPartDescriptor getDescriptor();
}

