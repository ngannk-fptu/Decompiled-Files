/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.w3c.css.sac.AttributeCondition
 *  org.w3c.css.sac.CSSException
 *  org.w3c.css.sac.CSSParseException
 *  org.w3c.css.sac.Condition
 *  org.w3c.css.sac.ConditionFactory
 *  org.w3c.css.sac.DocumentHandler
 *  org.w3c.css.sac.ElementSelector
 *  org.w3c.css.sac.ErrorHandler
 *  org.w3c.css.sac.InputSource
 *  org.w3c.css.sac.LexicalUnit
 *  org.w3c.css.sac.SACMediaList
 *  org.w3c.css.sac.Selector
 *  org.w3c.css.sac.SelectorFactory
 *  org.w3c.css.sac.SelectorList
 *  org.w3c.css.sac.SimpleSelector
 */
package org.apache.batik.css.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.StringTokenizer;
import org.apache.batik.css.parser.CSSLexicalUnit;
import org.apache.batik.css.parser.CSSSACMediaList;
import org.apache.batik.css.parser.CSSSelectorList;
import org.apache.batik.css.parser.DefaultConditionFactory;
import org.apache.batik.css.parser.DefaultDocumentHandler;
import org.apache.batik.css.parser.DefaultErrorHandler;
import org.apache.batik.css.parser.DefaultSelectorFactory;
import org.apache.batik.css.parser.ExtendedParser;
import org.apache.batik.css.parser.ParseException;
import org.apache.batik.css.parser.Scanner;
import org.apache.batik.css.parser.ScannerUtilities;
import org.apache.batik.i18n.Localizable;
import org.apache.batik.i18n.LocalizableSupport;
import org.apache.batik.util.ParsedURL;
import org.w3c.css.sac.AttributeCondition;
import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.CSSParseException;
import org.w3c.css.sac.Condition;
import org.w3c.css.sac.ConditionFactory;
import org.w3c.css.sac.DocumentHandler;
import org.w3c.css.sac.ElementSelector;
import org.w3c.css.sac.ErrorHandler;
import org.w3c.css.sac.InputSource;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.css.sac.SACMediaList;
import org.w3c.css.sac.Selector;
import org.w3c.css.sac.SelectorFactory;
import org.w3c.css.sac.SelectorList;
import org.w3c.css.sac.SimpleSelector;

public class Parser
implements ExtendedParser,
Localizable {
    public static final String BUNDLE_CLASSNAME = "org.apache.batik.css.parser.resources.Messages";
    protected LocalizableSupport localizableSupport = new LocalizableSupport("org.apache.batik.css.parser.resources.Messages", Parser.class.getClassLoader());
    protected Scanner scanner;
    protected int current;
    protected DocumentHandler documentHandler = DefaultDocumentHandler.INSTANCE;
    protected SelectorFactory selectorFactory = DefaultSelectorFactory.INSTANCE;
    protected ConditionFactory conditionFactory = DefaultConditionFactory.INSTANCE;
    protected ErrorHandler errorHandler = DefaultErrorHandler.INSTANCE;
    protected String pseudoElement;
    protected String documentURI;

    public String getParserVersion() {
        return "http://www.w3.org/TR/REC-CSS2";
    }

    @Override
    public void setLocale(Locale locale) throws CSSException {
        this.localizableSupport.setLocale(locale);
    }

    @Override
    public Locale getLocale() {
        return this.localizableSupport.getLocale();
    }

    @Override
    public String formatMessage(String key, Object[] args) throws MissingResourceException {
        return this.localizableSupport.formatMessage(key, args);
    }

    public void setDocumentHandler(DocumentHandler handler) {
        this.documentHandler = handler;
    }

    public void setSelectorFactory(SelectorFactory factory) {
        this.selectorFactory = factory;
    }

    public void setConditionFactory(ConditionFactory factory) {
        this.conditionFactory = factory;
    }

    public void setErrorHandler(ErrorHandler handler) {
        this.errorHandler = handler;
    }

    public void parseStyleSheet(InputSource source) throws CSSException, IOException {
        block18: {
            this.scanner = this.createScanner(source);
            try {
                this.documentHandler.startDocument(source);
                this.current = this.scanner.next();
                switch (this.current) {
                    case 30: {
                        if (this.nextIgnoreSpaces() != 19) {
                            this.reportError("charset.string");
                            break;
                        }
                        if (this.nextIgnoreSpaces() != 8) {
                            this.reportError("semicolon");
                        }
                        this.next();
                        break;
                    }
                    case 18: {
                        this.documentHandler.comment(this.scanner.getStringValue());
                    }
                }
                this.skipSpacesAndCDOCDC();
                while (this.current == 28) {
                    this.nextIgnoreSpaces();
                    this.parseImportRule();
                    this.nextIgnoreSpaces();
                }
                while (true) {
                    switch (this.current) {
                        case 33: {
                            this.nextIgnoreSpaces();
                            this.parsePageRule();
                            break;
                        }
                        case 32: {
                            this.nextIgnoreSpaces();
                            this.parseMediaRule();
                            break;
                        }
                        case 31: {
                            this.nextIgnoreSpaces();
                            this.parseFontFaceRule();
                            break;
                        }
                        case 29: {
                            this.nextIgnoreSpaces();
                            this.parseAtRule();
                            break;
                        }
                        case 0: {
                            break block18;
                        }
                        default: {
                            this.parseRuleSet();
                        }
                    }
                    this.skipSpacesAndCDOCDC();
                }
            }
            finally {
                this.documentHandler.endDocument(source);
                this.scanner.close();
                this.scanner = null;
            }
        }
    }

    public void parseStyleSheet(String uri) throws CSSException, IOException {
        this.parseStyleSheet(new InputSource(uri));
    }

    public void parseStyleDeclaration(InputSource source) throws CSSException, IOException {
        this.scanner = this.createScanner(source);
        this.parseStyleDeclarationInternal();
    }

    protected void parseStyleDeclarationInternal() throws CSSException, IOException {
        this.nextIgnoreSpaces();
        try {
            this.parseStyleDeclaration(false);
        }
        catch (CSSParseException e) {
            this.reportError(e);
        }
        finally {
            this.scanner.close();
            this.scanner = null;
        }
    }

    public void parseRule(InputSource source) throws CSSException, IOException {
        this.scanner = this.createScanner(source);
        this.parseRuleInternal();
    }

    protected void parseRuleInternal() throws CSSException, IOException {
        this.nextIgnoreSpaces();
        this.parseRule();
        this.scanner.close();
        this.scanner = null;
    }

    public SelectorList parseSelectors(InputSource source) throws CSSException, IOException {
        this.scanner = this.createScanner(source);
        return this.parseSelectorsInternal();
    }

    protected SelectorList parseSelectorsInternal() throws CSSException, IOException {
        this.nextIgnoreSpaces();
        SelectorList ret = this.parseSelectorList();
        this.scanner.close();
        this.scanner = null;
        return ret;
    }

    public LexicalUnit parsePropertyValue(InputSource source) throws CSSException, IOException {
        this.scanner = this.createScanner(source);
        return this.parsePropertyValueInternal();
    }

    protected LexicalUnit parsePropertyValueInternal() throws CSSException, IOException {
        this.nextIgnoreSpaces();
        LexicalUnit exp = null;
        try {
            exp = this.parseExpression(false);
        }
        catch (CSSParseException e) {
            this.reportError(e);
            throw e;
        }
        CSSParseException exception = null;
        if (this.current != 0) {
            exception = this.createCSSParseException("eof.expected");
        }
        this.scanner.close();
        this.scanner = null;
        if (exception != null) {
            this.errorHandler.fatalError(exception);
        }
        return exp;
    }

    public boolean parsePriority(InputSource source) throws CSSException, IOException {
        this.scanner = this.createScanner(source);
        return this.parsePriorityInternal();
    }

    protected boolean parsePriorityInternal() throws CSSException, IOException {
        this.nextIgnoreSpaces();
        this.scanner.close();
        this.scanner = null;
        switch (this.current) {
            case 0: {
                return false;
            }
            case 28: {
                return true;
            }
        }
        this.reportError("token", new Object[]{this.current});
        return false;
    }

    protected void parseRule() {
        switch (this.scanner.getType()) {
            case 28: {
                this.nextIgnoreSpaces();
                this.parseImportRule();
                break;
            }
            case 29: {
                this.nextIgnoreSpaces();
                this.parseAtRule();
                break;
            }
            case 31: {
                this.nextIgnoreSpaces();
                this.parseFontFaceRule();
                break;
            }
            case 32: {
                this.nextIgnoreSpaces();
                this.parseMediaRule();
                break;
            }
            case 33: {
                this.nextIgnoreSpaces();
                this.parsePageRule();
                break;
            }
            default: {
                this.parseRuleSet();
            }
        }
    }

    protected void parseAtRule() {
        this.scanner.scanAtRule();
        this.documentHandler.ignorableAtRule(this.scanner.getStringValue());
        this.nextIgnoreSpaces();
    }

    protected void parseImportRule() {
        CSSSACMediaList ml;
        String uri = null;
        switch (this.current) {
            default: {
                this.reportError("string.or.uri");
                return;
            }
            case 19: 
            case 51: 
        }
        uri = this.scanner.getStringValue();
        this.nextIgnoreSpaces();
        if (this.current != 20) {
            ml = new CSSSACMediaList();
            ml.append("all");
        } else {
            ml = this.parseMediaList();
        }
        this.documentHandler.importStyle(uri, (SACMediaList)ml, null);
        if (this.current != 8) {
            this.reportError("semicolon");
        } else {
            this.next();
        }
    }

    protected CSSSACMediaList parseMediaList() {
        CSSSACMediaList result = new CSSSACMediaList();
        result.append(this.scanner.getStringValue());
        this.nextIgnoreSpaces();
        block3: while (this.current == 6) {
            this.nextIgnoreSpaces();
            switch (this.current) {
                default: {
                    this.reportError("identifier");
                    continue block3;
                }
                case 20: 
            }
            result.append(this.scanner.getStringValue());
            this.nextIgnoreSpaces();
        }
        return result;
    }

    protected void parseFontFaceRule() {
        try {
            this.documentHandler.startFontFace();
            if (this.current != 1) {
                this.reportError("left.curly.brace");
            } else {
                this.nextIgnoreSpaces();
                try {
                    this.parseStyleDeclaration(true);
                }
                catch (CSSParseException e) {
                    this.reportError(e);
                }
            }
        }
        finally {
            this.documentHandler.endFontFace();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void parsePageRule() {
        String page = null;
        String ppage = null;
        if (this.current == 20) {
            page = this.scanner.getStringValue();
            this.nextIgnoreSpaces();
            if (this.current == 16) {
                this.nextIgnoreSpaces();
                if (this.current != 20) {
                    this.reportError("identifier");
                    return;
                }
                ppage = this.scanner.getStringValue();
                this.nextIgnoreSpaces();
            }
        }
        try {
            this.documentHandler.startPage(page, ppage);
            if (this.current != 1) {
                this.reportError("left.curly.brace");
            } else {
                this.nextIgnoreSpaces();
                try {
                    this.parseStyleDeclaration(true);
                }
                catch (CSSParseException e) {
                    this.reportError(e);
                }
            }
        }
        finally {
            this.documentHandler.endPage(page, ppage);
        }
    }

    protected void parseMediaRule() {
        if (this.current != 20) {
            this.reportError("identifier");
            return;
        }
        CSSSACMediaList ml = this.parseMediaList();
        try {
            this.documentHandler.startMedia((SACMediaList)ml);
            if (this.current != 1) {
                this.reportError("left.curly.brace");
            } else {
                this.nextIgnoreSpaces();
                block6: while (true) {
                    switch (this.current) {
                        case 0: 
                        case 2: {
                            break block6;
                        }
                        default: {
                            this.parseRuleSet();
                            continue block6;
                        }
                    }
                    break;
                }
                this.nextIgnoreSpaces();
            }
        }
        finally {
            this.documentHandler.endMedia((SACMediaList)ml);
        }
    }

    protected void parseRuleSet() {
        SelectorList sl = null;
        try {
            sl = this.parseSelectorList();
        }
        catch (CSSParseException e) {
            this.reportError(e);
            return;
        }
        try {
            this.documentHandler.startSelector(sl);
            if (this.current != 1) {
                this.reportError("left.curly.brace");
                if (this.current == 2) {
                    this.nextIgnoreSpaces();
                }
            } else {
                this.nextIgnoreSpaces();
                try {
                    this.parseStyleDeclaration(true);
                }
                catch (CSSParseException e) {
                    this.reportError(e);
                }
            }
        }
        finally {
            this.documentHandler.endSelector(sl);
        }
    }

    protected SelectorList parseSelectorList() {
        CSSSelectorList result = new CSSSelectorList();
        result.append(this.parseSelector());
        while (this.current == 6) {
            this.nextIgnoreSpaces();
            result.append(this.parseSelector());
        }
        return result;
    }

    protected Selector parseSelector() {
        this.pseudoElement = null;
        SimpleSelector result = this.parseSimpleSelector();
        block5: while (true) {
            switch (this.current) {
                default: {
                    break block5;
                }
                case 7: 
                case 11: 
                case 13: 
                case 16: 
                case 20: 
                case 27: {
                    if (this.pseudoElement != null) {
                        throw this.createCSSParseException("pseudo.element.position");
                    }
                    result = this.selectorFactory.createDescendantSelector((Selector)result, this.parseSimpleSelector());
                    continue block5;
                }
                case 4: {
                    if (this.pseudoElement != null) {
                        throw this.createCSSParseException("pseudo.element.position");
                    }
                    this.nextIgnoreSpaces();
                    result = this.selectorFactory.createDirectAdjacentSelector((short)1, (Selector)result, this.parseSimpleSelector());
                    continue block5;
                }
                case 9: {
                    if (this.pseudoElement != null) {
                        throw this.createCSSParseException("pseudo.element.position");
                    }
                    this.nextIgnoreSpaces();
                    result = this.selectorFactory.createChildSelector((Selector)result, this.parseSimpleSelector());
                    continue block5;
                }
            }
            break;
        }
        if (this.pseudoElement != null) {
            result = this.selectorFactory.createChildSelector((Selector)result, (SimpleSelector)this.selectorFactory.createPseudoElementSelector(null, this.pseudoElement));
        }
        return result;
    }

    protected SimpleSelector parseSimpleSelector() {
        ElementSelector result;
        switch (this.current) {
            case 20: {
                result = this.selectorFactory.createElementSelector(null, this.scanner.getStringValue());
                this.next();
                break;
            }
            case 13: {
                this.next();
            }
            default: {
                result = this.selectorFactory.createElementSelector(null, null);
            }
        }
        AttributeCondition cond = null;
        block25: while (true) {
            AttributeCondition c = null;
            block4 : switch (this.current) {
                case 27: {
                    c = this.conditionFactory.createIdCondition(this.scanner.getStringValue());
                    this.next();
                    break;
                }
                case 7: {
                    if (this.next() != 20) {
                        throw this.createCSSParseException("identifier");
                    }
                    c = this.conditionFactory.createClassCondition(null, this.scanner.getStringValue());
                    this.next();
                    break;
                }
                case 11: {
                    if (this.nextIgnoreSpaces() != 20) {
                        throw this.createCSSParseException("identifier");
                    }
                    String name = this.scanner.getStringValue();
                    int op = this.nextIgnoreSpaces();
                    switch (op) {
                        default: {
                            throw this.createCSSParseException("right.bracket");
                        }
                        case 12: {
                            this.next();
                            c = this.conditionFactory.createAttributeCondition(name, null, false, null);
                            break block4;
                        }
                        case 3: 
                        case 25: 
                        case 26: 
                    }
                    String val = null;
                    switch (this.nextIgnoreSpaces()) {
                        default: {
                            throw this.createCSSParseException("identifier.or.string");
                        }
                        case 19: 
                        case 20: 
                    }
                    val = this.scanner.getStringValue();
                    this.nextIgnoreSpaces();
                    if (this.current != 12) {
                        throw this.createCSSParseException("right.bracket");
                    }
                    this.next();
                    switch (op) {
                        case 3: {
                            c = this.conditionFactory.createAttributeCondition(name, null, false, val);
                            break block4;
                        }
                        case 26: {
                            c = this.conditionFactory.createOneOfAttributeCondition(name, null, false, val);
                            break block4;
                        }
                    }
                    c = this.conditionFactory.createBeginHyphenAttributeCondition(name, null, false, val);
                    break;
                }
                case 16: {
                    String val;
                    switch (this.nextIgnoreSpaces()) {
                        case 20: {
                            val = this.scanner.getStringValue();
                            if (this.isPseudoElement(val)) {
                                if (this.pseudoElement != null) {
                                    throw this.createCSSParseException("duplicate.pseudo.element");
                                }
                                this.pseudoElement = val;
                            } else {
                                c = this.conditionFactory.createPseudoClassCondition(null, val);
                            }
                            this.next();
                            break block4;
                        }
                        case 52: {
                            String func = this.scanner.getStringValue();
                            if (this.nextIgnoreSpaces() != 20) {
                                throw this.createCSSParseException("identifier");
                            }
                            String lang = this.scanner.getStringValue();
                            if (this.nextIgnoreSpaces() != 15) {
                                throw this.createCSSParseException("right.brace");
                            }
                            if (!func.equalsIgnoreCase("lang")) {
                                throw this.createCSSParseException("pseudo.function");
                            }
                            c = this.conditionFactory.createLangCondition(lang);
                            this.next();
                            break block4;
                        }
                    }
                    throw this.createCSSParseException("identifier");
                }
                default: {
                    break block25;
                }
            }
            if (c == null) continue;
            if (cond == null) {
                cond = c;
                continue;
            }
            cond = this.conditionFactory.createAndCondition((Condition)cond, (Condition)c);
        }
        this.skipSpaces();
        if (cond != null) {
            result = this.selectorFactory.createConditionalSelector((SimpleSelector)result, cond);
        }
        return result;
    }

    protected boolean isPseudoElement(String s) {
        switch (s.charAt(0)) {
            case 'A': 
            case 'a': {
                return s.equalsIgnoreCase("after");
            }
            case 'B': 
            case 'b': {
                return s.equalsIgnoreCase("before");
            }
            case 'F': 
            case 'f': {
                return s.equalsIgnoreCase("first-letter") || s.equalsIgnoreCase("first-line");
            }
        }
        return false;
    }

    protected void parseStyleDeclaration(boolean inSheet) throws CSSException {
        block8: while (true) {
            switch (this.current) {
                case 0: {
                    if (inSheet) {
                        throw this.createCSSParseException("eof");
                    }
                    return;
                }
                case 2: {
                    if (!inSheet) {
                        throw this.createCSSParseException("eof.expected");
                    }
                    this.nextIgnoreSpaces();
                    return;
                }
                case 8: {
                    this.nextIgnoreSpaces();
                    continue block8;
                }
                default: {
                    throw this.createCSSParseException("identifier");
                }
                case 20: 
            }
            String name = this.scanner.getStringValue();
            if (this.nextIgnoreSpaces() != 16) {
                throw this.createCSSParseException("colon");
            }
            this.nextIgnoreSpaces();
            LexicalUnit exp = null;
            try {
                exp = this.parseExpression(false);
            }
            catch (CSSParseException e) {
                this.reportError(e);
            }
            if (exp == null) continue;
            boolean important = false;
            if (this.current == 23) {
                important = true;
                this.nextIgnoreSpaces();
            }
            this.documentHandler.property(name, exp, important);
        }
    }

    protected LexicalUnit parseExpression(boolean param) {
        LexicalUnit result;
        LexicalUnit curr = result = this.parseTerm(null);
        while (true) {
            boolean op = false;
            switch (this.current) {
                case 6: {
                    op = true;
                    curr = CSSLexicalUnit.createSimple((short)0, curr);
                    this.nextIgnoreSpaces();
                    break;
                }
                case 10: {
                    op = true;
                    curr = CSSLexicalUnit.createSimple((short)4, curr);
                    this.nextIgnoreSpaces();
                }
            }
            if (param) {
                if (this.current == 15) {
                    if (op) {
                        throw this.createCSSParseException("token", new Object[]{this.current});
                    }
                    return result;
                }
                curr = this.parseTerm(curr);
                continue;
            }
            switch (this.current) {
                case 0: 
                case 2: 
                case 8: 
                case 23: {
                    if (op) {
                        throw this.createCSSParseException("token", new Object[]{this.current});
                    }
                    return result;
                }
            }
            curr = this.parseTerm(curr);
        }
    }

    protected LexicalUnit parseTerm(LexicalUnit prev) {
        boolean plus = true;
        boolean sgn = false;
        switch (this.current) {
            case 5: {
                plus = false;
            }
            case 4: {
                this.next();
                sgn = true;
            }
        }
        switch (this.current) {
            case 24: {
                long lVal;
                String sval = this.scanner.getStringValue();
                if (!plus) {
                    sval = "-" + sval;
                }
                if ((lVal = Long.parseLong(sval)) >= Integer.MIN_VALUE && lVal <= Integer.MAX_VALUE) {
                    int iVal = (int)lVal;
                    this.nextIgnoreSpaces();
                    return CSSLexicalUnit.createInteger(iVal, prev);
                }
            }
            case 54: {
                return CSSLexicalUnit.createFloat((short)14, this.number(plus), prev);
            }
            case 42: {
                return CSSLexicalUnit.createFloat((short)23, this.number(plus), prev);
            }
            case 45: {
                return CSSLexicalUnit.createFloat((short)21, this.number(plus), prev);
            }
            case 44: {
                return CSSLexicalUnit.createFloat((short)22, this.number(plus), prev);
            }
            case 46: {
                return CSSLexicalUnit.createFloat((short)17, this.number(plus), prev);
            }
            case 37: {
                return CSSLexicalUnit.createFloat((short)19, this.number(plus), prev);
            }
            case 38: {
                return CSSLexicalUnit.createFloat((short)20, this.number(plus), prev);
            }
            case 39: {
                return CSSLexicalUnit.createFloat((short)18, this.number(plus), prev);
            }
            case 36: {
                return CSSLexicalUnit.createFloat((short)15, this.number(plus), prev);
            }
            case 35: {
                return CSSLexicalUnit.createFloat((short)16, this.number(plus), prev);
            }
            case 47: {
                return CSSLexicalUnit.createFloat((short)28, this.number(plus), prev);
            }
            case 48: {
                return CSSLexicalUnit.createFloat((short)30, this.number(plus), prev);
            }
            case 49: {
                return CSSLexicalUnit.createFloat((short)29, this.number(plus), prev);
            }
            case 43: {
                return CSSLexicalUnit.createFloat((short)32, this.number(plus), prev);
            }
            case 40: {
                return CSSLexicalUnit.createFloat((short)31, this.number(plus), prev);
            }
            case 41: {
                return CSSLexicalUnit.createFloat((short)33, this.number(plus), prev);
            }
            case 50: {
                return CSSLexicalUnit.createFloat((short)34, this.number(plus), prev);
            }
            case 34: {
                return this.dimension(plus, prev);
            }
            case 52: {
                return this.parseFunction(plus, prev);
            }
        }
        if (sgn) {
            throw this.createCSSParseException("token", new Object[]{this.current});
        }
        switch (this.current) {
            case 19: {
                String val = this.scanner.getStringValue();
                this.nextIgnoreSpaces();
                return CSSLexicalUnit.createString((short)36, val, prev);
            }
            case 20: {
                String val = this.scanner.getStringValue();
                this.nextIgnoreSpaces();
                if (val.equalsIgnoreCase("inherit")) {
                    return CSSLexicalUnit.createSimple((short)12, prev);
                }
                return CSSLexicalUnit.createString((short)35, val, prev);
            }
            case 51: {
                String val = this.scanner.getStringValue();
                this.nextIgnoreSpaces();
                return CSSLexicalUnit.createString((short)24, val, prev);
            }
            case 27: {
                return this.hexcolor(prev);
            }
        }
        throw this.createCSSParseException("token", new Object[]{this.current});
    }

    protected LexicalUnit parseFunction(boolean positive, LexicalUnit prev) {
        String name = this.scanner.getStringValue();
        this.nextIgnoreSpaces();
        LexicalUnit params = this.parseExpression(true);
        if (this.current != 15) {
            throw this.createCSSParseException("token", new Object[]{this.current});
        }
        this.nextIgnoreSpaces();
        block0 : switch (name.charAt(0)) {
            case 'R': 
            case 'r': {
                LexicalUnit lu;
                if (name.equalsIgnoreCase("rgb")) {
                    LexicalUnit lu2 = params;
                    if (lu2 == null) break;
                    switch (lu2.getLexicalUnitType()) {
                        default: {
                            break block0;
                        }
                        case 13: 
                        case 23: 
                    }
                    lu2 = lu2.getNextLexicalUnit();
                    if (lu2 == null) break;
                    switch (lu2.getLexicalUnitType()) {
                        default: {
                            break block0;
                        }
                        case 0: 
                    }
                    lu2 = lu2.getNextLexicalUnit();
                    if (lu2 == null) break;
                    switch (lu2.getLexicalUnitType()) {
                        default: {
                            break block0;
                        }
                        case 13: 
                        case 23: 
                    }
                    lu2 = lu2.getNextLexicalUnit();
                    if (lu2 == null) break;
                    switch (lu2.getLexicalUnitType()) {
                        default: {
                            break block0;
                        }
                        case 0: 
                    }
                    lu2 = lu2.getNextLexicalUnit();
                    if (lu2 == null) break;
                    switch (lu2.getLexicalUnitType()) {
                        default: {
                            break block0;
                        }
                        case 13: 
                        case 23: 
                    }
                    lu2 = lu2.getNextLexicalUnit();
                    if (lu2 != null) break;
                    return CSSLexicalUnit.createPredefinedFunction((short)27, params, prev);
                }
                if (!name.equalsIgnoreCase("rect") || (lu = params) == null) break;
                switch (lu.getLexicalUnitType()) {
                    default: {
                        break block0;
                    }
                    case 13: {
                        if (lu.getIntegerValue() != 0) break block0;
                        lu = lu.getNextLexicalUnit();
                        break;
                    }
                    case 35: {
                        if (!lu.getStringValue().equalsIgnoreCase("auto")) break block0;
                        lu = lu.getNextLexicalUnit();
                        break;
                    }
                    case 15: 
                    case 16: 
                    case 17: 
                    case 18: 
                    case 19: 
                    case 20: 
                    case 21: 
                    case 22: 
                    case 23: {
                        lu = lu.getNextLexicalUnit();
                    }
                }
                if (lu == null) break;
                switch (lu.getLexicalUnitType()) {
                    default: {
                        break block0;
                    }
                    case 0: 
                }
                lu = lu.getNextLexicalUnit();
                if (lu == null) break;
                switch (lu.getLexicalUnitType()) {
                    default: {
                        break block0;
                    }
                    case 13: {
                        if (lu.getIntegerValue() != 0) break block0;
                        lu = lu.getNextLexicalUnit();
                        break;
                    }
                    case 35: {
                        if (!lu.getStringValue().equalsIgnoreCase("auto")) break block0;
                        lu = lu.getNextLexicalUnit();
                        break;
                    }
                    case 15: 
                    case 16: 
                    case 17: 
                    case 18: 
                    case 19: 
                    case 20: 
                    case 21: 
                    case 22: 
                    case 23: {
                        lu = lu.getNextLexicalUnit();
                    }
                }
                if (lu == null) break;
                switch (lu.getLexicalUnitType()) {
                    default: {
                        break block0;
                    }
                    case 0: 
                }
                lu = lu.getNextLexicalUnit();
                if (lu == null) break;
                switch (lu.getLexicalUnitType()) {
                    default: {
                        break block0;
                    }
                    case 13: {
                        if (lu.getIntegerValue() != 0) break block0;
                        lu = lu.getNextLexicalUnit();
                        break;
                    }
                    case 35: {
                        if (!lu.getStringValue().equalsIgnoreCase("auto")) break block0;
                        lu = lu.getNextLexicalUnit();
                        break;
                    }
                    case 15: 
                    case 16: 
                    case 17: 
                    case 18: 
                    case 19: 
                    case 20: 
                    case 21: 
                    case 22: 
                    case 23: {
                        lu = lu.getNextLexicalUnit();
                    }
                }
                if (lu == null) break;
                switch (lu.getLexicalUnitType()) {
                    default: {
                        break block0;
                    }
                    case 0: 
                }
                lu = lu.getNextLexicalUnit();
                if (lu == null) break;
                switch (lu.getLexicalUnitType()) {
                    default: {
                        break block0;
                    }
                    case 13: {
                        if (lu.getIntegerValue() != 0) break block0;
                        lu = lu.getNextLexicalUnit();
                        break;
                    }
                    case 35: {
                        if (!lu.getStringValue().equalsIgnoreCase("auto")) break block0;
                        lu = lu.getNextLexicalUnit();
                        break;
                    }
                    case 15: 
                    case 16: 
                    case 17: 
                    case 18: 
                    case 19: 
                    case 20: 
                    case 21: 
                    case 22: 
                    case 23: {
                        lu = lu.getNextLexicalUnit();
                    }
                }
                if (lu != null) break;
                return CSSLexicalUnit.createPredefinedFunction((short)38, params, prev);
            }
            case 'C': 
            case 'c': {
                LexicalUnit lu;
                if (name.equalsIgnoreCase("counter")) {
                    LexicalUnit lu3 = params;
                    if (lu3 == null) break;
                    switch (lu3.getLexicalUnitType()) {
                        default: {
                            break block0;
                        }
                        case 35: 
                    }
                    lu3 = lu3.getNextLexicalUnit();
                    if (lu3 == null) break;
                    switch (lu3.getLexicalUnitType()) {
                        default: {
                            break block0;
                        }
                        case 0: 
                    }
                    lu3 = lu3.getNextLexicalUnit();
                    if (lu3 == null) break;
                    switch (lu3.getLexicalUnitType()) {
                        default: {
                            break block0;
                        }
                        case 35: 
                    }
                    lu3 = lu3.getNextLexicalUnit();
                    if (lu3 != null) break;
                    return CSSLexicalUnit.createPredefinedFunction((short)25, params, prev);
                }
                if (!name.equalsIgnoreCase("counters") || (lu = params) == null) break;
                switch (lu.getLexicalUnitType()) {
                    default: {
                        break block0;
                    }
                    case 35: 
                }
                lu = lu.getNextLexicalUnit();
                if (lu == null) break;
                switch (lu.getLexicalUnitType()) {
                    default: {
                        break block0;
                    }
                    case 0: 
                }
                lu = lu.getNextLexicalUnit();
                if (lu == null) break;
                switch (lu.getLexicalUnitType()) {
                    default: {
                        break block0;
                    }
                    case 36: 
                }
                lu = lu.getNextLexicalUnit();
                if (lu == null) break;
                switch (lu.getLexicalUnitType()) {
                    default: {
                        break block0;
                    }
                    case 0: 
                }
                lu = lu.getNextLexicalUnit();
                if (lu == null) break;
                switch (lu.getLexicalUnitType()) {
                    default: {
                        break block0;
                    }
                    case 35: 
                }
                lu = lu.getNextLexicalUnit();
                if (lu != null) break;
                return CSSLexicalUnit.createPredefinedFunction((short)26, params, prev);
            }
            case 'A': 
            case 'a': {
                LexicalUnit lu;
                if (!name.equalsIgnoreCase("attr") || (lu = params) == null) break;
                switch (lu.getLexicalUnitType()) {
                    default: {
                        break block0;
                    }
                    case 35: 
                }
                lu = lu.getNextLexicalUnit();
                if (lu != null) break;
                return CSSLexicalUnit.createString((short)37, params.getStringValue(), prev);
            }
        }
        return CSSLexicalUnit.createFunction(name, params, prev);
    }

    protected LexicalUnit hexcolor(LexicalUnit prev) {
        String val = this.scanner.getStringValue();
        int len = val.length();
        CSSLexicalUnit params = null;
        switch (len) {
            case 3: {
                char rc = Character.toLowerCase(val.charAt(0));
                char gc = Character.toLowerCase(val.charAt(1));
                char bc = Character.toLowerCase(val.charAt(2));
                if (!(ScannerUtilities.isCSSHexadecimalCharacter(rc) && ScannerUtilities.isCSSHexadecimalCharacter(gc) && ScannerUtilities.isCSSHexadecimalCharacter(bc))) {
                    throw this.createCSSParseException("rgb.color", new Object[]{val});
                }
                int t = rc >= '0' && rc <= '9' ? rc - 48 : rc - 97 + 10;
                int r = t;
                r |= (t <<= 4);
                t = gc >= '0' && gc <= '9' ? gc - 48 : gc - 97 + 10;
                int g = t;
                t = bc >= '0' && bc <= '9' ? bc - 48 : bc - 97 + 10;
                int b = t;
                params = CSSLexicalUnit.createInteger(r, null);
                CSSLexicalUnit tmp = CSSLexicalUnit.createSimple((short)0, params);
                tmp = CSSLexicalUnit.createInteger(g |= (t <<= 4), tmp);
                tmp = CSSLexicalUnit.createSimple((short)0, tmp);
                tmp = CSSLexicalUnit.createInteger(b |= (t <<= 4), tmp);
                break;
            }
            case 6: {
                char rc1 = Character.toLowerCase(val.charAt(0));
                char rc2 = Character.toLowerCase(val.charAt(1));
                char gc1 = Character.toLowerCase(val.charAt(2));
                char gc2 = Character.toLowerCase(val.charAt(3));
                char bc1 = Character.toLowerCase(val.charAt(4));
                char bc2 = Character.toLowerCase(val.charAt(5));
                if (!(ScannerUtilities.isCSSHexadecimalCharacter(rc1) && ScannerUtilities.isCSSHexadecimalCharacter(rc2) && ScannerUtilities.isCSSHexadecimalCharacter(gc1) && ScannerUtilities.isCSSHexadecimalCharacter(gc2) && ScannerUtilities.isCSSHexadecimalCharacter(bc1) && ScannerUtilities.isCSSHexadecimalCharacter(bc2))) {
                    throw this.createCSSParseException("rgb.color");
                }
                int r = rc1 >= '0' && rc1 <= '9' ? rc1 - 48 : rc1 - 97 + 10;
                r <<= 4;
                r |= rc2 >= '0' && rc2 <= '9' ? rc2 - 48 : rc2 - 97 + 10;
                int g = gc1 >= '0' && gc1 <= '9' ? gc1 - 48 : gc1 - 97 + 10;
                g <<= 4;
                g |= gc2 >= '0' && gc2 <= '9' ? gc2 - 48 : gc2 - 97 + 10;
                int b = bc1 >= '0' && bc1 <= '9' ? bc1 - 48 : bc1 - 97 + 10;
                b <<= 4;
                int n = bc2 >= '0' && bc2 <= '9' ? bc2 - 48 : bc2 - 97 + 10;
                params = CSSLexicalUnit.createInteger(r, null);
                CSSLexicalUnit tmp = CSSLexicalUnit.createSimple((short)0, params);
                tmp = CSSLexicalUnit.createInteger(g, tmp);
                tmp = CSSLexicalUnit.createSimple((short)0, tmp);
                tmp = CSSLexicalUnit.createInteger(b |= n, tmp);
                break;
            }
            default: {
                throw this.createCSSParseException("rgb.color", new Object[]{val});
            }
        }
        this.nextIgnoreSpaces();
        return CSSLexicalUnit.createPredefinedFunction((short)27, params, prev);
    }

    protected Scanner createScanner(InputSource source) {
        Reader r;
        this.documentURI = source.getURI();
        if (this.documentURI == null) {
            this.documentURI = "";
        }
        if ((r = source.getCharacterStream()) != null) {
            return new Scanner(r);
        }
        InputStream is = source.getByteStream();
        if (is != null) {
            return new Scanner(is, source.getEncoding());
        }
        String uri = source.getURI();
        if (uri == null) {
            throw new CSSException(this.formatMessage("empty.source", null));
        }
        try {
            ParsedURL purl = new ParsedURL(uri);
            is = purl.openStreamRaw("text/css");
            return new Scanner(is, source.getEncoding());
        }
        catch (IOException e) {
            throw new CSSException((Exception)e);
        }
    }

    protected int skipSpaces() {
        int lex = this.scanner.getType();
        while (lex == 17) {
            lex = this.next();
        }
        return lex;
    }

    protected int skipSpacesAndCDOCDC() {
        block3: while (true) {
            switch (this.current) {
                default: {
                    break block3;
                }
                case 17: 
                case 18: 
                case 21: 
                case 22: {
                    this.scanner.clearBuffer();
                    this.next();
                    continue block3;
                }
            }
            break;
        }
        return this.current;
    }

    protected float number(boolean positive) {
        try {
            float sgn = positive ? 1.0f : -1.0f;
            String val = this.scanner.getStringValue();
            this.nextIgnoreSpaces();
            return sgn * Float.parseFloat(val);
        }
        catch (NumberFormatException e) {
            throw this.createCSSParseException("number.format");
        }
    }

    protected LexicalUnit dimension(boolean positive, LexicalUnit prev) {
        try {
            int i;
            float sgn = positive ? 1.0f : -1.0f;
            String val = this.scanner.getStringValue();
            block5: for (i = 0; i < val.length(); ++i) {
                switch (val.charAt(i)) {
                    default: {
                        break block5;
                    }
                    case '.': 
                    case '0': 
                    case '1': 
                    case '2': 
                    case '3': 
                    case '4': 
                    case '5': 
                    case '6': 
                    case '7': 
                    case '8': 
                    case '9': {
                        continue block5;
                    }
                }
            }
            this.nextIgnoreSpaces();
            return CSSLexicalUnit.createDimension(sgn * Float.parseFloat(val.substring(0, i)), val.substring(i), prev);
        }
        catch (NumberFormatException e) {
            throw this.createCSSParseException("number.format");
        }
    }

    protected int next() {
        try {
            while (true) {
                this.scanner.clearBuffer();
                this.current = this.scanner.next();
                if (this.current != 18) break;
                this.documentHandler.comment(this.scanner.getStringValue());
            }
            return this.current;
        }
        catch (ParseException e) {
            this.reportError(e.getMessage());
            return this.current;
        }
    }

    protected int nextIgnoreSpaces() {
        try {
            block6: while (true) {
                this.scanner.clearBuffer();
                this.current = this.scanner.next();
                switch (this.current) {
                    case 18: {
                        this.documentHandler.comment(this.scanner.getStringValue());
                        continue block6;
                    }
                    default: {
                        break block6;
                    }
                    case 17: {
                        continue block6;
                    }
                }
                break;
            }
            return this.current;
        }
        catch (ParseException e) {
            this.errorHandler.error(this.createCSSParseException(e.getMessage()));
            return this.current;
        }
    }

    protected void reportError(String key) {
        this.reportError(key, null);
    }

    protected void reportError(String key, Object[] params) {
        this.reportError(this.createCSSParseException(key, params));
    }

    protected void reportError(CSSParseException e) {
        this.errorHandler.error(e);
        int cbraces = 1;
        while (true) {
            switch (this.current) {
                case 0: {
                    return;
                }
                case 2: 
                case 8: {
                    if (--cbraces == 0) {
                        this.nextIgnoreSpaces();
                        return;
                    }
                }
                case 1: {
                    ++cbraces;
                }
            }
            this.nextIgnoreSpaces();
        }
    }

    protected CSSParseException createCSSParseException(String key) {
        return this.createCSSParseException(key, null);
    }

    protected CSSParseException createCSSParseException(String key, Object[] params) {
        return new CSSParseException(this.formatMessage(key, params), this.documentURI, this.scanner.getLine(), this.scanner.getColumn());
    }

    @Override
    public void parseStyleDeclaration(String source) throws CSSException, IOException {
        this.scanner = new Scanner(source);
        this.parseStyleDeclarationInternal();
    }

    @Override
    public void parseRule(String source) throws CSSException, IOException {
        this.scanner = new Scanner(source);
        this.parseRuleInternal();
    }

    @Override
    public SelectorList parseSelectors(String source) throws CSSException, IOException {
        this.scanner = new Scanner(source);
        return this.parseSelectorsInternal();
    }

    @Override
    public LexicalUnit parsePropertyValue(String source) throws CSSException, IOException {
        this.scanner = new Scanner(source);
        return this.parsePropertyValueInternal();
    }

    @Override
    public boolean parsePriority(String source) throws CSSException, IOException {
        this.scanner = new Scanner(source);
        return this.parsePriorityInternal();
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
}

