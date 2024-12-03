/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 */
package com.sun.xml.ws.api.wsdl.parser;

import com.sun.istack.NotNull;
import java.util.List;
import javax.xml.transform.Source;

public abstract class ServiceDescriptor {
    @NotNull
    public abstract List<? extends Source> getWSDLs();

    @NotNull
    public abstract List<? extends Source> getSchemas();
}

