/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import org.apache.xerces.impl.ExternalSubsetResolver;
import org.apache.xerces.impl.XMLEntityDescription;
import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.grammars.XMLDTDDescription;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.ext.EntityResolver2;

public class EntityResolver2Wrapper
implements ExternalSubsetResolver {
    protected EntityResolver2 fEntityResolver;

    public EntityResolver2Wrapper() {
    }

    public EntityResolver2Wrapper(EntityResolver2 entityResolver2) {
        this.setEntityResolver(entityResolver2);
    }

    public void setEntityResolver(EntityResolver2 entityResolver2) {
        this.fEntityResolver = entityResolver2;
    }

    public EntityResolver2 getEntityResolver() {
        return this.fEntityResolver;
    }

    @Override
    public XMLInputSource getExternalSubset(XMLDTDDescription xMLDTDDescription) throws XNIException, IOException {
        if (this.fEntityResolver != null) {
            String string = xMLDTDDescription.getRootName();
            String string2 = xMLDTDDescription.getBaseSystemId();
            try {
                InputSource inputSource = this.fEntityResolver.getExternalSubset(string, string2);
                return inputSource != null ? this.createXMLInputSource(inputSource, string2) : null;
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

    @Override
    public XMLInputSource resolveEntity(XMLResourceIdentifier xMLResourceIdentifier) throws XNIException, IOException {
        if (this.fEntityResolver != null) {
            String string = xMLResourceIdentifier.getPublicId();
            String string2 = xMLResourceIdentifier.getLiteralSystemId();
            String string3 = xMLResourceIdentifier.getBaseSystemId();
            String string4 = null;
            if (xMLResourceIdentifier instanceof XMLDTDDescription) {
                string4 = "[dtd]";
            } else if (xMLResourceIdentifier instanceof XMLEntityDescription) {
                string4 = ((XMLEntityDescription)xMLResourceIdentifier).getEntityName();
            }
            if (string == null && string2 == null) {
                return null;
            }
            try {
                InputSource inputSource = this.fEntityResolver.resolveEntity(string4, string, string3, string2);
                return inputSource != null ? this.createXMLInputSource(inputSource, string3) : null;
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

    private XMLInputSource createXMLInputSource(InputSource inputSource, String string) {
        String string2 = inputSource.getPublicId();
        String string3 = inputSource.getSystemId();
        String string4 = string;
        InputStream inputStream = inputSource.getByteStream();
        Reader reader = inputSource.getCharacterStream();
        String string5 = inputSource.getEncoding();
        XMLInputSource xMLInputSource = new XMLInputSource(string2, string3, string4);
        xMLInputSource.setByteStream(inputStream);
        xMLInputSource.setCharacterStream(reader);
        xMLInputSource.setEncoding(string5);
        return xMLInputSource;
    }
}

