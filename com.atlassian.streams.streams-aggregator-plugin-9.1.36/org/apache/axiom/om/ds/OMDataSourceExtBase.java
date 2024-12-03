/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.axiom.om.ds;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import org.apache.axiom.om.OMDataSourceExt;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.impl.MTOMXMLStreamWriter;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class OMDataSourceExtBase
implements OMDataSourceExt {
    private static final Log log = LogFactory.getLog(OMDataSourceExtBase.class);
    HashMap map = null;

    public Object getProperty(String key) {
        if (this.map == null) {
            return null;
        }
        return this.map.get(key);
    }

    public Object setProperty(String key, Object value) {
        if (this.map == null) {
            this.map = new HashMap();
        }
        return this.map.put(key, value);
    }

    public boolean hasProperty(String key) {
        if (this.map == null) {
            return false;
        }
        return this.map.containsKey(key);
    }

    public InputStream getXMLInputStream(String encoding) throws UnsupportedEncodingException {
        if (log.isDebugEnabled()) {
            log.debug((Object)("getXMLInputStream encoding=" + encoding));
        }
        return new ByteArrayInputStream(this.getXMLBytes(encoding));
    }

    public void serialize(OutputStream output, OMOutputFormat format) throws XMLStreamException {
        if (log.isDebugEnabled()) {
            log.debug((Object)("serialize output=" + output + " format=" + format));
        }
        try {
            output.write(this.getXMLBytes(format.getCharSetEncoding()));
        }
        catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }

    public void serialize(Writer writer, OMOutputFormat format) throws XMLStreamException {
        if (log.isDebugEnabled()) {
            log.debug((Object)("serialize writer=" + writer + " format=" + format));
        }
        try {
            String text = new String(this.getXMLBytes(format.getCharSetEncoding()));
            writer.write(text);
        }
        catch (UnsupportedEncodingException e) {
            throw new XMLStreamException(e);
        }
        catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }

    public void serialize(XMLStreamWriter xmlWriter) throws XMLStreamException {
        OutputStream os;
        if (log.isDebugEnabled()) {
            log.debug((Object)("serialize xmlWriter=" + xmlWriter));
        }
        if ((os = OMDataSourceExtBase.getOutputStream(xmlWriter)) != null) {
            if (log.isDebugEnabled()) {
                log.debug((Object)"serialize OutputStream optimisation: true");
            }
            String encoding = OMDataSourceExtBase.getCharacterEncoding(xmlWriter);
            OMOutputFormat format = new OMOutputFormat();
            format.setCharSetEncoding(encoding);
            this.serialize(os, format);
        } else {
            if (log.isDebugEnabled()) {
                log.debug((Object)"serialize OutputStream optimisation: false");
            }
            XMLStreamReader xmlReader = this.getReader();
            OMDataSourceExtBase.reader2writer(xmlReader, xmlWriter);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void reader2writer(XMLStreamReader reader, XMLStreamWriter writer) throws XMLStreamException {
        StAXOMBuilder builder = new StAXOMBuilder(reader);
        builder.releaseParserOnClose(true);
        try {
            OMDocument omDocument = builder.getDocument();
            Iterator it = omDocument.getChildren();
            while (it.hasNext()) {
                OMNode omNode = (OMNode)it.next();
                omNode.getNextOMSibling();
                omNode.serializeAndConsume(writer);
            }
        }
        finally {
            builder.close();
        }
    }

    private static OutputStream getOutputStream(XMLStreamWriter writer) throws XMLStreamException {
        if (writer instanceof MTOMXMLStreamWriter) {
            return ((MTOMXMLStreamWriter)writer).getOutputStream();
        }
        return null;
    }

    private static String getCharacterEncoding(XMLStreamWriter writer) {
        if (writer instanceof MTOMXMLStreamWriter) {
            return ((MTOMXMLStreamWriter)writer).getCharSetEncoding();
        }
        return null;
    }
}

