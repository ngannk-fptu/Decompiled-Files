/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.Nullable
 */
package com.sun.xml.ws.model.wsdl;

import com.oracle.webservices.api.message.BasePropertySet;
import com.oracle.webservices.api.message.PropertySet;
import com.sun.istack.Nullable;
import com.sun.xml.ws.api.model.SEIModel;
import javax.xml.namespace.QName;
import org.xml.sax.InputSource;

public abstract class WSDLProperties
extends BasePropertySet {
    private static final BasePropertySet.PropertyMap model = WSDLProperties.parse(WSDLProperties.class);
    @Nullable
    private final SEIModel seiModel;

    protected WSDLProperties(@Nullable SEIModel seiModel) {
        this.seiModel = seiModel;
    }

    @PropertySet.Property(value={"javax.xml.ws.wsdl.service"})
    public abstract QName getWSDLService();

    @PropertySet.Property(value={"javax.xml.ws.wsdl.port"})
    public abstract QName getWSDLPort();

    @PropertySet.Property(value={"javax.xml.ws.wsdl.interface"})
    public abstract QName getWSDLPortType();

    @PropertySet.Property(value={"javax.xml.ws.wsdl.description"})
    public InputSource getWSDLDescription() {
        return this.seiModel != null ? new InputSource(this.seiModel.getWSDLLocation()) : null;
    }

    @Override
    protected BasePropertySet.PropertyMap getPropertyMap() {
        return model;
    }
}

