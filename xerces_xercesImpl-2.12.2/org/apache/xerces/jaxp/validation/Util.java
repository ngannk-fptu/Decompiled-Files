/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.jaxp.validation;

import javax.xml.transform.stream.StreamSource;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.apache.xerces.xni.parser.XMLParseException;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

final class Util {
    Util() {
    }

    public static final XMLInputSource toXMLInputSource(StreamSource streamSource) {
        if (streamSource.getReader() != null) {
            return new XMLInputSource(streamSource.getPublicId(), streamSource.getSystemId(), streamSource.getSystemId(), streamSource.getReader(), null);
        }
        if (streamSource.getInputStream() != null) {
            return new XMLInputSource(streamSource.getPublicId(), streamSource.getSystemId(), streamSource.getSystemId(), streamSource.getInputStream(), null);
        }
        return new XMLInputSource(streamSource.getPublicId(), streamSource.getSystemId(), streamSource.getSystemId());
    }

    public static SAXException toSAXException(XNIException xNIException) {
        if (xNIException instanceof XMLParseException) {
            return Util.toSAXParseException((XMLParseException)xNIException);
        }
        if (xNIException.getException() instanceof SAXException) {
            return (SAXException)xNIException.getException();
        }
        return new SAXException(xNIException.getMessage(), xNIException.getException());
    }

    public static SAXParseException toSAXParseException(XMLParseException xMLParseException) {
        if (xMLParseException.getException() instanceof SAXParseException) {
            return (SAXParseException)xMLParseException.getException();
        }
        return new SAXParseException(xMLParseException.getMessage(), xMLParseException.getPublicId(), xMLParseException.getExpandedSystemId(), xMLParseException.getLineNumber(), xMLParseException.getColumnNumber(), xMLParseException.getException());
    }
}

