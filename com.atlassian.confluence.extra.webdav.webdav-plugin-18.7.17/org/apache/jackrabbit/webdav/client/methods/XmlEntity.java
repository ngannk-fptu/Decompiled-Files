/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.HttpEntity
 *  org.apache.http.entity.ByteArrayEntity
 *  org.apache.http.entity.ContentType
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav.client.methods;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.apache.jackrabbit.webdav.xml.XmlSerializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class XmlEntity {
    private static Logger LOG = LoggerFactory.getLogger(XmlEntity.class);
    private static ContentType CT = ContentType.create((String)"application/xml", (String)"UTF-8");

    public static HttpEntity create(Document doc) throws IOException {
        try {
            ByteArrayOutputStream xml = new ByteArrayOutputStream();
            DomUtil.transformDocument(doc, xml);
            return new ByteArrayEntity(xml.toByteArray(), CT);
        }
        catch (TransformerException ex) {
            LOG.error(ex.getMessage());
            throw new IOException(ex);
        }
        catch (SAXException ex) {
            LOG.error(ex.getMessage());
            throw new IOException(ex);
        }
    }

    public static HttpEntity create(XmlSerializable payload) throws IOException {
        try {
            Document doc = DomUtil.createDocument();
            doc.appendChild(payload.toXml(doc));
            return XmlEntity.create(doc);
        }
        catch (ParserConfigurationException ex) {
            LOG.error(ex.getMessage());
            throw new IOException(ex);
        }
    }
}

