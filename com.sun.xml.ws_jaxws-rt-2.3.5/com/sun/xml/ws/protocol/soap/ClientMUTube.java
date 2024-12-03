/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 */
package com.sun.xml.ws.protocol.soap;

import com.sun.istack.NotNull;
import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.pipe.NextAction;
import com.sun.xml.ws.api.pipe.Tube;
import com.sun.xml.ws.api.pipe.TubeCloner;
import com.sun.xml.ws.client.HandlerConfiguration;
import com.sun.xml.ws.protocol.soap.MUTube;
import java.util.Set;
import javax.xml.namespace.QName;

public class ClientMUTube
extends MUTube {
    public ClientMUTube(WSBinding binding, Tube next) {
        super(binding, next);
    }

    protected ClientMUTube(ClientMUTube that, TubeCloner cloner) {
        super(that, cloner);
    }

    @Override
    @NotNull
    public NextAction processResponse(Packet response) {
        Set<QName> misUnderstoodHeaders;
        if (response.getMessage() == null) {
            return super.processResponse(response);
        }
        HandlerConfiguration handlerConfig = response.handlerConfig;
        if (handlerConfig == null) {
            handlerConfig = this.binding.getHandlerConfig();
        }
        if ((misUnderstoodHeaders = this.getMisUnderstoodHeaders(response.getMessage().getHeaders(), handlerConfig.getRoles(), this.binding.getKnownHeaders())) == null || misUnderstoodHeaders.isEmpty()) {
            return super.processResponse(response);
        }
        throw this.createMUSOAPFaultException(misUnderstoodHeaders);
    }

    @Override
    public ClientMUTube copy(TubeCloner cloner) {
        return new ClientMUTube(this, cloner);
    }
}

