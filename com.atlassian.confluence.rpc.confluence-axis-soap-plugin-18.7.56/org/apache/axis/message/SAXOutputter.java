/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.message;

import java.io.IOException;
import javax.xml.namespace.QName;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.encoding.SerializationContext;
import org.apache.commons.logging.Log;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;

public class SAXOutputter
extends DefaultHandler
implements LexicalHandler {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$message$SAXOutputter == null ? (class$org$apache$axis$message$SAXOutputter = SAXOutputter.class$("org.apache.axis.message.SAXOutputter")) : class$org$apache$axis$message$SAXOutputter).getName());
    SerializationContext context;
    boolean isCDATA = false;
    static /* synthetic */ Class class$org$apache$axis$message$SAXOutputter;

    public SAXOutputter(SerializationContext context) {
        this.context = context;
    }

    public void startDocument() throws SAXException {
        this.context.setSendDecl(true);
    }

    public void endDocument() throws SAXException {
        if (log.isDebugEnabled()) {
            log.debug((Object)"SAXOutputter.endDocument");
        }
    }

    public void startPrefixMapping(String p1, String p2) throws SAXException {
        this.context.registerPrefixForURI(p1, p2);
    }

    public void endPrefixMapping(String p1) throws SAXException {
    }

    public void characters(char[] p1, int p2, int p3) throws SAXException {
        if (log.isDebugEnabled()) {
            log.debug((Object)("SAXOutputter.characters ['" + new String(p1, p2, p3) + "']"));
        }
        try {
            if (!this.isCDATA) {
                this.context.writeChars(p1, p2, p3);
            } else {
                this.context.writeString(new String(p1, p2, p3));
            }
        }
        catch (IOException e) {
            throw new SAXException(e);
        }
    }

    public void ignorableWhitespace(char[] p1, int p2, int p3) throws SAXException {
        try {
            this.context.writeChars(p1, p2, p3);
        }
        catch (IOException e) {
            throw new SAXException(e);
        }
    }

    public void skippedEntity(String p1) throws SAXException {
    }

    public void startElement(String namespace, String localName, String qName, Attributes attributes) throws SAXException {
        if (log.isDebugEnabled()) {
            log.debug((Object)("SAXOutputter.startElement ['" + namespace + "' " + localName + "]"));
        }
        try {
            this.context.startElement(new QName(namespace, localName), attributes);
        }
        catch (IOException e) {
            throw new SAXException(e);
        }
    }

    public void endElement(String namespace, String localName, String qName) throws SAXException {
        if (log.isDebugEnabled()) {
            log.debug((Object)("SAXOutputter.endElement ['" + namespace + "' " + localName + "]"));
        }
        try {
            this.context.endElement();
        }
        catch (IOException e) {
            throw new SAXException(e);
        }
    }

    public void startDTD(String name, String publicId, String systemId) throws SAXException {
    }

    public void endDTD() throws SAXException {
    }

    public void startEntity(String name) throws SAXException {
    }

    public void endEntity(String name) throws SAXException {
    }

    public void startCDATA() throws SAXException {
        try {
            this.isCDATA = true;
            this.context.writeString("<![CDATA[");
        }
        catch (IOException e) {
            throw new SAXException(e);
        }
    }

    public void endCDATA() throws SAXException {
        try {
            this.isCDATA = false;
            this.context.writeString("]]>");
        }
        catch (IOException e) {
            throw new SAXException(e);
        }
    }

    public void comment(char[] ch, int start, int length) throws SAXException {
        if (log.isDebugEnabled()) {
            log.debug((Object)("SAXOutputter.comment ['" + new String(ch, start, length) + "']"));
        }
        try {
            this.context.writeString("<!--");
            this.context.writeChars(ch, start, length);
            this.context.writeString("-->");
        }
        catch (IOException e) {
            throw new SAXException(e);
        }
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

