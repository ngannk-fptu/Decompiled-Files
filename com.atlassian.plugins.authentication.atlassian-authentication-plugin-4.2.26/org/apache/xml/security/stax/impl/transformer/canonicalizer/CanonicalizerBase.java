/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.stax.impl.transformer.canonicalizer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import org.apache.xml.security.c14n.implementations.UtfHelpper;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.ext.Transformer;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.stax.ext.XMLSecurityUtils;
import org.apache.xml.security.stax.ext.stax.XMLSecAttribute;
import org.apache.xml.security.stax.ext.stax.XMLSecComment;
import org.apache.xml.security.stax.ext.stax.XMLSecEndElement;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;
import org.apache.xml.security.stax.ext.stax.XMLSecEventFactory;
import org.apache.xml.security.stax.ext.stax.XMLSecNamespace;
import org.apache.xml.security.stax.ext.stax.XMLSecProcessingInstruction;
import org.apache.xml.security.stax.ext.stax.XMLSecStartElement;
import org.apache.xml.security.stax.impl.processor.input.XMLEventReaderInputProcessor;
import org.apache.xml.security.stax.impl.transformer.TransformIdentity;
import org.apache.xml.security.utils.UnsyncByteArrayInputStream;
import org.apache.xml.security.utils.UnsyncByteArrayOutputStream;

public abstract class CanonicalizerBase
extends TransformIdentity {
    static final byte[] _END_PI = new byte[]{63, 62};
    static final byte[] _BEGIN_PI = new byte[]{60, 63};
    static final byte[] _END_COMM = new byte[]{45, 45, 62};
    static final byte[] _BEGIN_COMM = new byte[]{60, 33, 45, 45};
    static final byte[] __XA_ = new byte[]{38, 35, 120, 65, 59};
    static final byte[] __X9_ = new byte[]{38, 35, 120, 57, 59};
    static final byte[] _QUOT_ = new byte[]{38, 113, 117, 111, 116, 59};
    static final byte[] __XD_ = new byte[]{38, 35, 120, 68, 59};
    static final byte[] _GT_ = new byte[]{38, 103, 116, 59};
    static final byte[] _LT_ = new byte[]{38, 108, 116, 59};
    static final byte[] _END_TAG = new byte[]{60, 47};
    static final byte[] _AMP_ = new byte[]{38, 97, 109, 112, 59};
    static final byte[] EQUAL_STRING = new byte[]{61, 34};
    static final byte[] NEWLINE = new byte[]{10};
    protected static final String XML = "xml";
    protected static final String XMLNS = "xmlns";
    protected static final char DOUBLEPOINT = ':';
    private static final Map<String, byte[]> CACHE = Collections.synchronizedMap(new WeakHashMap());
    private final C14NStack<XMLSecEvent> outputStack = new C14NStack();
    private boolean includeComments = false;
    private DocumentLevel currentDocumentLevel = DocumentLevel.NODE_BEFORE_DOCUMENT_ELEMENT;
    protected boolean firstCall = true;

    public CanonicalizerBase(boolean includeComments) {
        this.includeComments = includeComments;
    }

    @Override
    public void setProperties(Map<String, Object> properties) throws XMLSecurityException {
        throw new UnsupportedOperationException("InclusiveNamespace-PrefixList not supported");
    }

    @Override
    public void setTransformer(Transformer transformer) throws XMLSecurityException {
        this.setOutputStream(new UnsyncByteArrayOutputStream());
        super.setTransformer(transformer);
    }

    protected List<XMLSecNamespace> getCurrentUtilizedNamespaces(XMLSecStartElement xmlSecStartElement, C14NStack<XMLSecEvent> outputStack) {
        List<XMLSecNamespace> utilizedNamespaces = Collections.emptyList();
        XMLSecNamespace elementNamespace = xmlSecStartElement.getElementNamespace();
        XMLSecNamespace found = (XMLSecNamespace)outputStack.containsOnStack(elementNamespace);
        if (found == null || found.getNamespaceURI() == null || !found.getNamespaceURI().equals(elementNamespace.getNamespaceURI())) {
            utilizedNamespaces = new ArrayList(2);
            utilizedNamespaces.add(elementNamespace);
            outputStack.peek().add(elementNamespace);
        }
        List<XMLSecNamespace> declaredNamespaces = xmlSecStartElement.getOnElementDeclaredNamespaces();
        for (int i = 0; i < declaredNamespaces.size(); ++i) {
            XMLSecNamespace comparableNamespace = declaredNamespaces.get(i);
            XMLSecNamespace resultNamespace = (XMLSecNamespace)outputStack.containsOnStack(comparableNamespace);
            if (resultNamespace != null && resultNamespace.getNamespaceURI() != null && resultNamespace.getNamespaceURI().equals(comparableNamespace.getNamespaceURI())) continue;
            if (utilizedNamespaces == Collections.emptyList()) {
                utilizedNamespaces = new ArrayList<XMLSecNamespace>(2);
            }
            utilizedNamespaces.add(comparableNamespace);
            outputStack.peek().add(comparableNamespace);
        }
        List<XMLSecAttribute> comparableAttributes = xmlSecStartElement.getOnElementDeclaredAttributes();
        for (int i = 0; i < comparableAttributes.size(); ++i) {
            XMLSecNamespace resultNamespace;
            XMLSecAttribute xmlSecAttribute = comparableAttributes.get(i);
            XMLSecNamespace attributeNamespace = xmlSecAttribute.getAttributeNamespace();
            if (XML.equals(attributeNamespace.getPrefix()) || attributeNamespace.getNamespaceURI() == null || attributeNamespace.getNamespaceURI().isEmpty() || (resultNamespace = (XMLSecNamespace)outputStack.containsOnStack(attributeNamespace)) != null && resultNamespace.getNamespaceURI() != null && resultNamespace.getNamespaceURI().equals(attributeNamespace.getNamespaceURI())) continue;
            if (utilizedNamespaces == Collections.emptyList()) {
                utilizedNamespaces = new ArrayList<XMLSecNamespace>(2);
            }
            utilizedNamespaces.add(attributeNamespace);
            outputStack.peek().add(attributeNamespace);
        }
        return utilizedNamespaces;
    }

    protected List<XMLSecAttribute> getCurrentUtilizedAttributes(XMLSecStartElement xmlSecStartElement, C14NStack<XMLSecEvent> outputStack) {
        List<XMLSecAttribute> comparableAttributes = xmlSecStartElement.getOnElementDeclaredAttributes();
        if (comparableAttributes.isEmpty()) {
            return Collections.emptyList();
        }
        return new ArrayList<XMLSecAttribute>(comparableAttributes);
    }

    protected List<XMLSecNamespace> getInitialUtilizedNamespaces(XMLSecStartElement xmlSecStartElement, C14NStack<XMLSecEvent> outputStack) {
        ArrayList<XMLSecNamespace> utilizedNamespaces = new ArrayList<XMLSecNamespace>();
        ArrayList<XMLSecNamespace> visibleNamespaces = new ArrayList<XMLSecNamespace>();
        xmlSecStartElement.getNamespacesFromCurrentScope(visibleNamespaces);
        for (int i = 0; i < visibleNamespaces.size(); ++i) {
            XMLSecNamespace comparableNamespace = (XMLSecNamespace)visibleNamespaces.get(i);
            XMLSecNamespace found = (XMLSecNamespace)outputStack.containsOnStack(comparableNamespace);
            if (found != null) {
                utilizedNamespaces.remove(comparableNamespace);
            }
            outputStack.peek().add(comparableNamespace);
            if (comparableNamespace.getNamespaceURI().isEmpty() && comparableNamespace.getPrefix().isEmpty()) continue;
            utilizedNamespaces.add(comparableNamespace);
        }
        return utilizedNamespaces;
    }

    protected List<XMLSecAttribute> getInitialUtilizedAttributes(XMLSecStartElement xmlSecStartElement, C14NStack<XMLSecEvent> outputStack) {
        List<XMLSecAttribute> utilizedAttributes = Collections.emptyList();
        ArrayList<XMLSecAttribute> comparableAttributes = new ArrayList<XMLSecAttribute>();
        xmlSecStartElement.getAttributesFromCurrentScope(comparableAttributes);
        for (int i = 0; i < comparableAttributes.size(); ++i) {
            XMLSecAttribute comparableAttribute = (XMLSecAttribute)comparableAttributes.get(i);
            if (!XML.equals(comparableAttribute.getName().getPrefix()) || outputStack.containsOnStack(comparableAttribute) != null) continue;
            if (utilizedAttributes == Collections.emptyList()) {
                utilizedAttributes = new ArrayList<XMLSecAttribute>(2);
            }
            utilizedAttributes.add(comparableAttribute);
            outputStack.peek().add(comparableAttribute);
        }
        List<XMLSecAttribute> elementAttributes = xmlSecStartElement.getOnElementDeclaredAttributes();
        for (int i = 0; i < elementAttributes.size(); ++i) {
            XMLSecAttribute comparableAttribute = elementAttributes.get(i);
            QName attributeName = comparableAttribute.getName();
            if (XML.equals(attributeName.getPrefix())) continue;
            if (utilizedAttributes == Collections.emptyList()) {
                utilizedAttributes = new ArrayList<XMLSecAttribute>(2);
            }
            utilizedAttributes.add(comparableAttribute);
        }
        return utilizedAttributes;
    }

    @Override
    public XMLSecurityConstants.TransformMethod getPreferredTransformMethod(XMLSecurityConstants.TransformMethod forInput) {
        switch (forInput) {
            case XMLSecEvent: {
                return XMLSecurityConstants.TransformMethod.XMLSecEvent;
            }
            case InputStream: {
                return XMLSecurityConstants.TransformMethod.InputStream;
            }
        }
        throw new IllegalArgumentException("Unsupported class " + forInput.name());
    }

    @Override
    public void transform(XMLSecEvent xmlSecEvent) throws XMLStreamException {
        try {
            OutputStream outputStream = this.getOutputStream();
            switch (xmlSecEvent.getEventType()) {
                case 1: {
                    int i;
                    List<Object> utilizedAttributes;
                    List<Object> utilizedNamespaces;
                    XMLSecStartElement xmlSecStartElement = xmlSecEvent.asStartElement();
                    this.currentDocumentLevel = DocumentLevel.NODE_NOT_BEFORE_OR_AFTER_DOCUMENT_ELEMENT;
                    this.outputStack.push((XMLSecEvent)((Object)Collections.emptyList()));
                    if (this.firstCall) {
                        utilizedNamespaces = new ArrayList();
                        utilizedAttributes = new ArrayList();
                        this.outputStack.peek().add(XMLSecEventFactory.createXMLSecNamespace(null, ""));
                        this.outputStack.push((XMLSecEvent)((Object)Collections.emptyList()));
                        utilizedNamespaces.addAll(this.getInitialUtilizedNamespaces(xmlSecStartElement, this.outputStack));
                        utilizedAttributes.addAll(this.getInitialUtilizedAttributes(xmlSecStartElement, this.outputStack));
                        this.firstCall = false;
                    } else {
                        utilizedNamespaces = this.getCurrentUtilizedNamespaces(xmlSecStartElement, this.outputStack);
                        utilizedAttributes = this.getCurrentUtilizedAttributes(xmlSecStartElement, this.outputStack);
                    }
                    outputStream.write(60);
                    String prefix = xmlSecStartElement.getName().getPrefix();
                    if (prefix != null && !prefix.isEmpty()) {
                        UtfHelpper.writeByte(prefix, outputStream, CACHE);
                        outputStream.write(58);
                    }
                    String name = xmlSecStartElement.getName().getLocalPart();
                    UtfHelpper.writeByte(name, outputStream, CACHE);
                    if (!utilizedNamespaces.isEmpty()) {
                        Collections.sort(utilizedNamespaces);
                        for (i = 0; i < utilizedNamespaces.size(); ++i) {
                            XMLSecNamespace xmlSecNamespace = (XMLSecNamespace)utilizedNamespaces.get(i);
                            if (!this.namespaceIsAbsolute(xmlSecNamespace.getNamespaceURI())) {
                                throw new XMLStreamException("namespace is relative encountered: " + xmlSecNamespace.getNamespaceURI());
                            }
                            if (xmlSecNamespace.isDefaultNamespaceDeclaration()) {
                                CanonicalizerBase.outputAttrToWriter(null, XMLNS, xmlSecNamespace.getNamespaceURI(), outputStream, CACHE);
                                continue;
                            }
                            CanonicalizerBase.outputAttrToWriter(XMLNS, xmlSecNamespace.getPrefix(), xmlSecNamespace.getNamespaceURI(), outputStream, CACHE);
                        }
                    }
                    if (!utilizedAttributes.isEmpty()) {
                        Collections.sort(utilizedAttributes);
                        for (i = 0; i < utilizedAttributes.size(); ++i) {
                            XMLSecAttribute xmlSecAttribute = (XMLSecAttribute)utilizedAttributes.get(i);
                            QName attributeName = xmlSecAttribute.getName();
                            String attributeNamePrefix = attributeName.getPrefix();
                            if (attributeNamePrefix != null && !attributeNamePrefix.isEmpty()) {
                                CanonicalizerBase.outputAttrToWriter(attributeNamePrefix, attributeName.getLocalPart(), xmlSecAttribute.getValue(), outputStream, CACHE);
                                continue;
                            }
                            CanonicalizerBase.outputAttrToWriter(null, attributeName.getLocalPart(), xmlSecAttribute.getValue(), outputStream, CACHE);
                        }
                    }
                    outputStream.write(62);
                    break;
                }
                case 2: {
                    XMLSecEndElement xmlSecEndElement = xmlSecEvent.asEndElement();
                    String localPrefix = xmlSecEndElement.getName().getPrefix();
                    outputStream.write(_END_TAG);
                    if (localPrefix != null && !localPrefix.isEmpty()) {
                        UtfHelpper.writeByte(localPrefix, outputStream, CACHE);
                        outputStream.write(58);
                    }
                    UtfHelpper.writeByte(xmlSecEndElement.getName().getLocalPart(), outputStream, CACHE);
                    outputStream.write(62);
                    this.outputStack.pop();
                    if (this.outputStack.size() != 1) break;
                    this.currentDocumentLevel = DocumentLevel.NODE_AFTER_DOCUMENT_ELEMENT;
                    break;
                }
                case 3: {
                    CanonicalizerBase.outputPItoWriter((XMLSecProcessingInstruction)xmlSecEvent, outputStream, this.currentDocumentLevel);
                    break;
                }
                case 4: {
                    if (this.currentDocumentLevel != DocumentLevel.NODE_NOT_BEFORE_OR_AFTER_DOCUMENT_ELEMENT) break;
                    CanonicalizerBase.outputTextToWriter(xmlSecEvent.asCharacters().getText(), outputStream);
                    break;
                }
                case 5: {
                    if (!this.includeComments) break;
                    CanonicalizerBase.outputCommentToWriter((XMLSecComment)xmlSecEvent, outputStream, this.currentDocumentLevel);
                    break;
                }
                case 6: {
                    if (this.currentDocumentLevel != DocumentLevel.NODE_NOT_BEFORE_OR_AFTER_DOCUMENT_ELEMENT) break;
                    CanonicalizerBase.outputTextToWriter(xmlSecEvent.asCharacters().getText(), outputStream);
                    break;
                }
                case 7: {
                    this.currentDocumentLevel = DocumentLevel.NODE_BEFORE_DOCUMENT_ELEMENT;
                    break;
                }
                case 8: {
                    break;
                }
                case 9: {
                    throw new XMLStreamException("illegal event :" + XMLSecurityUtils.getXMLEventAsString(xmlSecEvent));
                }
                case 10: {
                    throw new XMLStreamException("illegal event :" + XMLSecurityUtils.getXMLEventAsString(xmlSecEvent));
                }
                case 11: {
                    break;
                }
                case 12: {
                    CanonicalizerBase.outputTextToWriter(xmlSecEvent.asCharacters().getData(), outputStream);
                    break;
                }
                case 13: {
                    throw new XMLStreamException("illegal event :" + XMLSecurityUtils.getXMLEventAsString(xmlSecEvent));
                }
                case 14: {
                    throw new XMLStreamException("illegal event :" + XMLSecurityUtils.getXMLEventAsString(xmlSecEvent));
                }
                case 15: {
                    throw new XMLStreamException("illegal event :" + XMLSecurityUtils.getXMLEventAsString(xmlSecEvent));
                }
            }
        }
        catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }

    @Override
    public void transform(InputStream inputStream) throws XMLStreamException {
        XMLEventReaderInputProcessor xmlEventReaderInputProcessor = new XMLEventReaderInputProcessor(null, CanonicalizerBase.getXmlInputFactory().createXMLStreamReader(inputStream));
        try {
            XMLSecEvent xmlSecEvent;
            do {
                xmlSecEvent = xmlEventReaderInputProcessor.processEvent(null);
                this.transform(xmlSecEvent);
            } while (xmlSecEvent.getEventType() != 8);
        }
        catch (XMLSecurityException e) {
            throw new XMLStreamException(e);
        }
    }

    @Override
    public void doFinal() throws XMLStreamException {
        if (this.getTransformer() != null) {
            UnsyncByteArrayOutputStream baos = (UnsyncByteArrayOutputStream)this.getOutputStream();
            try (UnsyncByteArrayInputStream is = new UnsyncByteArrayInputStream(baos.toByteArray());){
                this.getTransformer().transform(is);
                this.getTransformer().doFinal();
            }
            catch (IOException ex) {
                throw new XMLStreamException(ex);
            }
        }
    }

    protected static void outputAttrToWriter(String prefix, String name, String value, OutputStream writer, Map<String, byte[]> CACHE) throws IOException {
        writer.write(32);
        if (prefix != null) {
            UtfHelpper.writeByte(prefix, writer, CACHE);
            UtfHelpper.writeCodePointToUtf8(58, writer);
        }
        UtfHelpper.writeByte(name, writer, CACHE);
        writer.write(EQUAL_STRING);
        int length = value.length();
        int i = 0;
        block8: while (i < length) {
            byte[] toWrite;
            int c = value.codePointAt(i);
            i += Character.charCount(c);
            switch (c) {
                case 38: {
                    toWrite = _AMP_;
                    break;
                }
                case 60: {
                    toWrite = _LT_;
                    break;
                }
                case 34: {
                    toWrite = _QUOT_;
                    break;
                }
                case 9: {
                    toWrite = __X9_;
                    break;
                }
                case 10: {
                    toWrite = __XA_;
                    break;
                }
                case 13: {
                    toWrite = __XD_;
                    break;
                }
                default: {
                    if (c < 128) {
                        writer.write(c);
                        continue block8;
                    }
                    UtfHelpper.writeCodePointToUtf8(c, writer);
                    continue block8;
                }
            }
            writer.write(toWrite);
        }
        writer.write(34);
    }

    protected static void outputTextToWriter(String text, OutputStream writer) throws IOException {
        int length = text.length();
        int i = 0;
        block6: while (i < length) {
            byte[] toWrite;
            int c = text.codePointAt(i);
            i += Character.charCount(c);
            switch (c) {
                case 38: {
                    toWrite = _AMP_;
                    break;
                }
                case 60: {
                    toWrite = _LT_;
                    break;
                }
                case 62: {
                    toWrite = _GT_;
                    break;
                }
                case 13: {
                    toWrite = __XD_;
                    break;
                }
                default: {
                    if (c < 128) {
                        writer.write(c);
                        continue block6;
                    }
                    UtfHelpper.writeCodePointToUtf8(c, writer);
                    continue block6;
                }
            }
            writer.write(toWrite);
        }
    }

    protected static void outputTextToWriter(char[] text, OutputStream writer) throws IOException {
        int length = text.length;
        block6: for (int i = 0; i < length; ++i) {
            byte[] toWrite;
            int c = Character.isHighSurrogate(text[i]) && i + 1 != length && Character.isLowSurrogate(text[i + 1]) ? Character.toCodePoint(text[i], text[++i]) : text[i];
            switch (c) {
                case 38: {
                    toWrite = _AMP_;
                    break;
                }
                case 60: {
                    toWrite = _LT_;
                    break;
                }
                case 62: {
                    toWrite = _GT_;
                    break;
                }
                case 13: {
                    toWrite = __XD_;
                    break;
                }
                default: {
                    if (c < 128) {
                        writer.write(c);
                        continue block6;
                    }
                    UtfHelpper.writeCodePointToUtf8(c, writer);
                    continue block6;
                }
            }
            writer.write(toWrite);
        }
    }

    protected static void outputPItoWriter(XMLSecProcessingInstruction currentPI, OutputStream writer, DocumentLevel position) throws IOException {
        int c;
        if (position == DocumentLevel.NODE_AFTER_DOCUMENT_ELEMENT) {
            writer.write(NEWLINE);
        }
        writer.write(_BEGIN_PI);
        String target = currentPI.getTarget();
        int length = target.length();
        for (int i = 0; i < length; i += Character.charCount(c)) {
            c = target.codePointAt(i);
            if (c == 13) {
                writer.write(__XD_);
                continue;
            }
            if (c < 128) {
                writer.write(c);
                continue;
            }
            UtfHelpper.writeCodePointToUtf8(c, writer);
        }
        String data = currentPI.getData();
        length = data.length();
        if (length > 0) {
            int c2;
            writer.write(32);
            for (int i = 0; i < length; i += Character.charCount(c2)) {
                c2 = data.codePointAt(i);
                if (c2 == 13) {
                    writer.write(__XD_);
                    continue;
                }
                UtfHelpper.writeCodePointToUtf8(c2, writer);
            }
        }
        writer.write(_END_PI);
        if (position == DocumentLevel.NODE_BEFORE_DOCUMENT_ELEMENT) {
            writer.write(NEWLINE);
        }
    }

    protected static void outputCommentToWriter(XMLSecComment currentComment, OutputStream writer, DocumentLevel position) throws IOException {
        int c;
        if (position == DocumentLevel.NODE_AFTER_DOCUMENT_ELEMENT) {
            writer.write(NEWLINE);
        }
        writer.write(_BEGIN_COMM);
        String data = currentComment.getText();
        int length = data.length();
        for (int i = 0; i < length; i += Character.charCount(c)) {
            c = data.codePointAt(i);
            if (c == 13) {
                writer.write(__XD_);
                continue;
            }
            if (c < 128) {
                writer.write(c);
                continue;
            }
            UtfHelpper.writeCodePointToUtf8(c, writer);
        }
        writer.write(_END_COMM);
        if (position == DocumentLevel.NODE_BEFORE_DOCUMENT_ELEMENT) {
            writer.write(NEWLINE);
        }
    }

    private boolean namespaceIsAbsolute(String namespaceValue) {
        if (namespaceValue.isEmpty()) {
            return true;
        }
        return namespaceValue.indexOf(58) > 0;
    }

    public static class C14NStack<E>
    extends ArrayDeque<List<Comparable>> {
        public Object containsOnStack(Object o) {
            Iterator elementIterator = super.iterator();
            while (elementIterator.hasNext()) {
                int idx;
                List list = (List)elementIterator.next();
                if (list.isEmpty() || (idx = list.lastIndexOf(o)) == -1) continue;
                return list.get(idx);
            }
            return null;
        }

        @Override
        public List<Comparable> peek() {
            ArrayList list = (ArrayList)super.peekFirst();
            if (list == Collections.emptyList()) {
                super.removeFirst();
                list = new ArrayList();
                super.addFirst(list);
            }
            return list;
        }

        @Override
        public List<Comparable> peekFirst() {
            throw new UnsupportedOperationException("Use peek()");
        }
    }

    private static enum DocumentLevel {
        NODE_BEFORE_DOCUMENT_ELEMENT,
        NODE_NOT_BEFORE_OR_AFTER_DOCUMENT_ELEMENT,
        NODE_AFTER_DOCUMENT_ELEMENT;

    }
}

