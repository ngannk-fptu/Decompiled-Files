/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  org.glassfish.gmbal.AMXMetadata
 *  org.glassfish.gmbal.Description
 *  org.glassfish.gmbal.ManagedAttribute
 *  org.glassfish.gmbal.ManagedObject
 */
package com.sun.xml.ws.server;

import com.sun.istack.NotNull;
import com.sun.xml.ws.api.BindingID;
import com.sun.xml.ws.api.EndpointAddress;
import com.sun.xml.ws.api.WSFeatureList;
import com.sun.xml.ws.api.addressing.AddressingVersion;
import com.sun.xml.ws.api.server.Container;
import com.sun.xml.ws.api.server.WSEndpoint;
import com.sun.xml.ws.server.MonitorBase;
import com.sun.xml.ws.transport.http.HttpAdapter;
import com.sun.xml.ws.util.RuntimeVersion;
import java.net.URL;
import java.util.Set;
import javax.xml.namespace.QName;
import org.glassfish.gmbal.AMXMetadata;
import org.glassfish.gmbal.Description;
import org.glassfish.gmbal.ManagedAttribute;
import org.glassfish.gmbal.ManagedObject;

@ManagedObject
@Description(value="Metro Web Service endpoint")
@AMXMetadata(type="WSEndpoint")
public final class MonitorRootService
extends MonitorBase {
    private final WSEndpoint endpoint;

    MonitorRootService(WSEndpoint endpoint) {
        this.endpoint = endpoint;
    }

    @ManagedAttribute
    @Description(value="Policy associated with Endpoint")
    public String policy() {
        return this.endpoint.getPolicyMap() != null ? this.endpoint.getPolicyMap().toString() : null;
    }

    @ManagedAttribute
    @Description(value="Container")
    @NotNull
    public Container container() {
        return this.endpoint.getContainer();
    }

    @ManagedAttribute
    @Description(value="Port name")
    @NotNull
    public QName portName() {
        return this.endpoint.getPortName();
    }

    @ManagedAttribute
    @Description(value="Service name")
    @NotNull
    public QName serviceName() {
        return this.endpoint.getServiceName();
    }

    @ManagedAttribute
    @Description(value="Binding SOAP Version")
    public String soapVersionHttpBindingId() {
        return this.endpoint.getBinding().getSOAPVersion().httpBindingId;
    }

    @ManagedAttribute
    @Description(value="Binding Addressing Version")
    public AddressingVersion addressingVersion() {
        return this.endpoint.getBinding().getAddressingVersion();
    }

    @ManagedAttribute
    @Description(value="Binding Identifier")
    @NotNull
    public BindingID bindingID() {
        return this.endpoint.getBinding().getBindingId();
    }

    @ManagedAttribute
    @Description(value="Binding features")
    @NotNull
    public WSFeatureList features() {
        return this.endpoint.getBinding().getFeatures();
    }

    @ManagedAttribute
    @Description(value="WSDLPort bound port type")
    public QName wsdlPortTypeName() {
        return this.endpoint.getPort() != null ? this.endpoint.getPort().getBinding().getPortTypeName() : null;
    }

    @ManagedAttribute
    @Description(value="Endpoint address")
    public EndpointAddress wsdlEndpointAddress() {
        return this.endpoint.getPort() != null ? this.endpoint.getPort().getAddress() : null;
    }

    @ManagedAttribute
    @Description(value="Documents referenced")
    public Set<String> serviceDefinitionImports() {
        return this.endpoint.getServiceDefinition() != null ? this.endpoint.getServiceDefinition().getPrimary().getImports() : null;
    }

    @ManagedAttribute
    @Description(value="System ID where document is taken from")
    public URL serviceDefinitionURL() {
        return this.endpoint.getServiceDefinition() != null ? this.endpoint.getServiceDefinition().getPrimary().getURL() : null;
    }

    @ManagedAttribute
    @Description(value="SEI model WSDL location")
    public String seiModelWSDLLocation() {
        return this.endpoint.getSEIModel() != null ? this.endpoint.getSEIModel().getWSDLLocation() : null;
    }

    @ManagedAttribute
    @Description(value="JAX-WS runtime version")
    public String jaxwsRuntimeVersion() {
        return RuntimeVersion.VERSION.toString();
    }

    @ManagedAttribute
    @Description(value="If true: show what goes across HTTP transport")
    public boolean dumpHTTPMessages() {
        return HttpAdapter.dump;
    }

    @ManagedAttribute
    @Description(value="Show what goes across HTTP transport")
    public void dumpHTTPMessages(boolean x) {
        HttpAdapter.setDump(x);
    }
}

