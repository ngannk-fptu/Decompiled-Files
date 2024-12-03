/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map.ext;

import java.io.StringReader;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.deser.std.FromStringDeserializer;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class DOMDeserializer<T>
extends FromStringDeserializer<T> {
    static final DocumentBuilderFactory _parserFactory = DocumentBuilderFactory.newInstance();

    protected DOMDeserializer(Class<T> cls) {
        super(cls);
    }

    @Override
    public abstract T _deserialize(String var1, DeserializationContext var2);

    protected final Document parse(String value) throws IllegalArgumentException {
        try {
            return _parserFactory.newDocumentBuilder().parse(new InputSource(new StringReader(value)));
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Failed to parse JSON String as XML: " + e.getMessage(), e);
        }
    }

    static {
        _parserFactory.setNamespaceAware(true);
        _parserFactory.setExpandEntityReferences(false);
        try {
            _parserFactory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
        }
        catch (ParserConfigurationException pce) {
            System.err.println("[DOMDeserializer] Problem setting SECURE_PROCESSING_FEATURE: " + pce.toString());
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class DocumentDeserializer
    extends DOMDeserializer<Document> {
        public DocumentDeserializer() {
            super(Document.class);
        }

        @Override
        public Document _deserialize(String value, DeserializationContext ctxt) throws IllegalArgumentException {
            return this.parse(value);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class NodeDeserializer
    extends DOMDeserializer<Node> {
        public NodeDeserializer() {
            super(Node.class);
        }

        @Override
        public Node _deserialize(String value, DeserializationContext ctxt) throws IllegalArgumentException {
            return this.parse(value);
        }
    }
}

