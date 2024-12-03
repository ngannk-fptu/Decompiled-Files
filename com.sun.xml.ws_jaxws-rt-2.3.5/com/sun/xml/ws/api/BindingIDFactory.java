/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.api;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.ws.api.BindingID;
import com.sun.xml.ws.api.SOAPVersion;
import javax.xml.ws.WebServiceException;

public abstract class BindingIDFactory {
    @Nullable
    public abstract BindingID parse(@NotNull String var1) throws WebServiceException;

    @Nullable
    public BindingID create(@NotNull String transport, @NotNull SOAPVersion soapVersion) throws WebServiceException {
        return null;
    }
}

