/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.security.xml.SecureXmlParserFactory
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.calendar3.caldav.servlet;

import com.atlassian.security.xml.SecureXmlParserFactory;
import java.io.Reader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.bedework.webdav.servlet.shared.WebdavBadRequest;
import org.bedework.webdav.servlet.shared.WebdavException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public interface SecureXmlMethod {
    public static final Logger log = LoggerFactory.getLogger(SecureXmlMethod.class);

    default public Document parseContentSafe(int contentLength, Reader reader) throws WebdavException {
        if (contentLength == 0) {
            return null;
        }
        if (reader == null) {
            return null;
        }
        try {
            DocumentBuilderFactory factory = SecureXmlParserFactory.newDocumentBuilderFactory();
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(new InputSource(reader));
        }
        catch (SAXException exception) {
            log.debug(exception.getMessage(), (Throwable)exception);
            throw new WebdavBadRequest(exception.getMessage());
        }
        catch (Throwable exception) {
            log.debug(exception.getMessage(), exception);
            throw new WebdavException(exception);
        }
    }
}

