/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.api.model.wsdl.editable;

import com.sun.xml.ws.api.model.ParameterBinding;
import com.sun.xml.ws.api.model.wsdl.WSDLPart;

public interface EditableWSDLPart
extends WSDLPart {
    public void setBinding(ParameterBinding var1);

    public void setIndex(int var1);
}

