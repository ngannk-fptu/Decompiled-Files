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
import com.sun.xml.ws.api.model.SEIModel;
import com.sun.xml.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.ws.api.pipe.Codec;
import com.sun.xml.ws.api.pipe.ServerTubeAssemblerContext;
import com.sun.xml.ws.api.pipe.Tube;
import com.sun.xml.ws.api.server.WSEndpoint;
import com.sun.xml.ws.assembler.TubelineAssemblyContextImpl;
import com.sun.xml.ws.assembler.dev.ServerTubelineAssemblyContext;
import com.sun.xml.ws.policy.PolicyMap;

class DefaultServerTubelineAssemblyContext
extends TubelineAssemblyContextImpl
implements ServerTubelineAssemblyContext {
    @NotNull
    private final ServerTubeAssemblerContext wrappedContext;
    private final PolicyMap policyMap;

    public DefaultServerTubelineAssemblyContext(@NotNull ServerTubeAssemblerContext context) {
        this.wrappedContext = context;
        this.policyMap = context.getEndpoint().getPolicyMap();
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
    @Nullable
    public SEIModel getSEIModel() {
        return this.wrappedContext.getSEIModel();
    }

    @Override
    @Nullable
    public WSDLPort getWsdlPort() {
        return this.wrappedContext.getWsdlModel();
    }

    @Override
    @NotNull
    public WSEndpoint getEndpoint() {
        return this.wrappedContext.getEndpoint();
    }

    @Override
    @NotNull
    public Tube getTerminalTube() {
        return this.wrappedContext.getTerminalTube();
    }

    @Override
    public boolean isSynchronous() {
        return this.wrappedContext.isSynchronous();
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
    public ServerTubeAssemblerContext getWrappedContext() {
        return this.wrappedContext;
    }
}

