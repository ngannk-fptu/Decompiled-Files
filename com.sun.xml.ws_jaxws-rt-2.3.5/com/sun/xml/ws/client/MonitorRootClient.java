/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.glassfish.gmbal.AMXMetadata
 *  org.glassfish.gmbal.Description
 *  org.glassfish.gmbal.ManagedAttribute
 *  org.glassfish.gmbal.ManagedObject
 */
package com.sun.xml.ws.client;

import com.sun.xml.ws.api.model.wsdl.WSDLService;
import com.sun.xml.ws.api.server.Container;
import com.sun.xml.ws.client.PortInfo;
import com.sun.xml.ws.client.Stub;
import com.sun.xml.ws.server.MonitorBase;
import java.net.URL;
import java.util.Map;
import javax.xml.namespace.QName;
import org.glassfish.gmbal.AMXMetadata;
import org.glassfish.gmbal.Description;
import org.glassfish.gmbal.ManagedAttribute;
import org.glassfish.gmbal.ManagedObject;

@ManagedObject
@Description(value="Metro Web Service client")
@AMXMetadata(type="WSClient")
public final class MonitorRootClient
extends MonitorBase {
    private final Stub stub;

    MonitorRootClient(Stub stub) {
        this.stub = stub;
    }

    @ManagedAttribute
    private Container getContainer() {
        return this.stub.owner.getContainer();
    }

    @ManagedAttribute
    private Map<QName, PortInfo> qnameToPortInfoMap() {
        return this.stub.owner.getQNameToPortInfoMap();
    }

    @ManagedAttribute
    private QName serviceName() {
        return this.stub.owner.getServiceName();
    }

    @ManagedAttribute
    private Class serviceClass() {
        return this.stub.owner.getServiceClass();
    }

    @ManagedAttribute
    private URL wsdlDocumentLocation() {
        return this.stub.owner.getWSDLDocumentLocation();
    }

    @ManagedAttribute
    private WSDLService wsdlService() {
        return this.stub.owner.getWsdlService();
    }
}

