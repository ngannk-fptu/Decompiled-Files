/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.server.remoting.davex;

import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.jackrabbit.server.remoting.davex.ProtectedItemRemoveHandler;
import org.apache.jackrabbit.server.remoting.davex.ProtectedRemoveManager;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.apache.jackrabbit.webdav.xml.ElementIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

class ProtectedRemoveConfig {
    private static final Logger log = LoggerFactory.getLogger(ProtectedRemoveConfig.class);
    private static final String ELEMENT_HANDLER = "protecteditemremovehandler";
    private static final String ELEMENT_CLASS = "class";
    private static final String ATTRIBUTE_NAME = "name";
    private final ProtectedRemoveManager manager;

    ProtectedRemoveConfig(ProtectedRemoveManager manager) {
        this.manager = manager;
    }

    void parse(InputStream inputStream) throws IOException {
        ProtectedItemRemoveHandler instance = null;
        try {
            Element config = DomUtil.parseDocument(inputStream).getDocumentElement();
            if (config == null) {
                log.warn("Missing mandatory config element");
                return;
            }
            ElementIterator handlers = DomUtil.getChildren(config, ELEMENT_HANDLER, null);
            while (handlers.hasNext()) {
                Element handler = handlers.nextElement();
                instance = this.createHandler(handler);
                this.manager.addHandler(instance);
            }
        }
        catch (ParserConfigurationException e) {
            log.error(e.getMessage(), (Throwable)e);
        }
        catch (SAXException e) {
            log.error(e.getMessage(), (Throwable)e);
        }
    }

    private ProtectedItemRemoveHandler createHandler(Element parent) {
        String className;
        ProtectedItemRemoveHandler instance = null;
        Element classElem = DomUtil.getChildElement(parent, ELEMENT_CLASS, null);
        if (classElem != null && (className = DomUtil.getAttribute(classElem, ATTRIBUTE_NAME, null)) != null) {
            instance = this.manager.createHandler(className);
        }
        return instance;
    }
}

