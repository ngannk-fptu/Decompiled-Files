/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 *  com.sun.xml.ws.policy.PolicyMap
 */
package com.sun.xml.ws.assembler.dev;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.ws.api.model.SEIModel;
import com.sun.xml.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.ws.api.pipe.Codec;
import com.sun.xml.ws.api.pipe.ServerTubeAssemblerContext;
import com.sun.xml.ws.api.pipe.Tube;
import com.sun.xml.ws.api.server.WSEndpoint;
import com.sun.xml.ws.assembler.dev.TubelineAssemblyContext;
import com.sun.xml.ws.policy.PolicyMap;

public interface ServerTubelineAssemblyContext
extends TubelineAssemblyContext {
    @NotNull
    public Codec getCodec();

    @NotNull
    public WSEndpoint getEndpoint();

    public PolicyMap getPolicyMap();

    @Nullable
    public SEIModel getSEIModel();

    @NotNull
    public Tube getTerminalTube();

    public ServerTubeAssemblerContext getWrappedContext();

    @Nullable
    public WSDLPort getWsdlPort();

    public boolean isPolicyAvailable();

    public boolean isSynchronous();

    public void setCodec(@NotNull Codec var1);
}

