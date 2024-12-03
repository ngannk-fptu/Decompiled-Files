/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 */
package com.sun.xml.ws.server.sei;

import com.oracle.webservices.api.databinding.JavaCallInfo;
import com.sun.istack.NotNull;
import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.pipe.NextAction;
import com.sun.xml.ws.api.server.Invoker;
import com.sun.xml.ws.model.AbstractSEIModelImpl;
import com.sun.xml.ws.server.InvokerTube;
import com.sun.xml.ws.wsdl.DispatchException;
import java.lang.reflect.InvocationTargetException;

public class SEIInvokerTube
extends InvokerTube {
    private final WSBinding binding;
    private final AbstractSEIModelImpl model;

    public SEIInvokerTube(AbstractSEIModelImpl model, Invoker invoker, WSBinding binding) {
        super(invoker);
        this.binding = binding;
        this.model = model;
    }

    @Override
    @NotNull
    public NextAction processRequest(@NotNull Packet req) {
        JavaCallInfo call = this.model.getDatabinding().deserializeRequest(req);
        if (call.getException() == null) {
            try {
                if (req.getMessage().isOneWay(this.model.getPort()) && req.transportBackChannel != null) {
                    req.transportBackChannel.close();
                }
                Object ret = this.getInvoker(req).invoke(req, call.getMethod(), call.getParameters());
                call.setReturnValue(ret);
            }
            catch (InvocationTargetException e) {
                call.setException(e);
            }
            catch (Exception e) {
                call.setException(e);
            }
        } else if (call.getException() instanceof DispatchException) {
            DispatchException e = (DispatchException)call.getException();
            return this.doReturnWith(req.createServerResponse(e.fault, this.model.getPort(), null, this.binding));
        }
        Packet res = (Packet)this.model.getDatabinding().serializeResponse(call);
        res = req.relateServerResponse(res, req.endpoint.getPort(), this.model, req.endpoint.getBinding());
        assert (res != null);
        return this.doReturnWith(res);
    }

    @Override
    @NotNull
    public NextAction processResponse(@NotNull Packet response) {
        return this.doReturnWith(response);
    }

    @Override
    @NotNull
    public NextAction processException(@NotNull Throwable t) {
        return this.doThrow(t);
    }
}

