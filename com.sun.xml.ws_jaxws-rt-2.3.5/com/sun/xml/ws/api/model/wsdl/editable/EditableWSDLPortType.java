/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.api.model.wsdl.editable;

import com.sun.xml.ws.api.model.wsdl.WSDLPortType;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLOperation;

public interface EditableWSDLPortType
extends WSDLPortType {
    @Override
    public EditableWSDLOperation get(String var1);

    public Iterable<? extends EditableWSDLOperation> getOperations();

    public void put(String var1, EditableWSDLOperation var2);

    public void freeze();
}

