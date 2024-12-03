/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.Nullable
 */
package com.sun.xml.ws.api.server;

import com.sun.istack.Nullable;
import com.sun.xml.ws.api.addressing.WSEndpointReference;
import com.sun.xml.ws.api.server.WSEndpoint;
import javax.xml.namespace.QName;

public abstract class EndpointReferenceExtensionContributor {
    public abstract WSEndpointReference.EPRExtension getEPRExtension(WSEndpoint var1, @Nullable WSEndpointReference.EPRExtension var2);

    public abstract QName getQName();
}

