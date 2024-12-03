/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.spi.commons.namespace;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import javax.jcr.NamespaceException;
import org.apache.jackrabbit.spi.commons.namespace.NamespaceMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

public class NamespaceExtractor {
    private static Logger log = LoggerFactory.getLogger(NamespaceExtractor.class);
    private final NamespaceMapping mapping = new NamespaceMapping();
    private final Map basePrefixes = new HashMap();
    private String defaultBasePrefix;

    public NamespaceExtractor(String fileName, String dpb) throws NamespaceException {
        this.defaultBasePrefix = dpb;
        try {
            NamespaceHandler handler = new NamespaceHandler();
            XMLReader parser = XMLReaderFactory.createXMLReader();
            parser.setContentHandler(handler);
            parser.parse(new InputSource(new FileInputStream(fileName)));
        }
        catch (Exception e) {
            throw new NamespaceException();
        }
    }

    public NamespaceMapping getNamespaceMapping() {
        return this.mapping;
    }

    private class NamespaceHandler
    extends DefaultHandler {
        private NamespaceHandler() {
        }

        @Override
        public void startPrefixMapping(String prefix, String uri) throws SAXException {
            if (uri == null) {
                uri = "";
            }
            if (prefix == null || prefix.equals("")) {
                prefix = NamespaceExtractor.this.defaultBasePrefix;
            }
            try {
                if (NamespaceExtractor.this.mapping.hasPrefix(prefix)) {
                    int c;
                    Integer co = (Integer)NamespaceExtractor.this.basePrefixes.get(prefix);
                    if (co == null) {
                        NamespaceExtractor.this.basePrefixes.put(prefix, new Integer(1));
                        c = 1;
                    } else {
                        c = co + 1;
                        NamespaceExtractor.this.basePrefixes.put(prefix, new Integer(c));
                    }
                    prefix = prefix + "_" + c;
                }
                NamespaceExtractor.this.mapping.setMapping(prefix, uri);
            }
            catch (NamespaceException e) {
                String msg = e.getMessage();
                log.debug(msg);
            }
        }
    }
}

