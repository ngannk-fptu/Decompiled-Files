/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.ws.Holder
 */
package com.oracle.webservices.api.databinding;

import javax.xml.transform.Result;
import javax.xml.ws.Holder;

public interface WSDLResolver {
    public Result getWSDL(String var1);

    public Result getAbstractWSDL(Holder<String> var1);

    public Result getSchemaOutput(String var1, Holder<String> var2);
}

