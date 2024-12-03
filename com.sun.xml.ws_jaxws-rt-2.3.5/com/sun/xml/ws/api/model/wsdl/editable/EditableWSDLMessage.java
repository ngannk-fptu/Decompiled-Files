/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.api.model.wsdl.editable;

import com.sun.xml.ws.api.model.wsdl.WSDLMessage;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLPart;

public interface EditableWSDLMessage
extends WSDLMessage {
    public Iterable<? extends EditableWSDLPart> parts();

    public void add(EditableWSDLPart var1);
}

