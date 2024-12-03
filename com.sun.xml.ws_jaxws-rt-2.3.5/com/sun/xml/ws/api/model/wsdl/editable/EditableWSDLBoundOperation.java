/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 *  javax.jws.WebParam$Mode
 *  javax.jws.soap.SOAPBinding$Style
 */
package com.sun.xml.ws.api.model.wsdl.editable;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLBoundFault;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLBoundPortType;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLModel;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLOperation;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLPart;
import java.util.Map;
import javax.jws.WebParam;
import javax.jws.soap.SOAPBinding;

public interface EditableWSDLBoundOperation
extends WSDLBoundOperation {
    @Override
    @NotNull
    public EditableWSDLOperation getOperation();

    @Override
    @NotNull
    public EditableWSDLBoundPortType getBoundPortType();

    @Override
    @Nullable
    public EditableWSDLPart getPart(@NotNull String var1, @NotNull WebParam.Mode var2);

    @NotNull
    public Map<String, ? extends EditableWSDLPart> getInParts();

    @NotNull
    public Map<String, ? extends EditableWSDLPart> getOutParts();

    @NotNull
    public Iterable<? extends EditableWSDLBoundFault> getFaults();

    public void addPart(EditableWSDLPart var1, WebParam.Mode var2);

    public void addFault(@NotNull EditableWSDLBoundFault var1);

    public void setAnonymous(WSDLBoundOperation.ANONYMOUS var1);

    public void setInputExplicitBodyParts(boolean var1);

    public void setOutputExplicitBodyParts(boolean var1);

    public void setFaultExplicitBodyParts(boolean var1);

    public void setRequestNamespace(String var1);

    public void setResponseNamespace(String var1);

    public void setSoapAction(String var1);

    public void setStyle(SOAPBinding.Style var1);

    public void freeze(EditableWSDLModel var1);
}

