/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.springframework.beans.factory.xml;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.lang.Nullable;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

public class BeansDtdResolver
implements EntityResolver {
    private static final String DTD_EXTENSION = ".dtd";
    private static final String DTD_NAME = "spring-beans";
    private static final Log logger = LogFactory.getLog(BeansDtdResolver.class);

    @Override
    @Nullable
    public InputSource resolveEntity(@Nullable String publicId, @Nullable String systemId) throws IOException {
        block6: {
            int lastPathSeparator;
            int dtdNameStart;
            if (logger.isTraceEnabled()) {
                logger.trace((Object)("Trying to resolve XML entity with public ID [" + publicId + "] and system ID [" + systemId + "]"));
            }
            if (systemId != null && systemId.endsWith(DTD_EXTENSION) && (dtdNameStart = systemId.indexOf(DTD_NAME, lastPathSeparator = systemId.lastIndexOf(47))) != -1) {
                String dtdFile = "spring-beans.dtd";
                if (logger.isTraceEnabled()) {
                    logger.trace((Object)("Trying to locate [" + dtdFile + "] in Spring jar on classpath"));
                }
                try {
                    ClassPathResource resource = new ClassPathResource(dtdFile, this.getClass());
                    InputSource source = new InputSource(resource.getInputStream());
                    source.setPublicId(publicId);
                    source.setSystemId(systemId);
                    if (logger.isTraceEnabled()) {
                        logger.trace((Object)("Found beans DTD [" + systemId + "] in classpath: " + dtdFile));
                    }
                    return source;
                }
                catch (FileNotFoundException ex) {
                    if (!logger.isDebugEnabled()) break block6;
                    logger.debug((Object)("Could not resolve beans DTD [" + systemId + "]: not found in classpath"), (Throwable)ex);
                }
            }
        }
        return null;
    }

    public String toString() {
        return "EntityResolver for spring-beans DTD";
    }
}

