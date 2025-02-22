/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.dom.util.DocumentFactory
 *  org.apache.batik.dom.util.SAXDocumentFactory
 *  org.apache.batik.util.XMLResourceDescriptor
 */
package org.apache.batik.transcoder;

import java.io.IOException;
import org.apache.batik.dom.util.DocumentFactory;
import org.apache.batik.dom.util.SAXDocumentFactory;
import org.apache.batik.transcoder.AbstractTranscoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.TranscodingHints;
import org.apache.batik.transcoder.keys.BooleanKey;
import org.apache.batik.transcoder.keys.DOMImplementationKey;
import org.apache.batik.transcoder.keys.StringKey;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

public abstract class XMLAbstractTranscoder
extends AbstractTranscoder {
    public static final TranscodingHints.Key KEY_XML_PARSER_CLASSNAME = new StringKey();
    public static final TranscodingHints.Key KEY_XML_PARSER_VALIDATING = new BooleanKey();
    public static final TranscodingHints.Key KEY_DOCUMENT_ELEMENT = new StringKey();
    public static final TranscodingHints.Key KEY_DOCUMENT_ELEMENT_NAMESPACE_URI = new StringKey();
    public static final TranscodingHints.Key KEY_DOM_IMPLEMENTATION = new DOMImplementationKey();

    protected XMLAbstractTranscoder() {
        this.hints.put(KEY_XML_PARSER_VALIDATING, Boolean.FALSE);
    }

    @Override
    public void transcode(TranscoderInput input, TranscoderOutput output) throws TranscoderException {
        Document document = null;
        String uri = input.getURI();
        if (input.getDocument() != null) {
            document = input.getDocument();
        } else {
            String parserClassname = (String)this.hints.get(KEY_XML_PARSER_CLASSNAME);
            String namespaceURI = (String)this.hints.get(KEY_DOCUMENT_ELEMENT_NAMESPACE_URI);
            String documentElement = (String)this.hints.get(KEY_DOCUMENT_ELEMENT);
            DOMImplementation domImpl = (DOMImplementation)this.hints.get(KEY_DOM_IMPLEMENTATION);
            if (parserClassname == null) {
                parserClassname = XMLResourceDescriptor.getXMLParserClassName();
            }
            if (domImpl == null) {
                this.handler.fatalError(new TranscoderException("Unspecified transcoding hints: KEY_DOM_IMPLEMENTATION"));
                return;
            }
            if (namespaceURI == null) {
                this.handler.fatalError(new TranscoderException("Unspecified transcoding hints: KEY_DOCUMENT_ELEMENT_NAMESPACE_URI"));
                return;
            }
            if (documentElement == null) {
                this.handler.fatalError(new TranscoderException("Unspecified transcoding hints: KEY_DOCUMENT_ELEMENT"));
                return;
            }
            DocumentFactory f = this.createDocumentFactory(domImpl, parserClassname);
            Object xmlParserValidating = this.hints.get(KEY_XML_PARSER_VALIDATING);
            boolean validating = xmlParserValidating != null && (Boolean)xmlParserValidating != false;
            f.setValidating(validating);
            try {
                if (input.getInputStream() != null) {
                    document = f.createDocument(namespaceURI, documentElement, input.getURI(), input.getInputStream());
                } else if (input.getReader() != null) {
                    document = f.createDocument(namespaceURI, documentElement, input.getURI(), input.getReader());
                } else if (input.getXMLReader() != null) {
                    document = f.createDocument(namespaceURI, documentElement, input.getURI(), input.getXMLReader());
                } else if (uri != null) {
                    document = f.createDocument(namespaceURI, documentElement, uri);
                }
            }
            catch (DOMException ex) {
                this.handler.fatalError(new TranscoderException(ex));
            }
            catch (IOException ex) {
                this.handler.fatalError(new TranscoderException(ex));
            }
        }
        if (document != null) {
            try {
                this.transcode(document, uri, output);
            }
            catch (TranscoderException ex) {
                this.handler.fatalError(ex);
                return;
            }
        }
    }

    protected DocumentFactory createDocumentFactory(DOMImplementation domImpl, String parserClassname) {
        return new SAXDocumentFactory(domImpl, parserClassname);
    }

    protected abstract void transcode(Document var1, String var2, TranscoderOutput var3) throws TranscoderException;
}

