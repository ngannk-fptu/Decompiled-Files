/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  javax.xml.ws.WebServiceFeature
 */
package com.sun.xml.ws.client;

import com.sun.istack.NotNull;
import com.sun.xml.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.ws.binding.BindingImpl;
import com.sun.xml.ws.binding.SOAPBindingImpl;
import com.sun.xml.ws.binding.WebServiceFeatureList;
import com.sun.xml.ws.client.PortInfo;
import com.sun.xml.ws.client.WSServiceDelegate;
import com.sun.xml.ws.model.SOAPSEIModel;
import javax.xml.ws.WebServiceFeature;

public final class SEIPortInfo
extends PortInfo {
    public final Class sei;
    public final SOAPSEIModel model;

    public SEIPortInfo(WSServiceDelegate owner, Class sei, SOAPSEIModel model, @NotNull WSDLPort portModel) {
        super(owner, portModel);
        this.sei = sei;
        this.model = model;
        assert (sei != null && model != null);
    }

    @Override
    public BindingImpl createBinding(WebServiceFeature[] webServiceFeatures, Class<?> portInterface) {
        BindingImpl binding = super.createBinding(webServiceFeatures, portInterface);
        return this.setKnownHeaders(binding);
    }

    public BindingImpl createBinding(WebServiceFeatureList webServiceFeatures, Class<?> portInterface) {
        BindingImpl binding = super.createBinding(webServiceFeatures, portInterface, null);
        return this.setKnownHeaders(binding);
    }

    private BindingImpl setKnownHeaders(BindingImpl binding) {
        if (binding instanceof SOAPBindingImpl) {
            ((SOAPBindingImpl)binding).setPortKnownHeaders(this.model.getKnownHeaders());
        }
        return binding;
    }
}

