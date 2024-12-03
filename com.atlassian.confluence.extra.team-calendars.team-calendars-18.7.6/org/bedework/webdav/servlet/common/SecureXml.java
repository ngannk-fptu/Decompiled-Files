/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.webdav.servlet.common;

import java.io.Reader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.bedework.webdav.servlet.shared.WebdavBadRequest;
import org.bedework.webdav.servlet.shared.WebdavException;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public interface SecureXml {
    default public Document parseXmlSafely(int contentLength, Reader reader) throws WebdavException {
        if (contentLength == 0) {
            return null;
        }
        if (reader == null) {
            return null;
        }
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(false);
            factory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            factory.setAttribute("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(new InputSource(reader));
        }
        catch (SAXException exception) {
            throw new WebdavBadRequest(exception.getMessage());
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }
}

