/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 */
package com.sun.xml.ws.model.wsdl;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLModel;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLPort;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLService;
import com.sun.xml.ws.model.wsdl.AbstractExtensibleImpl;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

public final class WSDLServiceImpl
extends AbstractExtensibleImpl
implements EditableWSDLService {
    private final QName name;
    private final Map<QName, EditableWSDLPort> ports;
    private final EditableWSDLModel parent;

    public WSDLServiceImpl(XMLStreamReader xsr, EditableWSDLModel parent, QName name) {
        super(xsr);
        this.parent = parent;
        this.name = name;
        this.ports = new LinkedHashMap<QName, EditableWSDLPort>();
    }

    @Override
    @NotNull
    public EditableWSDLModel getParent() {
        return this.parent;
    }

    @Override
    public QName getName() {
        return this.name;
    }

    @Override
    public EditableWSDLPort get(QName portName) {
        return this.ports.get(portName);
    }

    @Override
    public EditableWSDLPort getFirstPort() {
        if (this.ports.isEmpty()) {
            return null;
        }
        return this.ports.values().iterator().next();
    }

    public Iterable<EditableWSDLPort> getPorts() {
        return this.ports.values();
    }

    @Override
    @Nullable
    public EditableWSDLPort getMatchingPort(QName portTypeName) {
        for (EditableWSDLPort port : this.getPorts()) {
            QName ptName = port.getBinding().getPortTypeName();
            assert (ptName != null);
            if (!ptName.equals(portTypeName)) continue;
            return port;
        }
        return null;
    }

    @Override
    public void put(QName portName, EditableWSDLPort port) {
        if (portName == null || port == null) {
            throw new NullPointerException();
        }
        this.ports.put(portName, port);
    }

    @Override
    public void freeze(EditableWSDLModel root) {
        for (EditableWSDLPort port : this.ports.values()) {
            port.freeze(root);
        }
    }
}

