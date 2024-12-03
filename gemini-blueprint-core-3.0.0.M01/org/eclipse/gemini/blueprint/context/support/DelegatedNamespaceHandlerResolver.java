/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.beans.factory.xml.NamespaceHandler
 *  org.springframework.beans.factory.xml.NamespaceHandlerResolver
 *  org.springframework.util.Assert
 */
package org.eclipse.gemini.blueprint.context.support;

import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.xml.NamespaceHandler;
import org.springframework.beans.factory.xml.NamespaceHandlerResolver;
import org.springframework.util.Assert;

class DelegatedNamespaceHandlerResolver
implements NamespaceHandlerResolver {
    private static final Log log = LogFactory.getLog(DelegatedNamespaceHandlerResolver.class);
    private final Map<NamespaceHandlerResolver, String> resolvers = new LinkedHashMap<NamespaceHandlerResolver, String>(2);

    DelegatedNamespaceHandlerResolver() {
    }

    public void addNamespaceHandler(NamespaceHandlerResolver resolver, String resolverToString) {
        Assert.notNull((Object)resolver);
        this.resolvers.put(resolver, resolverToString);
    }

    public NamespaceHandler resolve(String namespaceUri) {
        boolean trace = log.isTraceEnabled();
        for (Map.Entry<NamespaceHandlerResolver, String> entry : this.resolvers.entrySet()) {
            NamespaceHandler handler;
            String resolvedMsg;
            NamespaceHandlerResolver handlerResolver = entry.getKey();
            if (trace) {
                log.trace((Object)("Trying to resolve namespace [" + namespaceUri + "] through resolver " + entry.getValue()));
            }
            String string = resolvedMsg = (handler = handlerResolver.resolve(namespaceUri)) != null ? "" : "not ";
            if (trace) {
                log.trace((Object)("Namespace [" + namespaceUri + "] was " + resolvedMsg + "resolved through handler resolver " + entry.getValue()));
            }
            if (handler == null) continue;
            return handler;
        }
        return null;
    }
}

