/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.antlr.v4.runtime.ANTLRErrorListener
 *  org.antlr.v4.runtime.ANTLRErrorStrategy
 *  org.antlr.v4.runtime.CharStream
 *  org.antlr.v4.runtime.CommonTokenStream
 *  org.antlr.v4.runtime.RecognitionException
 *  org.antlr.v4.runtime.TokenSource
 *  org.antlr.v4.runtime.TokenStream
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package cz.vutbr.web.csskit.antlr4;

import cz.vutbr.web.css.CSSException;
import cz.vutbr.web.css.CSSFactory;
import cz.vutbr.web.css.MediaQuery;
import cz.vutbr.web.css.NetworkProcessor;
import cz.vutbr.web.css.RuleBlock;
import cz.vutbr.web.css.RuleList;
import cz.vutbr.web.css.StyleSheet;
import cz.vutbr.web.csskit.antlr4.CSSErrorListener;
import cz.vutbr.web.csskit.antlr4.CSSErrorStrategy;
import cz.vutbr.web.csskit.antlr4.CSSInputStream;
import cz.vutbr.web.csskit.antlr4.CSSLexer;
import cz.vutbr.web.csskit.antlr4.CSSParser;
import cz.vutbr.web.csskit.antlr4.CSSParserExtractor;
import cz.vutbr.web.csskit.antlr4.CSSParserVisitorImpl;
import cz.vutbr.web.csskit.antlr4.Preparator;
import cz.vutbr.web.csskit.antlr4.SimplePreparator;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.ANTLRErrorStrategy;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.TokenSource;
import org.antlr.v4.runtime.TokenStream;
import org.fit.net.DataURLHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

public class CSSParserFactory {
    private static final Logger log = LoggerFactory.getLogger(CSSParserFactory.class);
    private static CSSParserFactory instance;

    protected CSSParserFactory() {
    }

    public static CSSParserFactory getInstance() {
        if (instance == null) {
            instance = new CSSParserFactory();
        }
        return instance;
    }

    public StyleSheet parse(Object source, NetworkProcessor network, String encoding, SourceType type, Element inline, boolean inlinePriority, URL base) throws IOException, CSSException {
        StyleSheet sheet = (StyleSheet)CSSFactory.getRuleFactory().createStyleSheet().unlock();
        SimplePreparator preparator = new SimplePreparator(inline, inlinePriority);
        return this.parseAndImport(source, network, encoding, type, sheet, preparator, base, null);
    }

    public StyleSheet parse(Object source, NetworkProcessor network, String encoding, SourceType type, URL base) throws IOException, CSSException {
        if (type == SourceType.INLINE) {
            throw new IllegalArgumentException("Missing element for INLINE input");
        }
        return this.parse(source, network, encoding, type, null, false, base);
    }

    public StyleSheet append(Object source, NetworkProcessor network, String encoding, SourceType type, Element inline, boolean inlinePriority, StyleSheet sheet, URL base) throws IOException, CSSException {
        SimplePreparator preparator = new SimplePreparator(inline, inlinePriority);
        return this.parseAndImport(source, network, encoding, type, sheet, preparator, base, null);
    }

    public StyleSheet append(Object source, NetworkProcessor network, String encoding, SourceType type, StyleSheet sheet, URL base) throws IOException, CSSException {
        if (type == SourceType.INLINE) {
            throw new IllegalArgumentException("Missing element for INLINE input");
        }
        return this.append(source, network, encoding, type, null, false, sheet, base);
    }

    protected StyleSheet parseAndImport(Object source, NetworkProcessor network, String encoding, SourceType type, StyleSheet sheet, Preparator preparator, URL base, List<MediaQuery> media) throws CSSException, IOException {
        CSSParser parser = CSSParserFactory.createParser(source, network, encoding, type, base);
        CSSParserExtractor extractor = CSSParserFactory.parse(parser, type, preparator, media);
        for (int i = 0; i < extractor.getImportPaths().size(); ++i) {
            String path = extractor.getImportPaths().get(i);
            List<MediaQuery> imedia = extractor.getImportMedia().get(i);
            if ((imedia == null || imedia.isEmpty()) && CSSFactory.getAutoImportMedia().matchesEmpty() || CSSFactory.getAutoImportMedia().matchesOneOf(imedia)) {
                URL url = DataURLHandler.createURL(base, path);
                try {
                    this.parseAndImport(url, network, encoding, SourceType.URL, sheet, preparator, url, imedia);
                }
                catch (IOException e) {
                    log.warn("Couldn't read imported style sheet: {}", (Object)e.getMessage());
                }
                continue;
            }
            log.trace("Skipping import {} (media not matching)", (Object)path);
        }
        return CSSParserFactory.addRulesToStyleSheet(extractor.getRules(), sheet);
    }

    private static CSSParser createParser(Object source, NetworkProcessor network, String encoding, SourceType type, URL base) throws IOException, CSSException {
        CSSInputStream input = CSSParserFactory.getInput(source, network, encoding, type);
        input.setBase(base);
        return CSSParserFactory.createParserForInput(input);
    }

    private static CSSParser createParserForInput(CSSInputStream input) {
        CSSErrorListener errorListener = new CSSErrorListener();
        CSSLexer lexer = new CSSLexer((CharStream)input);
        lexer.init();
        lexer.removeErrorListeners();
        lexer.addErrorListener((ANTLRErrorListener)errorListener);
        CommonTokenStream tokens = new CommonTokenStream((TokenSource)lexer);
        CSSParser parser = new CSSParser((TokenStream)tokens);
        parser.removeErrorListeners();
        parser.addErrorListener((ANTLRErrorListener)errorListener);
        parser.setErrorHandler((ANTLRErrorStrategy)new CSSErrorStrategy());
        return parser;
    }

    private static CSSParserExtractor parse(CSSParser parser, SourceType type, Preparator preparator, List<MediaQuery> media) throws CSSException {
        CSSParserVisitorImpl visitor = new CSSParserVisitorImpl(preparator, media);
        switch (type) {
            case INLINE: {
                CSSParser.InlinestyleContext tree = parser.inlinestyle();
                visitor.visitInlinestyle(tree);
                break;
            }
            case EMBEDDED: {
                CSSParser.StylesheetContext tree = parser.stylesheet();
                visitor.visitStylesheet(tree);
                break;
            }
            case URL: {
                CSSParser.StylesheetContext tree = parser.stylesheet();
                visitor.visitStylesheet(tree);
                break;
            }
            default: {
                throw new RuntimeException("Coding error");
            }
        }
        return visitor;
    }

    public List<MediaQuery> parseMediaQuery(String query) {
        try {
            CSSInputStream input = CSSInputStream.stringStream(query);
            input.setBase(new URL("file://media/query/url"));
            CSSParser parser = CSSParserFactory.createParserForInput(input);
            CSSParserVisitorImpl visitor = new CSSParserVisitorImpl();
            return visitor.visitMedia(parser.media());
        }
        catch (IOException e) {
            log.error("I/O error during media query parsing: {}", (Object)e.getMessage());
            return null;
        }
        catch (RecognitionException e) {
            log.warn("Malformed media query {}", (Object)query);
            return null;
        }
    }

    protected static CSSException encapsulateException(Throwable t, String msg) {
        log.error("THROWN:", t);
        return new CSSException(msg, t);
    }

    protected static StyleSheet addRulesToStyleSheet(RuleList rules, StyleSheet sheet) {
        if (rules != null) {
            for (RuleBlock rule : rules) {
                sheet.add(rule);
            }
        }
        return sheet;
    }

    protected static CSSInputStream getInput(Object source, NetworkProcessor network, String encoding, SourceType type) throws IOException {
        switch (type) {
            case INLINE: 
            case EMBEDDED: {
                return CSSInputStream.stringStream((String)source);
            }
            case URL: {
                return CSSInputStream.urlStream((URL)source, network, encoding);
            }
        }
        throw new RuntimeException("Coding error");
    }

    public static enum SourceType {
        INLINE,
        EMBEDDED,
        URL;

    }
}

