/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.parser.stax;

import java.io.InputStream;
import java.io.Reader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.i18n.text.Localizer;
import org.apache.abdera.i18n.text.io.CompressionUtil;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.parser.ParseException;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.parser.ParserOptions;
import org.apache.abdera.parser.stax.FOMBuilder;
import org.apache.abdera.parser.stax.FOMFactory;
import org.apache.abdera.parser.stax.FOMParserOptions;
import org.apache.abdera.parser.stax.util.FOMSniffingInputStream;
import org.apache.abdera.parser.stax.util.FOMXmlRestrictedCharReader;
import org.apache.abdera.util.AbstractParser;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.util.StAXParserConfiguration;
import org.apache.axiom.om.util.StAXUtils;
import org.apache.axiom.util.stax.dialect.StAXDialect;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class FOMParser
extends AbstractParser
implements Parser {
    private static final StAXParserConfiguration ABDERA_PARSER_CONFIGURATION = new StAXParserConfiguration(){

        public XMLInputFactory configure(XMLInputFactory factory, StAXDialect dialect) {
            factory.setProperty("javax.xml.stream.isReplacingEntityReferences", Boolean.FALSE);
            return factory;
        }

        public String toString() {
            return "ABDERA";
        }
    };

    public FOMParser() {
    }

    public FOMParser(Abdera abdera) {
        super(abdera);
    }

    private FOMFactory getFomFactory(ParserOptions options) {
        FOMFactory factory;
        FOMFactory fOMFactory = factory = options != null && options.getFactory() != null ? (FOMFactory)options.getFactory() : null;
        if (factory == null) {
            Factory f = this.getFactory();
            factory = f instanceof FOMFactory ? (FOMFactory)f : new FOMFactory();
        }
        return factory;
    }

    private <T extends Element> Document<T> getDocument(FOMBuilder builder, IRI base, ParserOptions options) throws ParseException {
        Document document = builder.getFomDocument();
        try {
            if (base != null) {
                document.setBaseUri(base.toString());
            }
            if (options != null && options.getCharset() != null) {
                ((OMDocument)((Object)document)).setCharsetEncoding(options.getCharset());
            }
            if (options != null) {
                document.setMustPreserveWhitespace(options.getMustPreserveWhitespace());
            }
        }
        catch (Exception e2) {
            ParseException e2;
            if (!(e2 instanceof ParseException)) {
                e2 = new ParseException(e2);
            }
            throw (ParseException)e2;
        }
        return document;
    }

    @Override
    public <T extends Element> Document<T> parse(InputStream in, String base, ParserOptions options) throws ParseException {
        if (in == null) {
            throw new IllegalArgumentException(Localizer.get("INPUTSTREAM.NOT.NULL"));
        }
        try {
            String charset;
            if (options == null) {
                options = this.getDefaultParserOptions();
            }
            if (options.getCompressionCodecs() != null) {
                in = CompressionUtil.getDecodingInputStream(in, options.getCompressionCodecs());
            }
            if ((charset = options.getCharset()) == null && options.getAutodetectCharset()) {
                FOMSniffingInputStream sin = in instanceof FOMSniffingInputStream ? (FOMSniffingInputStream)in : new FOMSniffingInputStream(in);
                charset = sin.getEncoding();
                if (charset != null) {
                    options.setCharset(charset);
                }
                in = sin;
            }
            if (options.getFilterRestrictedCharacters()) {
                FOMXmlRestrictedCharReader rdr = charset == null ? new FOMXmlRestrictedCharReader(in, options.getFilterRestrictedCharacterReplacement()) : new FOMXmlRestrictedCharReader(in, charset, options.getFilterRestrictedCharacterReplacement());
                return this.parse(StAXUtils.createXMLStreamReader(rdr), base, options);
            }
            XMLStreamReader xmlreader = charset == null ? FOMParser.createXMLStreamReader(in) : FOMParser.createXMLStreamReader(in, charset);
            return this.parse(xmlreader, base, options);
        }
        catch (Exception e2) {
            ParseException e2;
            if (!(e2 instanceof ParseException)) {
                e2 = new ParseException(e2);
            }
            throw (ParseException)e2;
        }
    }

    @Override
    public <T extends Element> Document<T> parse(Reader in, String base, ParserOptions options) throws ParseException {
        if (in == null) {
            throw new IllegalArgumentException(Localizer.get("READER.NOT.NULL"));
        }
        try {
            if (options == null) {
                options = this.getDefaultParserOptions();
            }
            if (options.getFilterRestrictedCharacters() && !(in instanceof FOMXmlRestrictedCharReader)) {
                in = new FOMXmlRestrictedCharReader(in, options.getFilterRestrictedCharacterReplacement());
            }
            return this.parse(this.createXMLStreamReader(in), base, options);
        }
        catch (Exception e2) {
            ParseException e2;
            if (!(e2 instanceof ParseException)) {
                e2 = new ParseException(e2);
            }
            throw (ParseException)e2;
        }
    }

    private static XMLInputFactory getXMLInputFactory() {
        return StAXUtils.getXMLInputFactory(ABDERA_PARSER_CONFIGURATION);
    }

    private static void releaseXMLInputFactory(XMLInputFactory inputFactory) {
        StAXUtils.releaseXMLInputFactory(inputFactory);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static XMLStreamReader createXMLStreamReader(InputStream in, String encoding) throws XMLStreamException {
        XMLInputFactory inputFactory = FOMParser.getXMLInputFactory();
        try {
            XMLStreamReader xMLStreamReader = inputFactory.createXMLStreamReader(in, encoding);
            return xMLStreamReader;
        }
        finally {
            FOMParser.releaseXMLInputFactory(inputFactory);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static XMLStreamReader createXMLStreamReader(InputStream in) throws XMLStreamException {
        XMLInputFactory inputFactory = FOMParser.getXMLInputFactory();
        try {
            XMLStreamReader xMLStreamReader = inputFactory.createXMLStreamReader(in);
            return xMLStreamReader;
        }
        finally {
            FOMParser.releaseXMLInputFactory(inputFactory);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private XMLStreamReader createXMLStreamReader(Reader in) throws XMLStreamException {
        XMLInputFactory inputFactory = FOMParser.getXMLInputFactory();
        try {
            XMLStreamReader reader;
            XMLStreamReader xMLStreamReader = reader = inputFactory.createXMLStreamReader(in);
            return xMLStreamReader;
        }
        finally {
            FOMParser.releaseXMLInputFactory(inputFactory);
        }
    }

    @Override
    public <T extends Element> Document<T> parse(XMLStreamReader reader, String base, ParserOptions options) throws ParseException {
        try {
            FOMBuilder builder = new FOMBuilder(this.getFomFactory(options), reader, options);
            return this.getDocument(builder, base != null ? new IRI(base) : null, options);
        }
        catch (Exception e2) {
            ParseException e2;
            if (!(e2 instanceof ParseException)) {
                e2 = new ParseException(e2);
            }
            throw (ParseException)e2;
        }
    }

    @Override
    protected ParserOptions initDefaultParserOptions() {
        return new FOMParserOptions(this.getFactory());
    }
}

