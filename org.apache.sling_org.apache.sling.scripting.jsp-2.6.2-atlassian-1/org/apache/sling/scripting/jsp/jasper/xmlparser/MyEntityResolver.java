/*
 * Decompiled with CFR 0.152.
 */
package org.apache.sling.scripting.jsp.jasper.xmlparser;

import java.io.InputStream;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.sling.scripting.jsp.jasper.Constants;
import org.apache.sling.scripting.jsp.jasper.compiler.Localizer;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

class MyEntityResolver
implements EntityResolver {
    private Log log = LogFactory.getLog(MyEntityResolver.class);

    MyEntityResolver() {
    }

    @Override
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException {
        for (int i = 0; i < Constants.CACHED_DTD_PUBLIC_IDS.length; ++i) {
            String cachedDtdPublicId = Constants.CACHED_DTD_PUBLIC_IDS[i];
            if (!cachedDtdPublicId.equals(publicId)) continue;
            String resourcePath = Constants.CACHED_DTD_RESOURCE_PATHS[i];
            InputStream input = this.getClass().getResourceAsStream(resourcePath);
            if (input == null) {
                throw new SAXException(Localizer.getMessage("jsp.error.internal.filenotfound", resourcePath));
            }
            InputSource isrc = new InputSource(input);
            return isrc;
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug("Resolve entity failed" + publicId + " " + systemId);
        }
        this.log.error(Localizer.getMessage("jsp.error.parse.xml.invalidPublicId", publicId));
        return null;
    }
}

