/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.message.source;

import com.sun.xml.ws.message.RootElementSniffer;
import com.sun.xml.ws.streaming.SourceReaderFactory;
import com.sun.xml.ws.util.xml.XmlUtil;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.WebServiceException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

final class SourceUtils {
    int srcType;
    private static final int domSource = 1;
    private static final int streamSource = 2;
    private static final int saxSource = 4;

    public SourceUtils(Source src) {
        if (src instanceof StreamSource) {
            this.srcType = 2;
        } else if (src instanceof DOMSource) {
            this.srcType = 1;
        } else if (src instanceof SAXSource) {
            this.srcType = 4;
        }
    }

    public boolean isDOMSource() {
        return (this.srcType & 1) == 1;
    }

    public boolean isStreamSource() {
        return (this.srcType & 2) == 2;
    }

    public boolean isSaxSource() {
        return (this.srcType & 4) == 4;
    }

    public QName sniff(Source src) {
        return this.sniff(src, new RootElementSniffer());
    }

    public QName sniff(Source src, RootElementSniffer sniffer) {
        String localName = null;
        String namespaceUri = null;
        if (this.isDOMSource()) {
            DOMSource domSrc = (DOMSource)src;
            Node n = domSrc.getNode();
            if (n.getNodeType() == 9) {
                n = ((Document)n).getDocumentElement();
            }
            localName = n.getLocalName();
            namespaceUri = n.getNamespaceURI();
        } else if (this.isSaxSource()) {
            SAXSource saxSrc = (SAXSource)src;
            SAXResult saxResult = new SAXResult(sniffer);
            try {
                Transformer tr = XmlUtil.newTransformer();
                tr.transform(saxSrc, saxResult);
            }
            catch (TransformerConfigurationException e) {
                throw new WebServiceException((Throwable)e);
            }
            catch (TransformerException e) {
                localName = sniffer.getLocalName();
                namespaceUri = sniffer.getNsUri();
            }
        }
        return new QName(namespaceUri, localName);
    }

    public static void serializeSource(Source src, XMLStreamWriter writer) throws XMLStreamException {
        int state;
        XMLStreamReader reader = SourceReaderFactory.createSourceReader(src, true);
        block5: do {
            state = reader.next();
            switch (state) {
                case 1: {
                    int i;
                    String uri = reader.getNamespaceURI();
                    String prefix = reader.getPrefix();
                    String localName = reader.getLocalName();
                    if (prefix == null) {
                        if (uri == null) {
                            writer.writeStartElement(localName);
                        } else {
                            writer.writeStartElement(uri, localName);
                        }
                    } else if (prefix.length() > 0) {
                        String writerPrefix;
                        String writerURI = null;
                        if (writer.getNamespaceContext() != null) {
                            writerURI = writer.getNamespaceContext().getNamespaceURI(prefix);
                        }
                        if (SourceUtils.declarePrefix(prefix, uri, writerPrefix = writer.getPrefix(uri), writerURI)) {
                            writer.writeStartElement(prefix, localName, uri);
                            writer.setPrefix(prefix, uri != null ? uri : "");
                            writer.writeNamespace(prefix, uri);
                        } else {
                            writer.writeStartElement(prefix, localName, uri);
                        }
                    } else {
                        writer.writeStartElement(prefix, localName, uri);
                    }
                    int n = reader.getNamespaceCount();
                    for (i = 0; i < n; ++i) {
                        String nsPrefix = reader.getNamespacePrefix(i);
                        if (nsPrefix == null) {
                            nsPrefix = "";
                        }
                        String writerURI = null;
                        if (writer.getNamespaceContext() != null) {
                            writerURI = writer.getNamespaceContext().getNamespaceURI(nsPrefix);
                        }
                        String readerURI = reader.getNamespaceURI(i);
                        if (writerURI != null && nsPrefix.length() != 0 && prefix.length() != 0 && (nsPrefix.equals(prefix) || writerURI.equals(readerURI))) continue;
                        writer.setPrefix(nsPrefix, readerURI != null ? readerURI : "");
                        writer.writeNamespace(nsPrefix, readerURI != null ? readerURI : "");
                    }
                    n = reader.getAttributeCount();
                    for (i = 0; i < n; ++i) {
                        String attrPrefix = reader.getAttributePrefix(i);
                        String attrURI = reader.getAttributeNamespace(i);
                        writer.writeAttribute(attrPrefix != null ? attrPrefix : "", attrURI != null ? attrURI : "", reader.getAttributeLocalName(i), reader.getAttributeValue(i));
                        SourceUtils.setUndeclaredPrefix(attrPrefix, attrURI, writer);
                    }
                    continue block5;
                }
                case 2: {
                    writer.writeEndElement();
                    break;
                }
                case 4: {
                    writer.writeCharacters(reader.getText());
                    break;
                }
            }
        } while (state != 8);
        reader.close();
    }

    private static void setUndeclaredPrefix(String prefix, String readerURI, XMLStreamWriter writer) throws XMLStreamException {
        String writerURI = null;
        if (writer.getNamespaceContext() != null) {
            writerURI = writer.getNamespaceContext().getNamespaceURI(prefix);
        }
        if (writerURI == null) {
            writer.setPrefix(prefix, readerURI != null ? readerURI : "");
            writer.writeNamespace(prefix, readerURI != null ? readerURI : "");
        }
    }

    private static boolean declarePrefix(String rPrefix, String rUri, String wPrefix, String wUri) {
        return wUri == null || wPrefix != null && !rPrefix.equals(wPrefix) || rUri != null && !wUri.equals(rUri);
    }
}

