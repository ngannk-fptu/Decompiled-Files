/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataHandler
 *  javax.xml.ws.handler.MessageContext
 *  javax.xml.ws.handler.MessageContext$Scope
 */
package com.sun.xml.ws.handler;

import com.sun.xml.ws.api.message.Attachment;
import com.sun.xml.ws.api.message.AttachmentSet;
import com.sun.xml.ws.api.message.Packet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.activation.DataHandler;
import javax.xml.ws.handler.MessageContext;

class MessageContextImpl
implements MessageContext {
    private final Set<String> handlerScopeProps;
    private final Packet packet;
    private final Map<String, Object> asMapIncludingInvocationProperties;

    public MessageContextImpl(Packet packet) {
        this.packet = packet;
        this.asMapIncludingInvocationProperties = packet.asMapIncludingInvocationProperties();
        this.handlerScopeProps = packet.getHandlerScopePropertyNames(false);
    }

    protected void updatePacket() {
        throw new UnsupportedOperationException("wrong call");
    }

    public void setScope(String name, MessageContext.Scope scope) {
        if (!this.containsKey(name)) {
            throw new IllegalArgumentException("Property " + name + " does not exist.");
        }
        if (scope == MessageContext.Scope.APPLICATION) {
            this.handlerScopeProps.remove(name);
        } else {
            this.handlerScopeProps.add(name);
        }
    }

    public MessageContext.Scope getScope(String name) {
        if (!this.containsKey(name)) {
            throw new IllegalArgumentException("Property " + name + " does not exist.");
        }
        if (this.handlerScopeProps.contains(name)) {
            return MessageContext.Scope.HANDLER;
        }
        return MessageContext.Scope.APPLICATION;
    }

    public int size() {
        return this.asMapIncludingInvocationProperties.size();
    }

    public boolean isEmpty() {
        return this.asMapIncludingInvocationProperties.isEmpty();
    }

    public boolean containsKey(Object key) {
        return this.asMapIncludingInvocationProperties.containsKey(key);
    }

    public boolean containsValue(Object value) {
        return this.asMapIncludingInvocationProperties.containsValue(value);
    }

    public Object put(String key, Object value) {
        if (!this.asMapIncludingInvocationProperties.containsKey(key)) {
            this.handlerScopeProps.add(key);
        }
        return this.asMapIncludingInvocationProperties.put(key, value);
    }

    public Object get(Object key) {
        if (key == null) {
            return null;
        }
        Object value = this.asMapIncludingInvocationProperties.get(key);
        if (key.equals("javax.xml.ws.binding.attachments.outbound") || key.equals("javax.xml.ws.binding.attachments.inbound")) {
            HashMap<String, DataHandler> atts = (HashMap<String, DataHandler>)value;
            if (atts == null) {
                atts = new HashMap<String, DataHandler>();
            }
            AttachmentSet attSet = this.packet.getMessage().getAttachments();
            for (Attachment att : attSet) {
                String cid = att.getContentId();
                if (cid.indexOf("@jaxws.sun.com") == -1) {
                    Object a = atts.get(cid);
                    if (a != null || (a = atts.get("<" + cid + ">")) != null) continue;
                    atts.put(att.getContentId(), att.asDataHandler());
                    continue;
                }
                atts.put(att.getContentId(), att.asDataHandler());
            }
            return atts;
        }
        return value;
    }

    public void putAll(Map<? extends String, ? extends Object> t) {
        for (String string : t.keySet()) {
            if (this.asMapIncludingInvocationProperties.containsKey(string)) continue;
            this.handlerScopeProps.add(string);
        }
        this.asMapIncludingInvocationProperties.putAll(t);
    }

    public void clear() {
        this.asMapIncludingInvocationProperties.clear();
    }

    public Object remove(Object key) {
        this.handlerScopeProps.remove(key);
        return this.asMapIncludingInvocationProperties.remove(key);
    }

    public Set<String> keySet() {
        return this.asMapIncludingInvocationProperties.keySet();
    }

    public Set<Map.Entry<String, Object>> entrySet() {
        return this.asMapIncludingInvocationProperties.entrySet();
    }

    public Collection<Object> values() {
        return this.asMapIncludingInvocationProperties.values();
    }
}

