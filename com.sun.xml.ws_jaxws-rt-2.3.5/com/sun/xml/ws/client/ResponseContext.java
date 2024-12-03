/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataHandler
 */
package com.sun.xml.ws.client;

import com.sun.xml.ws.api.message.Attachment;
import com.sun.xml.ws.api.message.AttachmentSet;
import com.sun.xml.ws.api.message.Packet;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.activation.DataHandler;

public class ResponseContext
extends AbstractMap<String, Object> {
    private final Packet packet;
    private Set<Map.Entry<String, Object>> entrySet;

    public ResponseContext(Packet packet) {
        this.packet = packet;
    }

    @Override
    public boolean containsKey(Object key) {
        if (this.packet.supports(key)) {
            return this.packet.containsKey(key);
        }
        if (this.packet.invocationProperties.containsKey(key)) {
            return !this.packet.getHandlerScopePropertyNames(true).contains(key);
        }
        return false;
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
        if (key.equals("javax.xml.ws.binding.attachments.inbound")) {
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
        throw new UnsupportedOperationException();
    }

    @Override
    public Object remove(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends String, ? extends Object> t) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<Map.Entry<String, Object>> entrySet() {
        if (this.entrySet == null) {
            HashMap<String, Object> r = new HashMap<String, Object>();
            r.putAll(this.packet.invocationProperties);
            r.keySet().removeAll(this.packet.getHandlerScopePropertyNames(true));
            r.putAll(this.packet.createMapView());
            this.entrySet = Collections.unmodifiableSet(r.entrySet());
        }
        return this.entrySet;
    }
}

