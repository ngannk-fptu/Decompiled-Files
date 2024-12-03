/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 *  javax.xml.ws.WebServiceFeature
 *  org.glassfish.gmbal.ManagedAttribute
 *  org.glassfish.gmbal.ManagedData
 */
package com.sun.xml.ws.api.addressing;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.ws.api.FeatureConstructor;
import com.sun.xml.ws.api.addressing.AddressingPropertySet;
import com.sun.xml.ws.api.addressing.AddressingVersion;
import com.sun.xml.ws.api.addressing.WSEndpointReference;
import java.net.URL;
import javax.xml.ws.WebServiceFeature;
import org.glassfish.gmbal.ManagedAttribute;
import org.glassfish.gmbal.ManagedData;

@ManagedData
public class OneWayFeature
extends WebServiceFeature {
    public static final String ID = "http://java.sun.com/xml/ns/jaxws/addressing/oneway";
    private String messageId;
    private WSEndpointReference replyTo;
    private WSEndpointReference sslReplyTo;
    private WSEndpointReference from;
    private WSEndpointReference faultTo;
    private WSEndpointReference sslFaultTo;
    private String relatesToID;
    private boolean useAsyncWithSyncInvoke = false;

    public OneWayFeature() {
        this.enabled = true;
    }

    public OneWayFeature(boolean enabled) {
        this.enabled = enabled;
    }

    public OneWayFeature(boolean enabled, WSEndpointReference replyTo) {
        this.enabled = enabled;
        this.replyTo = replyTo;
    }

    @FeatureConstructor(value={"enabled", "replyTo", "from", "relatesTo"})
    public OneWayFeature(boolean enabled, WSEndpointReference replyTo, WSEndpointReference from, String relatesTo) {
        this.enabled = enabled;
        this.replyTo = replyTo;
        this.from = from;
        this.relatesToID = relatesTo;
    }

    public OneWayFeature(AddressingPropertySet a, AddressingVersion v) {
        this.enabled = true;
        this.messageId = a.getMessageId();
        this.relatesToID = a.getRelatesTo();
        this.replyTo = this.makeEPR(a.getReplyTo(), v);
        this.faultTo = this.makeEPR(a.getFaultTo(), v);
    }

    private WSEndpointReference makeEPR(String x, AddressingVersion v) {
        if (x == null) {
            return null;
        }
        return new WSEndpointReference(x, v);
    }

    public String getMessageId() {
        return this.messageId;
    }

    @ManagedAttribute
    public String getID() {
        return ID;
    }

    public boolean hasSslEprs() {
        return this.sslReplyTo != null || this.sslFaultTo != null;
    }

    @ManagedAttribute
    public WSEndpointReference getReplyTo() {
        return this.replyTo;
    }

    public WSEndpointReference getReplyTo(boolean ssl) {
        return ssl && this.sslReplyTo != null ? this.sslReplyTo : this.replyTo;
    }

    public void setReplyTo(WSEndpointReference address) {
        this.replyTo = address;
    }

    public WSEndpointReference getSslReplyTo() {
        return this.sslReplyTo;
    }

    public void setSslReplyTo(WSEndpointReference sslReplyTo) {
        this.sslReplyTo = sslReplyTo;
    }

    @ManagedAttribute
    public WSEndpointReference getFrom() {
        return this.from;
    }

    public void setFrom(WSEndpointReference address) {
        this.from = address;
    }

    @ManagedAttribute
    public String getRelatesToID() {
        return this.relatesToID;
    }

    public void setRelatesToID(String id) {
        this.relatesToID = id;
    }

    public WSEndpointReference getFaultTo() {
        return this.faultTo;
    }

    public WSEndpointReference getFaultTo(boolean ssl) {
        return ssl && this.sslFaultTo != null ? this.sslFaultTo : this.faultTo;
    }

    public void setFaultTo(WSEndpointReference address) {
        this.faultTo = address;
    }

    public WSEndpointReference getSslFaultTo() {
        return this.sslFaultTo;
    }

    public void setSslFaultTo(WSEndpointReference sslFaultTo) {
        this.sslFaultTo = sslFaultTo;
    }

    public boolean isUseAsyncWithSyncInvoke() {
        return this.useAsyncWithSyncInvoke;
    }

    public void setUseAsyncWithSyncInvoke(boolean useAsyncWithSyncInvoke) {
        this.useAsyncWithSyncInvoke = useAsyncWithSyncInvoke;
    }

    public static WSEndpointReference enableSslForEpr(@NotNull WSEndpointReference epr, @Nullable String sslHost, int sslPort) {
        if (!epr.isAnonymous()) {
            URL url;
            String address = epr.getAddress();
            try {
                url = new URL(address);
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
            String protocol = url.getProtocol();
            if (!protocol.equalsIgnoreCase("https")) {
                protocol = "https";
                String host = url.getHost();
                if (sslHost != null) {
                    host = sslHost;
                }
                int port = url.getPort();
                if (sslPort > 0) {
                    port = sslPort;
                }
                try {
                    url = new URL(protocol, host, port, url.getFile());
                }
                catch (Exception e) {
                    throw new RuntimeException(e);
                }
                address = url.toExternalForm();
                return new WSEndpointReference(address, epr.getVersion());
            }
        }
        return epr;
    }
}

