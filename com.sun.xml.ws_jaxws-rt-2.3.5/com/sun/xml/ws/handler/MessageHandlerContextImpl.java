/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.Nullable
 */
package com.sun.xml.ws.handler;

import com.sun.istack.Nullable;
import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.handler.MessageHandlerContext;
import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.model.SEIModel;
import com.sun.xml.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.ws.handler.MessageUpdatableContext;
import java.util.Set;

public class MessageHandlerContextImpl
extends MessageUpdatableContext
implements MessageHandlerContext {
    @Nullable
    private SEIModel seiModel;
    private Set<String> roles;
    private WSBinding binding;
    @Nullable
    private WSDLPort wsdlModel;

    public MessageHandlerContextImpl(@Nullable SEIModel seiModel, WSBinding binding, @Nullable WSDLPort wsdlModel, Packet packet, Set<String> roles) {
        super(packet);
        this.seiModel = seiModel;
        this.binding = binding;
        this.wsdlModel = wsdlModel;
        this.roles = roles;
    }

    @Override
    public Message getMessage() {
        return this.packet.getMessage();
    }

    @Override
    public void setMessage(Message message) {
        this.packet.setMessage(message);
    }

    @Override
    public Set<String> getRoles() {
        return this.roles;
    }

    @Override
    public WSBinding getWSBinding() {
        return this.binding;
    }

    @Override
    @Nullable
    public SEIModel getSEIModel() {
        return this.seiModel;
    }

    @Override
    @Nullable
    public WSDLPort getPort() {
        return this.wsdlModel;
    }

    @Override
    void updateMessage() {
    }

    @Override
    void setPacketMessage(Message newMessage) {
        this.setMessage(newMessage);
    }
}

