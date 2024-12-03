/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.buf.UriUtil
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.tomcat.util.descriptor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import org.apache.tomcat.util.buf.UriUtil;
import org.apache.tomcat.util.descriptor.Constants;
import org.apache.tomcat.util.res.StringManager;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.ext.EntityResolver2;

public class LocalResolver
implements EntityResolver2 {
    private static final StringManager sm = StringManager.getManager((String)Constants.PACKAGE_NAME);
    private static final String[] JAVA_EE_NAMESPACES = new String[]{"http://java.sun.com/xml/ns/j2ee", "http://java.sun.com/xml/ns/javaee", "http://xmlns.jcp.org/xml/ns/javaee"};
    private final Map<String, String> publicIds;
    private final Map<String, String> systemIds;
    private final boolean blockExternal;

    public LocalResolver(Map<String, String> publicIds, Map<String, String> systemIds, boolean blockExternal) {
        this.publicIds = publicIds;
        this.systemIds = systemIds;
        this.blockExternal = blockExternal;
    }

    @Override
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
        return this.resolveEntity(null, publicId, null, systemId);
    }

    @Override
    public InputSource resolveEntity(String name, String publicId, String base, String systemId) throws SAXException, IOException {
        URI systemUri;
        String resolved = this.publicIds.get(publicId);
        if (resolved != null) {
            InputSource is = new InputSource(resolved);
            is.setPublicId(publicId);
            return is;
        }
        if (systemId == null) {
            throw new FileNotFoundException(sm.getString("localResolver.unresolvedEntity", new Object[]{name, publicId, null, base}));
        }
        resolved = this.systemIds.get(systemId);
        if (resolved != null) {
            InputSource is = new InputSource(resolved);
            is.setPublicId(publicId);
            return is;
        }
        for (String javaEENamespace : JAVA_EE_NAMESPACES) {
            String javaEESystemId = javaEENamespace + '/' + systemId;
            resolved = this.systemIds.get(javaEESystemId);
            if (resolved == null) continue;
            InputSource is = new InputSource(resolved);
            is.setPublicId(publicId);
            return is;
        }
        try {
            if (base == null) {
                systemUri = new URI(systemId);
            } else {
                URI baseUri = new URI(base);
                systemUri = UriUtil.resolve((URI)baseUri, (String)systemId);
            }
            systemUri = systemUri.normalize();
        }
        catch (URISyntaxException e) {
            if (this.blockExternal) {
                throw new MalformedURLException(e.getMessage());
            }
            return new InputSource(systemId);
        }
        if (systemUri.isAbsolute()) {
            resolved = this.systemIds.get(systemUri.toString());
            if (resolved != null) {
                InputSource is = new InputSource(resolved);
                is.setPublicId(publicId);
                return is;
            }
            if (!this.blockExternal) {
                InputSource is = new InputSource(systemUri.toString());
                is.setPublicId(publicId);
                return is;
            }
        }
        throw new FileNotFoundException(sm.getString("localResolver.unresolvedEntity", new Object[]{name, publicId, systemId, base}));
    }

    @Override
    public InputSource getExternalSubset(String name, String baseURI) throws SAXException, IOException {
        return null;
    }
}

