/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 */
package com.sun.xml.ws.api.model.wsdl;

import com.sun.istack.NotNull;
import org.xml.sax.Locator;

public interface WSDLObject {
    @NotNull
    public Locator getLocation();
}

