/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.beans.factory.xml.DefaultDocumentLoader
 */
package org.eclipse.gemini.blueprint.context.support;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.xml.DefaultDocumentLoader;

class BlueprintDocumentLoader
extends DefaultDocumentLoader {
    static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";
    static final String BLUEPRINT_SCHEMA = "http://www.osgi.org/xmlns/blueprint/v1.0.0/blueprint.xsd";
    private static final Log log = LogFactory.getLog(BlueprintDocumentLoader.class);

    BlueprintDocumentLoader() {
    }

    protected DocumentBuilderFactory createDocumentBuilderFactory(int validationMode, boolean namespaceAware) throws ParserConfigurationException {
        DocumentBuilderFactory factory = super.createDocumentBuilderFactory(validationMode, namespaceAware);
        try {
            factory.setAttribute(JAXP_SCHEMA_SOURCE, BLUEPRINT_SCHEMA);
        }
        catch (IllegalArgumentException ex) {
            log.warn((Object)"Cannot work with attribute http://java.sun.com/xml/jaxp/properties/schemaSource - configurations w/o a schema locations will likely fail to validate", (Throwable)ex);
        }
        return factory;
    }
}

