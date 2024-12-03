/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.util.Assert
 */
package org.eclipse.gemini.blueprint.context.support;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

class ChainedEntityResolver
implements EntityResolver {
    private static final Log log = LogFactory.getLog(ChainedEntityResolver.class);
    private final Map<EntityResolver, String> resolvers = new LinkedHashMap<EntityResolver, String>(2);

    ChainedEntityResolver() {
    }

    public void addEntityResolver(EntityResolver resolver, String resolverToString) {
        Assert.notNull((Object)resolver);
        this.resolvers.put(resolver, resolverToString);
    }

    @Override
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
        boolean trace = log.isTraceEnabled();
        for (Map.Entry<EntityResolver, String> entry : this.resolvers.entrySet()) {
            InputSource entity;
            String resolvedMsg;
            EntityResolver entityResolver = entry.getKey();
            if (trace) {
                log.trace((Object)("Trying to resolve entity [" + publicId + "|" + systemId + "] through resolver " + entry.getValue()));
            }
            String string = resolvedMsg = (entity = entityResolver.resolveEntity(publicId, systemId)) != null ? "" : "not ";
            if (trace) {
                log.trace((Object)("Entity [" + publicId + "|" + systemId + "] was " + resolvedMsg + "resolved through entity resolver " + entry.getValue()));
            }
            if (entity == null) continue;
            return entity;
        }
        return null;
    }
}

