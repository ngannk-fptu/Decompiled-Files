/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 *  javax.xml.ws.soap.SOAPBinding
 */
package com.sun.xml.ws.api.pipe;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.ws.addressing.W3CWsaServerTube;
import com.sun.xml.ws.addressing.v200408.MemberSubmissionWsaServerTube;
import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.addressing.AddressingVersion;
import com.sun.xml.ws.api.model.SEIModel;
import com.sun.xml.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.ws.api.pipe.Codec;
import com.sun.xml.ws.api.pipe.ServerPipeAssemblerContext;
import com.sun.xml.ws.api.pipe.Tube;
import com.sun.xml.ws.api.pipe.helper.PipeAdapter;
import com.sun.xml.ws.api.server.ServerPipelineHook;
import com.sun.xml.ws.api.server.WSEndpoint;
import com.sun.xml.ws.binding.BindingImpl;
import com.sun.xml.ws.developer.SchemaValidationFeature;
import com.sun.xml.ws.handler.HandlerTube;
import com.sun.xml.ws.handler.ServerLogicalHandlerTube;
import com.sun.xml.ws.handler.ServerMessageHandlerTube;
import com.sun.xml.ws.handler.ServerSOAPHandlerTube;
import com.sun.xml.ws.protocol.soap.ServerMUTube;
import com.sun.xml.ws.server.ServerSchemaValidationTube;
import com.sun.xml.ws.util.pipe.DumpTube;
import java.io.PrintStream;
import javax.xml.ws.soap.SOAPBinding;

public class ServerTubeAssemblerContext {
    private final SEIModel seiModel;
    private final WSDLPort wsdlModel;
    private final WSEndpoint endpoint;
    private final BindingImpl binding;
    private final Tube terminal;
    private final boolean isSynchronous;
    @NotNull
    private Codec codec;

    public ServerTubeAssemblerContext(@Nullable SEIModel seiModel, @Nullable WSDLPort wsdlModel, @NotNull WSEndpoint endpoint, @NotNull Tube terminal, boolean isSynchronous) {
        this.seiModel = seiModel;
        this.wsdlModel = wsdlModel;
        this.endpoint = endpoint;
        this.terminal = terminal;
        this.binding = (BindingImpl)endpoint.getBinding();
        this.isSynchronous = isSynchronous;
        this.codec = this.binding.createCodec();
    }

    @Nullable
    public SEIModel getSEIModel() {
        return this.seiModel;
    }

    @Nullable
    public WSDLPort getWsdlModel() {
        return this.wsdlModel;
    }

    @NotNull
    public WSEndpoint<?> getEndpoint() {
        return this.endpoint;
    }

    @NotNull
    public Tube getTerminalTube() {
        return this.terminal;
    }

    public boolean isSynchronous() {
        return this.isSynchronous;
    }

    @NotNull
    public Tube createServerMUTube(@NotNull Tube next) {
        if (this.binding instanceof SOAPBinding) {
            return new ServerMUTube(this, next);
        }
        return next;
    }

    @NotNull
    public Tube createHandlerTube(@NotNull Tube next) {
        if (!this.binding.getHandlerChain().isEmpty()) {
            HandlerTube cousin = new ServerLogicalHandlerTube((WSBinding)this.binding, this.seiModel, this.wsdlModel, next);
            next = cousin;
            if (this.binding instanceof SOAPBinding) {
                cousin = new ServerSOAPHandlerTube((WSBinding)this.binding, next, cousin);
                next = cousin;
                next = new ServerMessageHandlerTube(this.seiModel, this.binding, next, cousin);
            }
        }
        return next;
    }

    @NotNull
    public Tube createMonitoringTube(@NotNull Tube next) {
        ServerPipelineHook hook = this.endpoint.getContainer().getSPI(ServerPipelineHook.class);
        if (hook != null) {
            ServerPipeAssemblerContext ctxt = new ServerPipeAssemblerContext(this.seiModel, this.wsdlModel, this.endpoint, this.terminal, this.isSynchronous);
            return PipeAdapter.adapt(hook.createMonitoringPipe(ctxt, PipeAdapter.adapt(next)));
        }
        return next;
    }

    @NotNull
    public Tube createSecurityTube(@NotNull Tube next) {
        ServerPipelineHook hook = this.endpoint.getContainer().getSPI(ServerPipelineHook.class);
        if (hook != null) {
            ServerPipeAssemblerContext ctxt = new ServerPipeAssemblerContext(this.seiModel, this.wsdlModel, this.endpoint, this.terminal, this.isSynchronous);
            return PipeAdapter.adapt(hook.createSecurityPipe(ctxt, PipeAdapter.adapt(next)));
        }
        return next;
    }

    public Tube createDumpTube(String name, PrintStream out, Tube next) {
        return new DumpTube(name, out, next);
    }

    public Tube createValidationTube(Tube next) {
        if (this.binding instanceof SOAPBinding && this.binding.isFeatureEnabled(SchemaValidationFeature.class) && this.wsdlModel != null) {
            return new ServerSchemaValidationTube(this.endpoint, this.binding, this.seiModel, this.wsdlModel, next);
        }
        return next;
    }

    public Tube createWsaTube(Tube next) {
        if (this.binding instanceof SOAPBinding && AddressingVersion.isEnabled(this.binding)) {
            if (AddressingVersion.fromBinding(this.binding) == AddressingVersion.MEMBER) {
                return new MemberSubmissionWsaServerTube(this.endpoint, this.wsdlModel, this.binding, next);
            }
            return new W3CWsaServerTube(this.endpoint, this.wsdlModel, this.binding, next);
        }
        return next;
    }

    @NotNull
    public Codec getCodec() {
        return this.codec;
    }

    public void setCodec(@NotNull Codec codec) {
        this.codec = codec;
    }
}

