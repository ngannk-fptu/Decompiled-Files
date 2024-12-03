/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.resolver;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.configuration2.resolver.EntityRegistry;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class DefaultEntityResolver
implements EntityResolver,
EntityRegistry {
    private final Map<String, URL> registeredEntities = new HashMap<String, URL>();

    @Override
    public void registerEntityId(String publicId, URL entityURL) {
        if (publicId == null) {
            throw new IllegalArgumentException("Public ID must not be null!");
        }
        this.getRegisteredEntities().put(publicId, entityURL);
    }

    @Override
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException {
        URL entityURL = null;
        if (publicId != null) {
            entityURL = this.getRegisteredEntities().get(publicId);
        }
        if (entityURL != null) {
            try {
                URLConnection connection = entityURL.openConnection();
                connection.setUseCaches(false);
                InputStream stream = connection.getInputStream();
                InputSource source = new InputSource(stream);
                source.setSystemId(entityURL.toExternalForm());
                return source;
            }
            catch (IOException e) {
                throw new SAXException(e);
            }
        }
        return null;
    }

    @Override
    public Map<String, URL> getRegisteredEntities() {
        return this.registeredEntities;
    }
}

