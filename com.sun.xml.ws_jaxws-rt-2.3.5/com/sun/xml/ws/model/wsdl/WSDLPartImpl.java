/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.model.wsdl;

import com.sun.xml.ws.api.model.ParameterBinding;
import com.sun.xml.ws.api.model.wsdl.WSDLPartDescriptor;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLPart;
import com.sun.xml.ws.model.wsdl.AbstractObjectImpl;
import javax.xml.stream.XMLStreamReader;

public final class WSDLPartImpl
extends AbstractObjectImpl
implements EditableWSDLPart {
    private final String name;
    private ParameterBinding binding;
    private int index;
    private final WSDLPartDescriptor descriptor;

    public WSDLPartImpl(XMLStreamReader xsr, String partName, int index, WSDLPartDescriptor descriptor) {
        super(xsr);
        this.name = partName;
        this.binding = ParameterBinding.UNBOUND;
        this.index = index;
        this.descriptor = descriptor;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public ParameterBinding getBinding() {
        return this.binding;
    }

    @Override
    public void setBinding(ParameterBinding binding) {
        this.binding = binding;
    }

    @Override
    public int getIndex() {
        return this.index;
    }

    @Override
    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public WSDLPartDescriptor getDescriptor() {
        return this.descriptor;
    }
}

