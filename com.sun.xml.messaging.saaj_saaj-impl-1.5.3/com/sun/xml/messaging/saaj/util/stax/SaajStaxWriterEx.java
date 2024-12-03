/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataHandler
 *  javax.xml.bind.attachment.AttachmentMarshaller
 *  javax.xml.soap.SOAPException
 *  javax.xml.soap.SOAPMessage
 *  org.jvnet.staxex.Base64Data
 *  org.jvnet.staxex.BinaryText
 *  org.jvnet.staxex.MtomEnabled
 *  org.jvnet.staxex.NamespaceContextEx
 *  org.jvnet.staxex.NamespaceContextEx$Binding
 *  org.jvnet.staxex.StreamingDataHandler
 *  org.jvnet.staxex.XMLStreamWriterEx
 *  org.jvnet.staxex.util.MtomStreamWriter
 */
package com.sun.xml.messaging.saaj.util.stax;

import com.sun.xml.messaging.saaj.util.stax.SaajStaxWriter;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.UUID;
import javax.activation.DataHandler;
import javax.xml.bind.attachment.AttachmentMarshaller;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.stream.XMLStreamException;
import org.jvnet.staxex.Base64Data;
import org.jvnet.staxex.BinaryText;
import org.jvnet.staxex.MtomEnabled;
import org.jvnet.staxex.NamespaceContextEx;
import org.jvnet.staxex.StreamingDataHandler;
import org.jvnet.staxex.XMLStreamWriterEx;
import org.jvnet.staxex.util.MtomStreamWriter;

public class SaajStaxWriterEx
extends SaajStaxWriter
implements XMLStreamWriterEx,
MtomStreamWriter {
    protected static final String xopNS = "http://www.w3.org/2004/08/xop/include";
    protected static final String Include = "Include";
    protected static final String href = "href";
    private State state = State.others;
    private BinaryText binaryText;

    public SaajStaxWriterEx(SOAPMessage msg, String uri) throws SOAPException {
        super(msg, uri);
    }

    @Override
    public void writeStartElement(String prefix, String ln, String ns) throws XMLStreamException {
        if (xopNS.equals(ns) && Include.equals(ln)) {
            this.state = State.xopInclude;
            return;
        }
        super.writeStartElement(prefix, ln, ns);
    }

    @Override
    public void writeEndElement() throws XMLStreamException {
        if (this.state.equals((Object)State.xopInclude)) {
            this.state = State.others;
        } else {
            super.writeEndElement();
        }
    }

    @Override
    public void writeAttribute(String prefix, String ns, String ln, String value) throws XMLStreamException {
        if (this.binaryText != null && href.equals(ln)) {
            return;
        }
        super.writeAttribute(prefix, ns, ln, value);
    }

    public NamespaceContextEx getNamespaceContext() {
        return new NamespaceContextEx(){

            public String getNamespaceURI(String prefix) {
                return SaajStaxWriterEx.this.currentElement.getNamespaceURI(prefix);
            }

            public String getPrefix(String namespaceURI) {
                return SaajStaxWriterEx.this.currentElement.lookupPrefix(namespaceURI);
            }

            public Iterator getPrefixes(final String namespaceURI) {
                return new Iterator<String>(){
                    String prefix;
                    {
                        this.prefix = this.getPrefix(namespaceURI);
                    }

                    @Override
                    public boolean hasNext() {
                        return this.prefix != null;
                    }

                    @Override
                    public String next() {
                        if (this.prefix == null) {
                            throw new NoSuchElementException();
                        }
                        String next = this.prefix;
                        this.prefix = null;
                        return next;
                    }

                    @Override
                    public void remove() {
                    }
                };
            }

            public Iterator<NamespaceContextEx.Binding> iterator() {
                return new Iterator<NamespaceContextEx.Binding>(){

                    @Override
                    public boolean hasNext() {
                        return false;
                    }

                    @Override
                    public NamespaceContextEx.Binding next() {
                        return null;
                    }

                    @Override
                    public void remove() {
                    }
                };
            }
        };
    }

    public void writeBinary(DataHandler data) throws XMLStreamException {
        this.addBinaryText(data);
    }

    public OutputStream writeBinary(String arg0) throws XMLStreamException {
        return null;
    }

    public void writeBinary(byte[] data, int offset, int length, String contentType) throws XMLStreamException {
        byte[] bytes;
        byte[] byArray = bytes = offset == 0 && length == data.length ? data : Arrays.copyOfRange(data, offset, offset + length);
        if (!(this.currentElement instanceof MtomEnabled)) {
            throw new IllegalStateException("The currentElement is not MtomEnabled " + this.currentElement);
        }
        this.binaryText = ((MtomEnabled)this.currentElement).addBinaryText(bytes);
    }

    public void writePCDATA(CharSequence arg0) throws XMLStreamException {
        if (arg0 instanceof Base64Data) {
            this.addBinaryText(((Base64Data)arg0).getDataHandler());
        } else {
            try {
                this.currentElement.addTextNode(arg0.toString());
            }
            catch (SOAPException e) {
                throw new XMLStreamException("Cannot add Text node", e);
            }
        }
    }

    private static String encodeCid() {
        String cid = "example.jaxws.sun.com";
        String name = UUID.randomUUID() + "@";
        return name + cid;
    }

    private String addBinaryText(DataHandler data) {
        String prefixedCid;
        String hrefOrCid = null;
        if (data instanceof StreamingDataHandler) {
            hrefOrCid = ((StreamingDataHandler)data).getHrefCid();
        }
        if (hrefOrCid == null) {
            hrefOrCid = SaajStaxWriterEx.encodeCid();
        }
        String string = prefixedCid = hrefOrCid.startsWith("cid:") ? hrefOrCid : "cid:" + hrefOrCid;
        if (!(this.currentElement instanceof MtomEnabled)) {
            throw new IllegalStateException("The currentElement is not MtomEnabled " + this.currentElement);
        }
        this.binaryText = ((MtomEnabled)this.currentElement).addBinaryText(prefixedCid, data);
        return hrefOrCid;
    }

    public AttachmentMarshaller getAttachmentMarshaller() {
        return new AttachmentMarshaller(){

            public String addMtomAttachment(DataHandler data, String ns, String ln) {
                String hrefOrCid = SaajStaxWriterEx.this.addBinaryText(data);
                return hrefOrCid;
            }

            public String addMtomAttachment(byte[] data, int offset, int length, String mimeType, String ns, String ln) {
                byte[] bytes;
                byte[] byArray = bytes = offset == 0 && length == data.length ? data : Arrays.copyOfRange(data, offset, offset + length);
                if (!(SaajStaxWriterEx.this.currentElement instanceof MtomEnabled)) {
                    throw new IllegalStateException("The currentElement is not MtomEnabled " + SaajStaxWriterEx.this.currentElement);
                }
                SaajStaxWriterEx.this.binaryText = ((MtomEnabled)SaajStaxWriterEx.this.currentElement).addBinaryText(bytes);
                return SaajStaxWriterEx.this.binaryText.getHref();
            }

            public String addSwaRefAttachment(DataHandler data) {
                return "cid:" + SaajStaxWriterEx.encodeCid();
            }

            public boolean isXOPPackage() {
                return true;
            }
        };
    }

    private static enum State {
        xopInclude,
        others;

    }
}

