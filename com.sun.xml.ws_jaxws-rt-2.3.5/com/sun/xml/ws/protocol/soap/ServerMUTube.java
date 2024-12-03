/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.protocol.soap;

import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.pipe.NextAction;
import com.sun.xml.ws.api.pipe.ServerTubeAssemblerContext;
import com.sun.xml.ws.api.pipe.Tube;
import com.sun.xml.ws.api.pipe.TubeCloner;
import com.sun.xml.ws.client.HandlerConfiguration;
import com.sun.xml.ws.protocol.soap.MUTube;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.xml.namespace.QName;

public class ServerMUTube
extends MUTube {
    private ServerTubeAssemblerContext tubeContext;
    private final Set<String> roles;
    private final Set<QName> handlerKnownHeaders;
    private final Lock lock = new ReentrantLock();

    public ServerMUTube(ServerTubeAssemblerContext tubeContext, Tube next) {
        super(tubeContext.getEndpoint().getBinding(), next);
        this.tubeContext = tubeContext;
        HandlerConfiguration handlerConfig = this.binding.getHandlerConfig();
        this.roles = handlerConfig.getRoles();
        this.handlerKnownHeaders = this.binding.getKnownHeaders();
    }

    protected ServerMUTube(ServerMUTube that, TubeCloner cloner) {
        super(that, cloner);
        this.tubeContext = that.tubeContext;
        this.roles = that.roles;
        this.handlerKnownHeaders = that.handlerKnownHeaders;
    }

    @Override
    public NextAction processRequest(Packet request) {
        Set<QName> misUnderstoodHeaders = null;
        this.lock.lock();
        try {
            misUnderstoodHeaders = this.getMisUnderstoodHeaders(request.getMessage().getHeaders(), this.roles, this.handlerKnownHeaders);
        }
        finally {
            this.lock.unlock();
        }
        if (misUnderstoodHeaders == null || misUnderstoodHeaders.isEmpty()) {
            return this.doInvoke(this.next, request);
        }
        return this.doReturnWith(request.createServerResponse(this.createMUSOAPFaultMessage(misUnderstoodHeaders), this.tubeContext.getWsdlModel(), this.tubeContext.getSEIModel(), this.tubeContext.getEndpoint().getBinding()));
    }

    @Override
    public ServerMUTube copy(TubeCloner cloner) {
        return new ServerMUTube(this, cloner);
    }
}

