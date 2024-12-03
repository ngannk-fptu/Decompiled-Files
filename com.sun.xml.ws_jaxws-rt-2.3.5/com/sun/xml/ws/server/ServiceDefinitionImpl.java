/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 */
package com.sun.xml.ws.server;

import com.sun.istack.NotNull;
import com.sun.xml.ws.api.server.SDDocument;
import com.sun.xml.ws.api.server.SDDocumentFilter;
import com.sun.xml.ws.api.server.ServiceDefinition;
import com.sun.xml.ws.server.SDDocumentImpl;
import com.sun.xml.ws.server.WSEndpointImpl;
import com.sun.xml.ws.wsdl.SDDocumentResolver;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class ServiceDefinitionImpl
implements ServiceDefinition,
SDDocumentResolver {
    private final Collection<SDDocumentImpl> docs;
    private final Map<String, SDDocumentImpl> bySystemId;
    @NotNull
    private final SDDocumentImpl primaryWsdl;
    WSEndpointImpl<?> owner;
    final List<SDDocumentFilter> filters = new ArrayList<SDDocumentFilter>();
    private boolean isInitialized = false;

    public ServiceDefinitionImpl(Collection<SDDocumentImpl> docs, @NotNull SDDocumentImpl primaryWsdl) {
        assert (docs.contains(primaryWsdl));
        this.docs = docs;
        this.primaryWsdl = primaryWsdl;
        this.bySystemId = new HashMap<String, SDDocumentImpl>();
    }

    private synchronized void init() {
        if (this.isInitialized) {
            return;
        }
        this.isInitialized = true;
        for (SDDocumentImpl doc : this.docs) {
            this.bySystemId.put(doc.getURL().toExternalForm(), doc);
            doc.setFilters(this.filters);
            doc.setResolver(this);
        }
    }

    void setOwner(WSEndpointImpl<?> owner) {
        assert (owner != null && this.owner == null);
        this.owner = owner;
    }

    @Override
    @NotNull
    public SDDocument getPrimary() {
        return this.primaryWsdl;
    }

    @Override
    public void addFilter(SDDocumentFilter filter) {
        this.filters.add(filter);
    }

    @Override
    public Iterator<SDDocument> iterator() {
        this.init();
        return this.docs.iterator();
    }

    @Override
    public SDDocument resolve(String systemId) {
        this.init();
        return this.bySystemId.get(systemId);
    }
}

