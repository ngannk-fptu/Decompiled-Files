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
import com.sun.xml.ws.api.model.wsdl.WSDLService;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLModel;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLPort;
import javax.xml.namespace.QName;

public interface EditableWSDLService
extends WSDLService {
    @Override
    @NotNull
    public EditableWSDLModel getParent();

    @Override
    public EditableWSDLPort get(QName var1);

    @Override
    public EditableWSDLPort getFirstPort();

    @Override
    @Nullable
    public EditableWSDLPort getMatchingPort(QName var1);

    public Iterable<? extends EditableWSDLPort> getPorts();

    public void put(QName var1, EditableWSDLPort var2);

    public void freeze(EditableWSDLModel var1);
}

