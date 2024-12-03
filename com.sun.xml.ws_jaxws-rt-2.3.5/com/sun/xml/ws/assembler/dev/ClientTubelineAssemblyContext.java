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
import com.sun.xml.ws.api.EndpointAddress;
import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.WSService;
import com.sun.xml.ws.api.client.WSPortInfo;
import com.sun.xml.ws.api.model.SEIModel;
import com.sun.xml.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.ws.api.pipe.ClientTubeAssemblerContext;
import com.sun.xml.ws.api.pipe.Codec;
import com.sun.xml.ws.api.server.Container;
import com.sun.xml.ws.assembler.dev.TubelineAssemblyContext;
import com.sun.xml.ws.policy.PolicyMap;

public interface ClientTubelineAssemblyContext
extends TubelineAssemblyContext {
    @NotNull
    public EndpointAddress getAddress();

    @NotNull
    public WSBinding getBinding();

    @NotNull
    public Codec getCodec();

    public Container getContainer();

    public PolicyMap getPolicyMap();

    public WSPortInfo getPortInfo();

    @Nullable
    public SEIModel getSEIModel();

    @NotNull
    public WSService getService();

    public ClientTubeAssemblerContext getWrappedContext();

    public WSDLPort getWsdlPort();

    public boolean isPolicyAvailable();

    public void setCodec(@NotNull Codec var1);
}

