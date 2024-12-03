/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 *  javax.jws.WebParam$Mode
 */
package com.sun.xml.ws.api.model.wsdl;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.ws.api.model.ParameterBinding;
import com.sun.xml.ws.api.model.wsdl.WSDLBoundFault;
import com.sun.xml.ws.api.model.wsdl.WSDLBoundPortType;
import com.sun.xml.ws.api.model.wsdl.WSDLExtensible;
import com.sun.xml.ws.api.model.wsdl.WSDLObject;
import com.sun.xml.ws.api.model.wsdl.WSDLOperation;
import com.sun.xml.ws.api.model.wsdl.WSDLPart;
import java.util.Map;
import javax.jws.WebParam;
import javax.xml.namespace.QName;

public interface WSDLBoundOperation
extends WSDLObject,
WSDLExtensible {
    @NotNull
    public QName getName();

    @NotNull
    public String getSOAPAction();

    @NotNull
    public WSDLOperation getOperation();

    @NotNull
    public WSDLBoundPortType getBoundPortType();

    public ANONYMOUS getAnonymous();

    @Nullable
    public WSDLPart getPart(@NotNull String var1, @NotNull WebParam.Mode var2);

    public ParameterBinding getInputBinding(String var1);

    public ParameterBinding getOutputBinding(String var1);

    public ParameterBinding getFaultBinding(String var1);

    public String getMimeTypeForInputPart(String var1);

    public String getMimeTypeForOutputPart(String var1);

    public String getMimeTypeForFaultPart(String var1);

    @NotNull
    public Map<String, ? extends WSDLPart> getInParts();

    @NotNull
    public Map<String, ? extends WSDLPart> getOutParts();

    @NotNull
    public Iterable<? extends WSDLBoundFault> getFaults();

    public Map<String, ParameterBinding> getInputParts();

    public Map<String, ParameterBinding> getOutputParts();

    public Map<String, ParameterBinding> getFaultParts();

    @Nullable
    public QName getRequestPayloadName();

    @Nullable
    public QName getResponsePayloadName();

    public String getRequestNamespace();

    public String getResponseNamespace();

    public static enum ANONYMOUS {
        optional,
        required,
        prohibited;

    }
}

