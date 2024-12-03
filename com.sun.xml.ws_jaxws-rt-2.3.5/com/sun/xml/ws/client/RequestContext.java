/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 */
package com.sun.xml.ws.client;

import com.oracle.webservices.api.message.BaseDistributedPropertySet;
import com.oracle.webservices.api.message.BasePropertySet;
import com.oracle.webservices.api.message.PropertySet;
import com.sun.istack.NotNull;
import com.sun.xml.ws.api.EndpointAddress;
import com.sun.xml.ws.api.PropertySet;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.client.ContentNegotiation;
import com.sun.xml.ws.transport.Headers;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public final class RequestContext
extends BaseDistributedPropertySet {
    private static final Logger LOGGER = Logger.getLogger(RequestContext.class.getName());
    private static ContentNegotiation defaultContentNegotiation = ContentNegotiation.obtainFromSystemProperty();
    @NotNull
    private EndpointAddress endpointAddress;
    public ContentNegotiation contentNegotiation = defaultContentNegotiation;
    private String soapAction;
    private Boolean soapActionUse;
    private static final BasePropertySet.PropertyMap propMap = RequestContext.parse(RequestContext.class);

    public void addSatellite(@NotNull PropertySet satellite) {
        super.addSatellite(satellite);
    }

    @PropertySet.Property(value={"javax.xml.ws.service.endpoint.address"})
    public String getEndPointAddressString() {
        return this.endpointAddress != null ? this.endpointAddress.toString() : null;
    }

    public void setEndPointAddressString(String s) {
        if (s == null) {
            throw new IllegalArgumentException();
        }
        this.endpointAddress = EndpointAddress.create(s);
    }

    public void setEndpointAddress(@NotNull EndpointAddress epa) {
        this.endpointAddress = epa;
    }

    @NotNull
    public EndpointAddress getEndpointAddress() {
        return this.endpointAddress;
    }

    @PropertySet.Property(value={"com.sun.xml.ws.client.ContentNegotiation"})
    public String getContentNegotiationString() {
        return this.contentNegotiation.toString();
    }

    public void setContentNegotiationString(String s) {
        if (s == null) {
            this.contentNegotiation = ContentNegotiation.none;
        } else {
            try {
                this.contentNegotiation = ContentNegotiation.valueOf(s);
            }
            catch (IllegalArgumentException e) {
                this.contentNegotiation = ContentNegotiation.none;
            }
        }
    }

    @PropertySet.Property(value={"javax.xml.ws.soap.http.soapaction.uri"})
    public String getSoapAction() {
        return this.soapAction;
    }

    public void setSoapAction(String sAction) {
        this.soapAction = sAction;
    }

    @PropertySet.Property(value={"javax.xml.ws.soap.http.soapaction.use"})
    public Boolean getSoapActionUse() {
        return this.soapActionUse;
    }

    public void setSoapActionUse(Boolean sActionUse) {
        this.soapActionUse = sActionUse;
    }

    RequestContext() {
    }

    private RequestContext(RequestContext that) {
        for (Map.Entry<String, Object> entry : that.asMapLocal().entrySet()) {
            if (propMap.containsKey(entry.getKey())) continue;
            this.asMap().put(entry.getKey(), entry.getValue());
        }
        this.endpointAddress = that.endpointAddress;
        this.soapAction = that.soapAction;
        this.soapActionUse = that.soapActionUse;
        this.contentNegotiation = that.contentNegotiation;
        that.copySatelliteInto(this);
    }

    @Override
    public Object get(Object key) {
        if (this.supports(key)) {
            return super.get(key);
        }
        return this.asMap().get(key);
    }

    @Override
    public Object put(String key, Object value) {
        if (this.supports(key)) {
            return super.put(key, value);
        }
        return this.asMap().put(key, value);
    }

    public void fill(Packet packet, boolean isAddressingEnabled) {
        if (this.endpointAddress != null) {
            packet.endpointAddress = this.endpointAddress;
        }
        packet.contentNegotiation = this.contentNegotiation;
        this.fillSOAPAction(packet, isAddressingEnabled);
        this.mergeRequestHeaders(packet);
        HashSet<String> handlerScopeNames = new HashSet<String>();
        this.copySatelliteInto(packet);
        for (String key : this.asMapLocal().keySet()) {
            if (!this.supportsLocal(key)) {
                handlerScopeNames.add(key);
            }
            if (propMap.containsKey(key)) continue;
            Object value = this.asMapLocal().get(key);
            if (packet.supports(key)) {
                packet.put(key, value);
                continue;
            }
            packet.invocationProperties.put(key, value);
        }
        if (!handlerScopeNames.isEmpty()) {
            packet.getHandlerScopePropertyNames(false).addAll(handlerScopeNames);
        }
    }

    private void mergeRequestHeaders(Packet packet) {
        Headers packetHeaders = (Headers)packet.invocationProperties.get("javax.xml.ws.http.request.headers");
        Map myHeaders = (Map)this.asMap().get("javax.xml.ws.http.request.headers");
        if (packetHeaders != null && myHeaders != null) {
            for (Map.Entry entry : myHeaders.entrySet()) {
                String key = (String)entry.getKey();
                if (key == null || key.trim().length() == 0) continue;
                List listFromPacket = (List)packetHeaders.get(key);
                if (listFromPacket != null) {
                    listFromPacket.addAll((Collection)entry.getValue());
                    continue;
                }
                packetHeaders.put(key, (List)myHeaders.get(key));
            }
            this.asMap().put("javax.xml.ws.http.request.headers", packetHeaders);
        }
    }

    private void fillSOAPAction(Packet packet, boolean isAddressingEnabled) {
        Boolean localSoapActionUse;
        boolean p = packet.packetTakesPriorityOverRequestContext;
        String localSoapAction = p ? packet.soapAction : this.soapAction;
        Boolean bl = localSoapActionUse = p ? (Boolean)packet.invocationProperties.get("javax.xml.ws.soap.http.soapaction.use") : this.soapActionUse;
        if ((localSoapActionUse != null && localSoapActionUse.booleanValue() || localSoapActionUse == null && isAddressingEnabled) && localSoapAction != null) {
            packet.soapAction = localSoapAction;
        }
        if (!(isAddressingEnabled || localSoapActionUse != null && localSoapActionUse.booleanValue() || localSoapAction == null)) {
            LOGGER.warning("BindingProvider.SOAPACTION_URI_PROPERTY is set in the RequestContext but is ineffective, Either set BindingProvider.SOAPACTION_USE_PROPERTY to true or enable AddressingFeature");
        }
    }

    public RequestContext copy() {
        return new RequestContext(this);
    }

    @Override
    protected BasePropertySet.PropertyMap getPropertyMap() {
        return propMap;
    }

    @Override
    protected boolean mapAllowsAdditionalProperties() {
        return true;
    }
}

