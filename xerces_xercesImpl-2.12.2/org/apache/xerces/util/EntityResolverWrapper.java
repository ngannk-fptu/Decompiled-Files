/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLEntityResolver;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class EntityResolverWrapper
implements XMLEntityResolver {
    protected EntityResolver fEntityResolver;

    public EntityResolverWrapper() {
    }

    public EntityResolverWrapper(EntityResolver entityResolver) {
        this.setEntityResolver(entityResolver);
    }

    public void setEntityResolver(EntityResolver entityResolver) {
        this.fEntityResolver = entityResolver;
    }

    public EntityResolver getEntityResolver() {
        return this.fEntityResolver;
    }

    @Override
    public XMLInputSource resolveEntity(XMLResourceIdentifier xMLResourceIdentifier) throws XNIException, IOException {
        String string = xMLResourceIdentifier.getPublicId();
        String string2 = xMLResourceIdentifier.getExpandedSystemId();
        if (string == null && string2 == null) {
            return null;
        }
        if (this.fEntityResolver != null && xMLResourceIdentifier != null) {
            try {
                InputSource inputSource = this.fEntityResolver.resolveEntity(string, string2);
                if (inputSource != null) {
                    String string3 = inputSource.getPublicId();
                    String string4 = inputSource.getSystemId();
                    String string5 = xMLResourceIdentifier.getBaseSystemId();
                    InputStream inputStream = inputSource.getByteStream();
                    Reader reader = inputSource.getCharacterStream();
                    String string6 = inputSource.getEncoding();
                    XMLInputSource xMLInputSource = new XMLInputSource(string3, string4, string5);
                    xMLInputSource.setByteStream(inputStream);
                    xMLInputSource.setCharacterStream(reader);
                    xMLInputSource.setEncoding(string6);
                    return xMLInputSource;
                }
            }
            catch (SAXException sAXException) {
                Exception exception = sAXException.getException();
                if (exception == null) {
                    exception = sAXException;
                }
                throw new XNIException(exception);
            }
        }
        return null;
    }
}

