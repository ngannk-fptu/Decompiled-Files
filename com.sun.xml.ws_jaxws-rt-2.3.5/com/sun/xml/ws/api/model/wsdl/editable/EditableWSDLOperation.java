/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 */
package com.sun.xml.ws.api.model.wsdl.editable;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.ws.api.model.wsdl.WSDLOperation;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLFault;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLInput;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLModel;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLOutput;
import javax.xml.namespace.QName;

public interface EditableWSDLOperation
extends WSDLOperation {
    @Override
    @NotNull
    public EditableWSDLInput getInput();

    public void setInput(EditableWSDLInput var1);

    @Override
    @Nullable
    public EditableWSDLOutput getOutput();

    public void setOutput(EditableWSDLOutput var1);

    public Iterable<? extends EditableWSDLFault> getFaults();

    public void addFault(EditableWSDLFault var1);

    @Override
    @Nullable
    public EditableWSDLFault getFault(QName var1);

    public void setParameterOrder(String var1);

    public void freeze(EditableWSDLModel var1);
}

