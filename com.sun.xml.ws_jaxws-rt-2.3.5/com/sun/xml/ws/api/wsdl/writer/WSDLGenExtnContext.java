/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 *  com.sun.xml.txw2.TypedXmlWriter
 */
package com.sun.xml.ws.api.wsdl.writer;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.txw2.TypedXmlWriter;
import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.model.SEIModel;
import com.sun.xml.ws.api.server.Container;

public class WSDLGenExtnContext {
    private final TypedXmlWriter root;
    private final SEIModel model;
    private final WSBinding binding;
    private final Container container;
    private final Class endpointClass;

    public WSDLGenExtnContext(@NotNull TypedXmlWriter root, @NotNull SEIModel model, @NotNull WSBinding binding, @Nullable Container container, @NotNull Class endpointClass) {
        this.root = root;
        this.model = model;
        this.binding = binding;
        this.container = container;
        this.endpointClass = endpointClass;
    }

    public TypedXmlWriter getRoot() {
        return this.root;
    }

    public SEIModel getModel() {
        return this.model;
    }

    public WSBinding getBinding() {
        return this.binding;
    }

    public Container getContainer() {
        return this.container;
    }

    public Class getEndpointClass() {
        return this.endpointClass;
    }
}

