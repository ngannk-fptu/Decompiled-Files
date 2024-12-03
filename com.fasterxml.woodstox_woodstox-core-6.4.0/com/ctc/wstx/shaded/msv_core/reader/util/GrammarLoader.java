/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.util;

import com.ctc.wstx.shaded.msv.org_isorelax.verifier.Schema;
import com.ctc.wstx.shaded.msv.org_isorelax.verifier.Verifier;
import com.ctc.wstx.shaded.msv.org_isorelax.verifier.VerifierConfigurationException;
import com.ctc.wstx.shaded.msv.org_isorelax.verifier.VerifierFilter;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionPool;
import com.ctc.wstx.shaded.msv_core.grammar.Grammar;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.XMLSchemaGrammar;
import com.ctc.wstx.shaded.msv_core.reader.Controller;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReader;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReaderController;
import com.ctc.wstx.shaded.msv_core.reader.dtd.DTDReader;
import com.ctc.wstx.shaded.msv_core.reader.relax.core.RELAXCoreReader;
import com.ctc.wstx.shaded.msv_core.reader.trex.classic.TREXGrammarReader;
import com.ctc.wstx.shaded.msv_core.reader.trex.ng.comp.RELAXNGCompReader;
import com.ctc.wstx.shaded.msv_core.reader.xmlschema.XMLSchemaReader;
import com.ctc.wstx.shaded.msv_core.relaxns.reader.RELAXNSReader;
import com.ctc.wstx.shaded.msv_core.util.Util;
import com.ctc.wstx.shaded.msv_core.verifier.jaxp.SAXParserFactoryImpl;
import com.ctc.wstx.shaded.msv_core.verifier.regexp.REDocumentDeclaration;
import com.ctc.wstx.shaded.msv_core.verifier.regexp.xmlschema.XSREDocDecl;
import java.io.IOException;
import java.util.Vector;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class GrammarLoader {
    private SAXParserFactory factory;
    private Controller controller;
    private ExpressionPool pool;
    private boolean strictCheck = false;

    public static REDocumentDeclaration loadVGM(String url, GrammarReaderController controller, SAXParserFactory factory) throws SAXException, ParserConfigurationException, IOException {
        Grammar g = GrammarLoader.loadSchema(url, controller, factory);
        if (g != null) {
            return GrammarLoader.wrapByVGM(g);
        }
        return null;
    }

    public static REDocumentDeclaration loadVGM(InputSource source, GrammarReaderController controller, SAXParserFactory factory) throws SAXException, ParserConfigurationException, IOException {
        Grammar g = GrammarLoader.loadSchema(source, controller, factory);
        if (g != null) {
            return GrammarLoader.wrapByVGM(g);
        }
        return null;
    }

    private static REDocumentDeclaration wrapByVGM(Grammar g) {
        if (g instanceof XMLSchemaGrammar) {
            return new XSREDocDecl((XMLSchemaGrammar)g);
        }
        return new REDocumentDeclaration(g);
    }

    public static REDocumentDeclaration loadVGM(String url) throws SAXException, ParserConfigurationException, IOException {
        try {
            return GrammarLoader.loadVGM(url, (GrammarReaderController)new ThrowController(), null);
        }
        catch (GrammarLoaderException e) {
            throw e.e;
        }
    }

    public static REDocumentDeclaration loadVGM(InputSource source) throws SAXException, ParserConfigurationException, IOException {
        try {
            return GrammarLoader.loadVGM(source, (GrammarReaderController)new ThrowController(), null);
        }
        catch (GrammarLoaderException e) {
            throw e.e;
        }
    }

    public static Grammar loadSchema(String url, GrammarReaderController controller, SAXParserFactory factory) throws SAXException, ParserConfigurationException, IOException {
        GrammarLoader loader = new GrammarLoader();
        loader.setController(controller);
        loader.setSAXParserFactory(factory);
        return loader.parse(url);
    }

    public static Grammar loadSchema(InputSource source, GrammarReaderController controller, SAXParserFactory factory) throws SAXException, ParserConfigurationException, IOException {
        GrammarLoader loader = new GrammarLoader();
        loader.setController(controller);
        loader.setSAXParserFactory(factory);
        return loader.parse(source);
    }

    public static Grammar loadSchema(String source, GrammarReaderController controller) throws SAXException, ParserConfigurationException, IOException {
        GrammarLoader loader = new GrammarLoader();
        loader.setController(controller);
        return loader.parse(source);
    }

    public static Grammar loadSchema(InputSource source, GrammarReaderController controller) throws SAXException, ParserConfigurationException, IOException {
        GrammarLoader loader = new GrammarLoader();
        loader.setController(controller);
        return loader.parse(source);
    }

    public static Grammar loadSchema(String url) throws SAXException, ParserConfigurationException, IOException {
        try {
            return GrammarLoader.loadSchema(url, (GrammarReaderController)new ThrowController(), null);
        }
        catch (GrammarLoaderException e) {
            throw e.e;
        }
    }

    public static Grammar loadSchema(InputSource source) throws SAXException, ParserConfigurationException, IOException {
        try {
            return GrammarLoader.loadSchema(source, (GrammarReaderController)new ThrowController(), null);
        }
        catch (GrammarLoaderException e) {
            throw e.e;
        }
    }

    public void setSAXParserFactory(SAXParserFactory factory) {
        this.factory = factory;
    }

    public SAXParserFactory getSAXParserFactory() {
        if (this.factory == null) {
            this.factory = SAXParserFactory.newInstance();
            this.factory.setNamespaceAware(true);
        }
        return this.factory;
    }

    public void setController(GrammarReaderController controller) {
        this.controller = new Controller(controller);
    }

    public Controller getController() {
        if (this.controller == null) {
            this.controller = new Controller(new GrammarReaderController(){

                public void warning(Locator[] locs, String errorMessage) {
                }

                public void error(Locator[] locs, String errorMessage, Exception nestedException) {
                }

                public InputSource resolveEntity(String s, String p) {
                    return null;
                }
            });
        }
        return this.controller;
    }

    public void setPool(ExpressionPool pool) {
        this.pool = pool;
    }

    public ExpressionPool getPool() {
        if (this.pool == null) {
            return new ExpressionPool();
        }
        return this.pool;
    }

    public void setStrictCheck(boolean value) {
        this.strictCheck = value;
    }

    public boolean getStrictCheck() {
        return this.strictCheck;
    }

    public Grammar parse(InputSource source) throws SAXException, ParserConfigurationException, IOException {
        return this._loadSchema(source);
    }

    public Grammar parse(String url) throws SAXException, ParserConfigurationException, IOException {
        return this._loadSchema(url);
    }

    public REDocumentDeclaration parseVGM(String url) throws SAXException, ParserConfigurationException, IOException {
        Grammar g = this._loadSchema(url);
        if (g == null) {
            return null;
        }
        return new REDocumentDeclaration(g);
    }

    public REDocumentDeclaration parseVGM(InputSource source) throws SAXException, ParserConfigurationException, IOException {
        Grammar g = this._loadSchema(source);
        if (g == null) {
            return null;
        }
        return new REDocumentDeclaration(g);
    }

    private boolean hasDTDextension(String name) {
        if (name == null) {
            return false;
        }
        int idx = name.length() - 4;
        if (idx < 0) {
            return false;
        }
        return name.substring(idx).equalsIgnoreCase(".dtd");
    }

    private Grammar _loadSchema(Object source) throws SAXException, ParserConfigurationException, IOException {
        boolean isDTD = false;
        if (source instanceof String && this.hasDTDextension((String)source)) {
            isDTD = true;
        }
        if (source instanceof InputSource && this.hasDTDextension(((InputSource)source).getSystemId())) {
            isDTD = true;
        }
        if (isDTD) {
            if (source instanceof String) {
                source = Util.getInputSource((String)source);
            }
            return DTDReader.parse((InputSource)source, this.getController());
        }
        final GrammarReader[] reader = new GrammarReader[1];
        final XMLReader parser = this.getSAXParserFactory().newSAXParser().getXMLReader();
        parser.setContentHandler(new DefaultHandler(){
            private Locator locator;
            private Vector prefixes = new Vector();

            public void setDocumentLocator(Locator loc) {
                this.locator = loc;
            }

            public void startPrefixMapping(String prefix, String uri) {
                this.prefixes.add(new String[]{prefix, uri});
            }

            private ContentHandler setupPipeline(Schema schema) throws SAXException {
                try {
                    Verifier v = schema.newVerifier();
                    v.setErrorHandler(GrammarLoader.this.getController());
                    v.setEntityResolver(GrammarLoader.this.getController());
                    VerifierFilter filter = v.getVerifierFilter();
                    filter.setContentHandler(reader[0]);
                    return (ContentHandler)((Object)filter);
                }
                catch (VerifierConfigurationException vce) {
                    throw new SAXException(vce);
                }
            }

            public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
                ContentHandler winner;
                Schema s;
                if (localName.equals("module")) {
                    if (GrammarLoader.this.strictCheck) {
                        s = RELAXCoreReader.getRELAXCoreSchema4Schema();
                        reader[0] = new RELAXCoreReader(GrammarLoader.this.getController(), new SAXParserFactoryImpl(GrammarLoader.this.getSAXParserFactory(), s), GrammarLoader.this.getPool());
                        winner = this.setupPipeline(s);
                    } else {
                        reader[0] = new RELAXCoreReader(GrammarLoader.this.getController(), GrammarLoader.this.getSAXParserFactory(), GrammarLoader.this.getPool());
                        winner = reader[0];
                    }
                } else if (localName.equals("schema")) {
                    if (GrammarLoader.this.strictCheck) {
                        s = XMLSchemaReader.getXmlSchemaForXmlSchema();
                        reader[0] = new XMLSchemaReader(GrammarLoader.this.getController(), new SAXParserFactoryImpl(GrammarLoader.this.getSAXParserFactory(), s), GrammarLoader.this.getPool());
                        winner = this.setupPipeline(s);
                    } else {
                        reader[0] = new XMLSchemaReader(GrammarLoader.this.getController(), GrammarLoader.this.getSAXParserFactory(), GrammarLoader.this.getPool());
                        winner = reader[0];
                    }
                } else if ("http://www.xml.gr.jp/xmlns/relaxNamespace".equals(namespaceURI)) {
                    reader[0] = new RELAXNSReader(GrammarLoader.this.getController(), GrammarLoader.this.getSAXParserFactory(), GrammarLoader.this.getPool());
                    winner = reader[0];
                } else if ("http://www.thaiopensource.com/trex".equals(namespaceURI) || namespaceURI.equals("")) {
                    reader[0] = new TREXGrammarReader(GrammarLoader.this.getController(), GrammarLoader.this.getSAXParserFactory(), GrammarLoader.this.getPool());
                    winner = reader[0];
                } else if (GrammarLoader.this.strictCheck) {
                    s = RELAXNGCompReader.getRELAXNGSchema4Schema();
                    reader[0] = new RELAXNGCompReader(GrammarLoader.this.getController(), new SAXParserFactoryImpl(GrammarLoader.this.getSAXParserFactory(), s), GrammarLoader.this.getPool());
                    winner = this.setupPipeline(s);
                } else {
                    reader[0] = new RELAXNGCompReader(GrammarLoader.this.getController(), GrammarLoader.this.getSAXParserFactory(), GrammarLoader.this.getPool());
                    winner = reader[0];
                }
                winner.setDocumentLocator(this.locator);
                winner.startDocument();
                for (int i = 0; i < this.prefixes.size(); ++i) {
                    String[] d = (String[])this.prefixes.get(i);
                    winner.startPrefixMapping(d[0], d[1]);
                }
                winner.startElement(namespaceURI, localName, qName, atts);
                parser.setContentHandler(winner);
            }
        });
        parser.setErrorHandler(this.getController());
        parser.setEntityResolver(this.getController());
        if (source instanceof String) {
            parser.parse((String)source);
        } else {
            parser.parse((InputSource)source);
        }
        if (this.getController().hadError()) {
            return null;
        }
        return reader[0].getResultAsGrammar();
    }

    private static class ThrowController
    implements GrammarReaderController {
        private ThrowController() {
        }

        public void warning(Locator[] locs, String errorMessage) {
        }

        public void error(Locator[] locs, String errorMessage, Exception nestedException) {
            for (int i = 0; i < locs.length; ++i) {
                if (locs[i] == null) continue;
                throw new GrammarLoaderException(new SAXParseException(errorMessage, locs[i], nestedException));
            }
            throw new GrammarLoaderException(new SAXException(errorMessage, nestedException));
        }

        public InputSource resolveEntity(String p, String s) {
            return null;
        }
    }

    private static class GrammarLoaderException
    extends RuntimeException {
        public final SAXException e;

        GrammarLoaderException(SAXException e) {
            super(e.getMessage());
            this.e = e;
        }
    }
}

