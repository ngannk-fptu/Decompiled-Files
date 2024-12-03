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
import com.sun.xml.ws.api.model.wsdl.WSDLBoundFault;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLBoundOperation;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLFault;

public interface EditableWSDLBoundFault
extends WSDLBoundFault {
    @Override
    @Nullable
    public EditableWSDLFault getFault();

    @Override
    @NotNull
    public EditableWSDLBoundOperation getBoundOperation();

    public void freeze(EditableWSDLBoundOperation var1);
}

