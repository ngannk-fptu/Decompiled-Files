/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 */
package com.sun.xml.ws.api.pipe;

import com.sun.istack.NotNull;
import com.sun.xml.ws.api.EndpointAddress;
import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.WSService;
import com.sun.xml.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.ws.api.pipe.ClientTubeAssemblerContext;
import com.sun.xml.ws.api.pipe.Pipe;
import com.sun.xml.ws.api.pipe.helper.PipeAdapter;
import com.sun.xml.ws.api.server.Container;
import java.io.PrintStream;

public final class ClientPipeAssemblerContext
extends ClientTubeAssemblerContext {
    public ClientPipeAssemblerContext(@NotNull EndpointAddress address, @NotNull WSDLPort wsdlModel, @NotNull WSService rootOwner, @NotNull WSBinding binding) {
        this(address, wsdlModel, rootOwner, binding, Container.NONE);
    }

    public ClientPipeAssemblerContext(@NotNull EndpointAddress address, @NotNull WSDLPort wsdlModel, @NotNull WSService rootOwner, @NotNull WSBinding binding, @NotNull Container container) {
        super(address, wsdlModel, rootOwner, binding, container);
    }

    public Pipe createDumpPipe(String name, PrintStream out, Pipe next) {
        return PipeAdapter.adapt(super.createDumpTube(name, out, PipeAdapter.adapt(next)));
    }

    public Pipe createWsaPipe(Pipe next) {
        return PipeAdapter.adapt(super.createWsaTube(PipeAdapter.adapt(next)));
    }

    public Pipe createClientMUPipe(Pipe next) {
        return PipeAdapter.adapt(super.createClientMUTube(PipeAdapter.adapt(next)));
    }

    public Pipe createValidationPipe(Pipe next) {
        return PipeAdapter.adapt(super.createValidationTube(PipeAdapter.adapt(next)));
    }

    public Pipe createHandlerPipe(Pipe next) {
        return PipeAdapter.adapt(super.createHandlerTube(PipeAdapter.adapt(next)));
    }

    @NotNull
    public Pipe createSecurityPipe(@NotNull Pipe next) {
        return PipeAdapter.adapt(super.createSecurityTube(PipeAdapter.adapt(next)));
    }

    public Pipe createTransportPipe() {
        return PipeAdapter.adapt(super.createTransportTube());
    }
}

