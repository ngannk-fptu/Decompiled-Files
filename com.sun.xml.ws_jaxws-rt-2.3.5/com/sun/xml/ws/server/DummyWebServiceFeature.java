/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.glassfish.gmbal.Description
 *  org.glassfish.gmbal.InheritedAttribute
 *  org.glassfish.gmbal.InheritedAttributes
 *  org.glassfish.gmbal.ManagedData
 */
package com.sun.xml.ws.server;

import org.glassfish.gmbal.Description;
import org.glassfish.gmbal.InheritedAttribute;
import org.glassfish.gmbal.InheritedAttributes;
import org.glassfish.gmbal.ManagedData;

@ManagedData
@Description(value="WebServiceFeature")
@InheritedAttributes(value={@InheritedAttribute(methodName="getID", description="unique id for this feature"), @InheritedAttribute(methodName="isEnabled", description="true if this feature is enabled")})
interface DummyWebServiceFeature {
}

