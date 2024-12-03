/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataHandler
 *  javax.xml.ws.handler.MessageContext
 *  javax.xml.ws.handler.MessageContext$Scope
 */
package com.sun.xml.ws.server;

import com.sun.xml.ws.api.message.Attachment;
import com.sun.xml.ws.api.message.AttachmentSet;
import com.sun.xml.ws.api.message.Packet;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.activation.DataHandler;
import javax.xml.ws.handler.MessageContext;

public final class EndpointMessageContextImpl
extends AbstractMap<String, Object>
implements MessageContext {
    private Set<Map.Entry<String, Object>> entrySet;
    private final Packet packet;

    public EndpointMessageContextImpl(Packet packet) {
        this.packet = packet;
    }

    @Override
    public Object get(Object key) {
        if (this.packet.supports(key)) {
            return this.packet.get(key);
        }
        if (this.packet.getHandlerScopePropertyNames(true).contains(key)) {
            return null;
        }
        Object value = this.packet.invocationProperties.get(key);
        if (key.equals("javax.xml.ws.binding.attachments.outbound") || key.equals("javax.xml.ws.binding.attachments.inbound")) {
            HashMap<String, DataHandler> atts = (HashMap<String, DataHandler>)value;
            if (atts == null) {
                atts = new HashMap<String, DataHandler>();
            }
            AttachmentSet attSet = this.packet.getMessage().getAttachments();
            for (Attachment att : attSet) {
                atts.put(att.getContentId(), att.asDataHandler());
            }
            return atts;
        }
        return value;
    }

    @Override
    public Object put(String key, Object value) {
        if (this.packet.supports(key)) {
            return this.packet.put(key, value);
        }
        Object old = this.packet.invocationProperties.get(key);
        if (old != null) {
            if (this.packet.getHandlerScopePropertyNames(true).contains(key)) {
                throw new IllegalArgumentException("Cannot overwrite property in HANDLER scope");
            }
            this.packet.invocationProperties.put(key, value);
            return old;
        }
        this.packet.invocationProperties.put(key, value);
        return null;
    }

    @Override
    public Object remove(Object key) {
        if (this.packet.supports(key)) {
            return this.packet.remove(key);
        }
        Object old = this.packet.invocationProperties.get(key);
        if (old != null) {
            if (this.packet.getHandlerScopePropertyNames(true).contains(key)) {
                throw new IllegalArgumentException("Cannot remove property in HANDLER scope");
            }
            this.packet.invocationProperties.remove(key);
            return old;
        }
        return null;
    }

    @Override
    public Set<Map.Entry<String, Object>> entrySet() {
        if (this.entrySet == null) {
            this.entrySet = new EntrySet();
        }
        return this.entrySet;
    }

    public void setScope(String name, MessageContext.Scope scope) {
        throw new UnsupportedOperationException("All the properties in this context are in APPLICATION scope. Cannot do setScope().");
    }

    public MessageContext.Scope getScope(String name) {
        throw new UnsupportedOperationException("All the properties in this context are in APPLICATION scope. Cannot do getScope().");
    }

    private Map<String, Object> createBackupMap() {
        HashMap<String, Object> backupMap = new HashMap<String, Object>();
        backupMap.putAll(this.packet.createMapView());
        Set<String> handlerProps = this.packet.getHandlerScopePropertyNames(true);
        for (Map.Entry<String, Object> e : this.packet.invocationProperties.entrySet()) {
            if (handlerProps.contains(e.getKey())) continue;
            backupMap.put(e.getKey(), e.getValue());
        }
        return backupMap;
    }

    private class EntrySet
    extends AbstractSet<Map.Entry<String, Object>> {
        private EntrySet() {
        }

        @Override
        public Iterator<Map.Entry<String, Object>> iterator() {
            final Iterator it = EndpointMessageContextImpl.this.createBackupMap().entrySet().iterator();
            return new Iterator<Map.Entry<String, Object>>(){
                Map.Entry<String, Object> cur;

                @Override
                public boolean hasNext() {
                    return it.hasNext();
                }

                @Override
                public Map.Entry<String, Object> next() {
                    this.cur = (Map.Entry)it.next();
                    return this.cur;
                }

                @Override
                public void remove() {
                    it.remove();
                    EndpointMessageContextImpl.this.remove(this.cur.getKey());
                }
            };
        }

        @Override
        public int size() {
            return EndpointMessageContextImpl.this.createBackupMap().size();
        }
    }
}

