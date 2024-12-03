/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.axiom.util.stax.dialect;

import javax.xml.stream.XMLResolver;
import javax.xml.stream.XMLStreamException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

final class SecureXMLResolver
implements XMLResolver {
    private static final Log log = LogFactory.getLog(SecureXMLResolver.class);

    SecureXMLResolver() {
    }

    public Object resolveEntity(String publicID, String systemID, String baseURI, String namespace) throws XMLStreamException {
        if (log.isDebugEnabled()) {
            log.debug((Object)("resolveEntity is disabled because this is a secure XMLStreamReader(" + publicID + ") (" + systemID + ") (" + baseURI + ") (" + namespace + ")"));
        }
        throw new XMLStreamException("Reading external entities is disabled");
    }
}

