/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 */
package com.sun.xml.ws.api.model.wsdl.editable;

import com.sun.istack.NotNull;
import com.sun.xml.ws.api.model.wsdl.WSDLInput;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLMessage;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLModel;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLOperation;

public interface EditableWSDLInput
extends WSDLInput {
    @Override
    public EditableWSDLMessage getMessage();

    @Override
    @NotNull
    public EditableWSDLOperation getOperation();

    public void setAction(String var1);

    public void setDefaultAction(boolean var1);

    public void freeze(EditableWSDLModel var1);
}

