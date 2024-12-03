/*
 * Decompiled with CFR 0.152.
 */
package com.onelogin.saml2.model;

import com.onelogin.saml2.model.RequestedAttribute;
import java.util.ArrayList;
import java.util.List;

public class AttributeConsumingService {
    private final String serviceName;
    private final String serviceDescription;
    private final List<RequestedAttribute> requestedAttributes;

    public AttributeConsumingService(String serviceName, String serviceDescription) {
        this.serviceName = serviceName != null ? serviceName : "";
        this.serviceDescription = serviceDescription != null ? serviceDescription : "";
        this.requestedAttributes = new ArrayList<RequestedAttribute>();
    }

    public final void addRequestedAttribute(RequestedAttribute attr) {
        this.requestedAttributes.add(attr);
    }

    public final String getServiceName() {
        return this.serviceName;
    }

    public final String getServiceDescription() {
        return this.serviceDescription;
    }

    public final List<RequestedAttribute> getRequestedAttributes() {
        return this.requestedAttributes;
    }
}

