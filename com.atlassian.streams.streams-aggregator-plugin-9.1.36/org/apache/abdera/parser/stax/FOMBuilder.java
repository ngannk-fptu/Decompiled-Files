/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.parser.stax;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Text;
import org.apache.abdera.parser.ParseException;
import org.apache.abdera.parser.ParserOptions;
import org.apache.abdera.parser.stax.FOMElement;
import org.apache.abdera.parser.stax.FOMFactory;
import org.apache.abdera.parser.stax.FOMStAXFilter;
import org.apache.abdera.parser.stax.FOMUnsupportedContentTypeException;
import org.apache.abdera.parser.stax.FOMUnsupportedTextTypeException;
import org.apache.abdera.util.Constants;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class FOMBuilder
extends StAXOMBuilder
implements Constants {
    private final FOMFactory fomfactory;
    private final ParserOptions parserOptions;
    private boolean indoc = false;

    public FOMBuilder(FOMFactory factory, XMLStreamReader parser, ParserOptions parserOptions) {
        super(factory, new FOMStAXFilter(parser, parserOptions));
        this.document = (OMDocument)((Object)factory.newDocument());
        this.parserOptions = parserOptions;
        this.fomfactory = factory;
        String enc = parser.getCharacterEncodingScheme();
        this.document.setCharsetEncoding(enc != null ? enc : "utf-8");
        this.document.setXMLVersion(parser.getVersion() != null ? parser.getVersion() : "1.0");
    }

    public ParserOptions getParserOptions() {
        return this.parserOptions;
    }

    protected Text.Type getTextType() {
        Text.Type ttype = Text.Type.TEXT;
        String type = this.parser.getAttributeValue(null, "type");
        if (type != null && (ttype = Text.Type.typeFromString(type)) == null) {
            throw new FOMUnsupportedTextTypeException(type);
        }
        return ttype;
    }

    protected Content.Type getContentType() {
        Content.Type ctype = Content.Type.TEXT;
        String type = this.parser.getAttributeValue(null, "type");
        String src = this.parser.getAttributeValue(null, "src");
        if (type != null) {
            ctype = Content.Type.typeFromString(type);
            if (ctype == null) {
                throw new FOMUnsupportedContentTypeException(type);
            }
        } else if (type == null && src != null) {
            ctype = Content.Type.MEDIA;
        }
        return ctype;
    }

    @Override
    public int next() throws OMException {
        try {
            return super.next();
        }
        catch (OMException e) {
            throw new ParseException(e);
        }
    }

    @Override
    protected OMElement constructNode(OMContainer parent, String name) {
        QName qname;
        OMElement element = null;
        if (!this.indoc) {
            parent = this.document;
            this.indoc = true;
        }
        if ((element = this.fomfactory.createElement(qname = this.parser.getName(), parent, this)) == null) {
            element = new FOMElement(qname.getLocalPart(), parent, this.fomfactory, this);
        }
        return element;
    }

    public <T extends Element> Document<T> getFomDocument() {
        while (!this.indoc && !this.done) {
            this.next();
        }
        return (Document)((Object)this.document);
    }

    @Override
    public OMDocument getDocument() {
        return (OMDocument)((Object)this.getFomDocument());
    }

    public FOMFactory getFactory() {
        return this.fomfactory;
    }
}

