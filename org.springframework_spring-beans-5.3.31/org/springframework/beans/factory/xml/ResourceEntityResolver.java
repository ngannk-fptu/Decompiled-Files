/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.core.io.Resource
 *  org.springframework.core.io.ResourceLoader
 *  org.springframework.lang.Nullable
 */
package org.springframework.beans.factory.xml;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.xml.DelegatingEntityResolver;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.lang.Nullable;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class ResourceEntityResolver
extends DelegatingEntityResolver {
    private static final Log logger = LogFactory.getLog(ResourceEntityResolver.class);
    private final ResourceLoader resourceLoader;

    public ResourceEntityResolver(ResourceLoader resourceLoader) {
        super(resourceLoader.getClassLoader());
        this.resourceLoader = resourceLoader;
    }

    @Override
    @Nullable
    public InputSource resolveEntity(@Nullable String publicId, @Nullable String systemId) throws SAXException, IOException {
        InputSource source = super.resolveEntity(publicId, systemId);
        if (source == null && systemId != null) {
            String resourcePath = null;
            try {
                String decodedSystemId = URLDecoder.decode(systemId, "UTF-8");
                String givenUrl = new URL(decodedSystemId).toString();
                String systemRootUrl = new File("").toURI().toURL().toString();
                if (givenUrl.startsWith(systemRootUrl)) {
                    resourcePath = givenUrl.substring(systemRootUrl.length());
                }
            }
            catch (Exception ex) {
                if (logger.isDebugEnabled()) {
                    logger.debug((Object)("Could not resolve XML entity [" + systemId + "] against system root URL"), (Throwable)ex);
                }
                resourcePath = systemId;
            }
            if (resourcePath != null) {
                if (logger.isTraceEnabled()) {
                    logger.trace((Object)("Trying to locate XML entity [" + systemId + "] as resource [" + resourcePath + "]"));
                }
                Resource resource = this.resourceLoader.getResource(resourcePath);
                source = new InputSource(resource.getInputStream());
                source.setPublicId(publicId);
                source.setSystemId(systemId);
                if (logger.isDebugEnabled()) {
                    logger.debug((Object)("Found XML entity [" + systemId + "]: " + resource));
                }
            } else if (systemId.endsWith(".dtd") || systemId.endsWith(".xsd")) {
                String url = systemId;
                if (url.startsWith("http:")) {
                    url = "https:" + url.substring(5);
                }
                try {
                    source = new InputSource(new URL(url).openStream());
                    source.setPublicId(publicId);
                    source.setSystemId(systemId);
                }
                catch (IOException ex) {
                    if (logger.isDebugEnabled()) {
                        logger.debug((Object)("Could not resolve XML entity [" + systemId + "] through URL [" + url + "]"), (Throwable)ex);
                    }
                    source = null;
                }
            }
        }
        return source;
    }
}

