/*
 * Decompiled with CFR 0.152.
 */
package org.tuckey.web.filters.urlrewrite;

import java.util.Hashtable;
import java.util.regex.Pattern;
import org.tuckey.web.filters.urlrewrite.utils.Log;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class ConfHandler
extends DefaultHandler {
    private static Log log = Log.getLog(ConfHandler.class);
    private static final Pattern HAS_PROTOCOL = Pattern.compile("^\\w+:");
    private String confSystemId;
    private static Hashtable dtdPaths = new Hashtable();

    public ConfHandler(String confSystemId) {
        this.confSystemId = confSystemId;
    }

    public InputSource resolveEntity(String publicId, String systemId) throws SAXException {
        if (publicId == null) {
            if (log.isDebugEnabled()) {
                log.debug("Couldn't resolve entity with no publicId, system id is " + systemId);
            }
            if (this.confSystemId != null && !ConfHandler.hasProtocol(systemId)) {
                return new InputSource(this.confSystemId.substring(0, this.confSystemId.lastIndexOf(47)) + "/" + systemId);
            }
            return new InputSource(systemId);
        }
        String entity = (String)dtdPaths.get(publicId);
        if (entity == null) {
            if (log.isDebugEnabled()) {
                log.debug("Couldn't resolve DTD: " + publicId + ", " + systemId);
            }
            return null;
        }
        if (log.isDebugEnabled()) {
            log.debug("Resolving to DTD " + entity);
        }
        return new InputSource(ConfHandler.class.getResourceAsStream(entity));
    }

    private static boolean hasProtocol(String systemId) {
        return systemId != null && HAS_PROTOCOL.matcher(systemId).find();
    }

    public void warning(SAXParseException ex) {
        log.debug("error: " + ex.getMessage());
    }

    public void error(SAXParseException ex) {
        log.debug("error: " + ex.getMessage());
    }

    public void fatalError(SAXParseException ex) throws SAXException {
        log.debug("error: " + ex.getMessage());
    }

    static {
        dtdPaths.put("-//tuckey.org//DTD UrlRewrite 1.0//EN", "/org/tuckey/web/filters/urlrewrite/dtds/urlrewrite1.0.dtd");
        dtdPaths.put("-//tuckey.org//DTD UrlRewrite 2.0//EN", "/org/tuckey/web/filters/urlrewrite/dtds/urlrewrite2.0.dtd");
        dtdPaths.put("-//tuckey.org//DTD UrlRewrite 2.3//EN", "/org/tuckey/web/filters/urlrewrite/dtds/urlrewrite2.3.dtd");
        dtdPaths.put("-//tuckey.org//DTD UrlRewrite 2.4//EN", "/org/tuckey/web/filters/urlrewrite/dtds/urlrewrite2.4.dtd");
        dtdPaths.put("-//tuckey.org//DTD UrlRewrite 2.5//EN", "/org/tuckey/web/filters/urlrewrite/dtds/urlrewrite2.5.dtd");
        dtdPaths.put("-//tuckey.org//DTD UrlRewrite 2.6//EN", "/org/tuckey/web/filters/urlrewrite/dtds/urlrewrite2.6.dtd");
        dtdPaths.put("-//tuckey.org//DTD UrlRewrite 3.0//EN", "/org/tuckey/web/filters/urlrewrite/dtds/urlrewrite3.0.dtd");
        dtdPaths.put("-//tuckey.org//DTD UrlRewrite 3.1//EN", "/org/tuckey/web/filters/urlrewrite/dtds/urlrewrite3.1.dtd");
        dtdPaths.put("-//tuckey.org//DTD UrlRewrite 3.2//EN", "/org/tuckey/web/filters/urlrewrite/dtds/urlrewrite3.2.dtd");
        dtdPaths.put("-//tuckey.org//DTD UrlRewrite 3.3//EN", "/org/tuckey/web/filters/urlrewrite/dtds/urlrewrite3.3.dtd");
        dtdPaths.put("-//tuckey.org//DTD UrlRewrite 4.0//EN", "/org/tuckey/web/filters/urlrewrite/dtds/urlrewrite4.0.dtd");
    }
}

