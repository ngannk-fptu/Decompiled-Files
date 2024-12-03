/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 */
package com.sun.xml.ws.api.model;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.ws.api.model.CheckedException;
import com.sun.xml.ws.api.model.MEP;
import com.sun.xml.ws.api.model.SEIModel;
import com.sun.xml.ws.api.model.soap.SOAPBinding;
import java.lang.reflect.Method;
import java.util.Collection;
import javax.xml.namespace.QName;

public interface JavaMethod {
    public SEIModel getOwner();

    @NotNull
    public Method getMethod();

    @NotNull
    public Method getSEIMethod();

    public MEP getMEP();

    public SOAPBinding getBinding();

    @NotNull
    public String getOperationName();

    @NotNull
    public String getRequestMessageName();

    @Nullable
    public String getResponseMessageName();

    @Nullable
    public QName getRequestPayloadName();

    @Nullable
    public QName getResponsePayloadName();

    public Collection<? extends CheckedException> getCheckedExceptions();
}

