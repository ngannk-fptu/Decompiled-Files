/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.xhtmlrenderer.resource.FSCatalog;
import org.xhtmlrenderer.util.GeneralUtil;
import org.xhtmlrenderer.util.XRLog;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.ext.EntityResolver2;

public class FSEntityResolver
implements EntityResolver2 {
    private static FSEntityResolver instance;
    private final Map entities = new HashMap();

    private FSEntityResolver() {
        FSCatalog catalog = new FSCatalog();
        this.entities.putAll(catalog.parseCatalog("resources/schema/html-4.01/catalog-html-4.01.xml"));
        this.entities.putAll(catalog.parseCatalog("resources/schema/xhtml/catalog-xhtml-common.xml"));
        this.entities.putAll(catalog.parseCatalog("resources/schema/xhtml/catalog-xhtml-1.0.xml"));
        this.entities.putAll(catalog.parseCatalog("resources/schema/xhtml/catalog-xhtml-1.1.xml"));
        this.entities.putAll(catalog.parseCatalog("resources/schema/docbook/catalog-docbook.xml"));
    }

    @Override
    public InputSource resolveEntity(String publicID, String systemID) throws SAXException {
        InputSource local = null;
        String url = (String)this.getEntities().get(publicID);
        if (url != null) {
            URL realUrl = GeneralUtil.getURLFromClasspath(this, url);
            InputStream is = null;
            try {
                is = realUrl.openStream();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            if (is == null) {
                XRLog.xmlEntities(Level.WARNING, "Can't find a local reference for Entity for public ID: " + publicID + " and expected to. The local URL should be: " + url + ". Not finding this probably means a CLASSPATH configuration problem; this resource should be included with the renderer and so not finding it means it is not on the CLASSPATH, and should be. Will let parser use the default in this case.");
            }
            local = new InputSource(is);
            local.setSystemId(realUrl.toExternalForm());
            XRLog.xmlEntities(Level.FINE, "Entity public: " + publicID + " -> " + url + (local == null ? ", NOT FOUND" : " (local)"));
        } else if ("about:legacy-compat".equals(systemID)) {
            local = FSEntityResolver.newHTML5DoctypeSource();
        } else {
            XRLog.xmlEntities("Entity public: " + publicID + ", no local mapping. Replacing with empty content.");
        }
        return local == null ? FSEntityResolver.newEmptySource() : local;
    }

    @Override
    public InputSource resolveEntity(String name, String publicId, String baseURI, String systemId) throws SAXException, IOException {
        return this.resolveEntity(publicId, systemId);
    }

    @Override
    public InputSource getExternalSubset(String name, String baseURI) throws SAXException, IOException {
        return name.equalsIgnoreCase("html") ? FSEntityResolver.newHTML5DoctypeSource() : null;
    }

    private static InputSource newHTML5DoctypeSource() {
        URL dtd = FSEntityResolver.class.getResource("/resources/schema/html5/entities.dtd");
        if (dtd == null) {
            throw new IllegalStateException("Could not find /resources/schema/html5/entities.dtd on the classpath");
        }
        return new InputSource(dtd.toExternalForm());
    }

    private static InputSource newEmptySource() {
        return new InputSource(new StringReader(""));
    }

    public static synchronized FSEntityResolver instance() {
        if (instance == null) {
            instance = new FSEntityResolver();
        }
        return instance;
    }

    public Map getEntities() {
        return new HashMap(this.entities);
    }
}

