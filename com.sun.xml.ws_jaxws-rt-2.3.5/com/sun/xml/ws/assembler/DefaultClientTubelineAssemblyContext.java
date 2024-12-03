/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 *  com.sun.xml.ws.policy.PolicyMap
 */
package com.sun.xml.ws.assembler;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.ws.api.EndpointAddress;
import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.WSService;
import com.sun.xml.ws.api.client.WSPortInfo;
import com.sun.xml.ws.api.model.SEIModel;
import com.sun.xml.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.ws.api.pipe.ClientTubeAssemblerContext;
import com.sun.xml.ws.api.pipe.Codec;
import com.sun.xml.ws.api.server.Container;
import com.sun.xml.ws.assembler.TubelineAssemblyContextImpl;
import com.sun.xml.ws.assembler.dev.ClientTubelineAssemblyContext;
import com.sun.xml.ws.policy.PolicyMap;

class DefaultClientTubelineAssemblyContext
extends TubelineAssemblyContextImpl
implements ClientTubelineAssemblyContext {
    @NotNull
    private final ClientTubeAssemblerContext wrappedContext;
    private final PolicyMap policyMap;
    private final WSPortInfo portInfo;
    private final WSDLPort wsdlPort;

    public DefaultClientTubelineAssemblyContext(@NotNull ClientTubeAssemblerContext context) {
        this.wrappedContext = context;
        this.wsdlPort = context.getWsdlModel();
        this.portInfo = context.getPortInfo();
        this.policyMap = context.getPortInfo().getPolicyMap();
    }

    @Override
    public PolicyMap getPolicyMap() {
        return this.policyMap;
    }

    @Override
    public boolean isPolicyAvailable() {
        return this.policyMap != null && !this.policyMap.isEmpty();
    }

    @Override
    public WSDLPort getWsdlPort() {
        return this.wsdlPort;
    }

    @Override
    public WSPortInfo getPortInfo() {
        return this.portInfo;
    }

    @Override
    @NotNull
    public EndpointAddress getAddress() {
        return this.wrappedContext.getAddress();
    }

    @Override
    @NotNull
    public WSService getService() {
        return this.wrappedContext.getService();
    }

    @Override
    @NotNull
    public WSBinding getBinding() {
        return this.wrappedContext.getBinding();
    }

    @Override
    @Nullable
    public SEIModel getSEIModel() {
        return this.wrappedContext.getSEIModel();
    }

    @Override
    public Container getContainer() {
        return this.wrappedContext.getContainer();
    }

    @Override
    @NotNull
    public Codec getCodec() {
        return this.wrappedContext.getCodec();
    }

    @Override
    public void setCodec(@NotNull Codec codec) {
        this.wrappedContext.setCodec(codec);
    }

    @Override
    public ClientTubeAssemblerContext getWrappedContext() {
        return this.wrappedContext;
    }
}

