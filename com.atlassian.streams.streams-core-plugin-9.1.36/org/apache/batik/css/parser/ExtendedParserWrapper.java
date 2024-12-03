/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.css.parser;

import java.io.IOException;
import java.io.StringReader;
import java.util.Locale;
import java.util.StringTokenizer;
import org.apache.batik.css.parser.CSSSACMediaList;
import org.apache.batik.css.parser.ExtendedParser;
import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.ConditionFactory;
import org.w3c.css.sac.DocumentHandler;
import org.w3c.css.sac.ErrorHandler;
import org.w3c.css.sac.InputSource;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.css.sac.Parser;
import org.w3c.css.sac.SACMediaList;
import org.w3c.css.sac.SelectorFactory;
import org.w3c.css.sac.SelectorList;

public class ExtendedParserWrapper
implements ExtendedParser {
    public Parser parser;

    public static ExtendedParser wrap(Parser p) {
        if (p instanceof ExtendedParser) {
            return (ExtendedParser)p;
        }
        return new ExtendedParserWrapper(p);
    }

    public ExtendedParserWrapper(Parser parser) {
        this.parser = parser;
    }

    @Override
    public String getParserVersion() {
        return this.parser.getParserVersion();
    }

    @Override
    public void setLocale(Locale locale) throws CSSException {
        this.parser.setLocale(locale);
    }

    @Override
    public void setDocumentHandler(DocumentHandler handler) {
        this.parser.setDocumentHandler(handler);
    }

    @Override
    public void setSelectorFactory(SelectorFactory selectorFactory) {
        this.parser.setSelectorFactory(selectorFactory);
    }

    @Override
    public void setConditionFactory(ConditionFactory conditionFactory) {
        this.parser.setConditionFactory(conditionFactory);
    }

    @Override
    public void setErrorHandler(ErrorHandler handler) {
        this.parser.setErrorHandler(handler);
    }

    @Override
    public void parseStyleSheet(InputSource source) throws CSSException, IOException {
        this.parser.parseStyleSheet(source);
    }

    @Override
    public void parseStyleSheet(String uri) throws CSSException, IOException {
        this.parser.parseStyleSheet(uri);
    }

    @Override
    public void parseStyleDeclaration(InputSource source) throws CSSException, IOException {
        this.parser.parseStyleDeclaration(source);
    }

    @Override
    public void parseStyleDeclaration(String source) throws CSSException, IOException {
        this.parser.parseStyleDeclaration(new InputSource(new StringReader(source)));
    }

    @Override
    public void parseRule(InputSource source) throws CSSException, IOException {
        this.parser.parseRule(source);
    }

    @Override
    public void parseRule(String source) throws CSSException, IOException {
        this.parser.parseRule(new InputSource(new StringReader(source)));
    }

    @Override
    public SelectorList parseSelectors(InputSource source) throws CSSException, IOException {
        return this.parser.parseSelectors(source);
    }

    @Override
    public SelectorList parseSelectors(String source) throws CSSException, IOException {
        return this.parser.parseSelectors(new InputSource(new StringReader(source)));
    }

    @Override
    public LexicalUnit parsePropertyValue(InputSource source) throws CSSException, IOException {
        return this.parser.parsePropertyValue(source);
    }

    @Override
    public LexicalUnit parsePropertyValue(String source) throws CSSException, IOException {
        return this.parser.parsePropertyValue(new InputSource(new StringReader(source)));
    }

    @Override
    public boolean parsePriority(InputSource source) throws CSSException, IOException {
        return this.parser.parsePriority(source);
    }

    @Override
    public SACMediaList parseMedia(String mediaText) throws CSSException, IOException {
        CSSSACMediaList result = new CSSSACMediaList();
        if (!"all".equalsIgnoreCase(mediaText)) {
            StringTokenizer st = new StringTokenizer(mediaText, " ,");
            while (st.hasMoreTokens()) {
                result.append(st.nextToken());
            }
        }
        return result;
    }

    @Override
    public boolean parsePriority(String source) throws CSSException, IOException {
        return this.parser.parsePriority(new InputSource(new StringReader(source)));
    }
}

