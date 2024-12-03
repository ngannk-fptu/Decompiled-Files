/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.css.parser;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.xhtmlrenderer.css.constants.CSSName;
import org.xhtmlrenderer.css.constants.MarginBoxName;
import org.xhtmlrenderer.css.newmatch.Selector;
import org.xhtmlrenderer.css.parser.CSSErrorHandler;
import org.xhtmlrenderer.css.parser.CSSParseException;
import org.xhtmlrenderer.css.parser.FSCMYKColor;
import org.xhtmlrenderer.css.parser.FSFunction;
import org.xhtmlrenderer.css.parser.FSRGBColor;
import org.xhtmlrenderer.css.parser.Lexer;
import org.xhtmlrenderer.css.parser.PropertyValue;
import org.xhtmlrenderer.css.parser.Token;
import org.xhtmlrenderer.css.parser.property.PropertyBuilder;
import org.xhtmlrenderer.css.sheet.FontFaceRule;
import org.xhtmlrenderer.css.sheet.MediaRule;
import org.xhtmlrenderer.css.sheet.PageRule;
import org.xhtmlrenderer.css.sheet.PropertyDeclaration;
import org.xhtmlrenderer.css.sheet.Ruleset;
import org.xhtmlrenderer.css.sheet.RulesetContainer;
import org.xhtmlrenderer.css.sheet.Stylesheet;
import org.xhtmlrenderer.css.sheet.StylesheetInfo;

public class CSSParser {
    private static final Set SUPPORTED_PSEUDO_ELEMENTS = new HashSet();
    private static final Set CSS21_PSEUDO_ELEMENTS;
    private Token _saved;
    private Lexer _lexer;
    private CSSErrorHandler _errorHandler;
    private String _URI;
    private Map _namespaces = new HashMap();
    private boolean _supportCMYKColors;

    public CSSParser(CSSErrorHandler errorHandler) {
        this._lexer = new Lexer(new StringReader(""));
        this._errorHandler = errorHandler;
    }

    public Stylesheet parseStylesheet(String uri, int origin, Reader reader) throws IOException {
        this._URI = uri;
        this.reset(reader);
        Stylesheet result = new Stylesheet(uri, origin);
        this.stylesheet(result);
        return result;
    }

    public Ruleset parseDeclaration(int origin, String text) {
        try {
            this._URI = "style attribute";
            this.reset(new StringReader(text));
            this.skip_whitespace();
            Ruleset result = new Ruleset(origin);
            try {
                this.declaration_list(result, true, false, false);
            }
            catch (CSSParseException cSSParseException) {
                // empty catch block
            }
            return result;
        }
        catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public PropertyValue parsePropertyValue(CSSName cssName, int origin, String expr) {
        this._URI = cssName + " property value";
        try {
            List props;
            this.reset(new StringReader(expr));
            List values = this.expr(cssName == CSSName.FONT_FAMILY || cssName == CSSName.FONT_SHORTHAND || cssName == CSSName.FS_PDF_FONT_ENCODING);
            PropertyBuilder builder = CSSName.getPropertyBuilder(cssName);
            try {
                props = builder.buildDeclarations(cssName, values, origin, false);
            }
            catch (CSSParseException e) {
                e.setLine(this.getCurrentLine());
                throw e;
            }
            if (props.size() != 1) {
                throw new CSSParseException("Builder created " + props.size() + "properties, expected 1", this.getCurrentLine());
            }
            PropertyDeclaration decl = (PropertyDeclaration)props.get(0);
            return (PropertyValue)decl.getValue();
        }
        catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        catch (CSSParseException e) {
            this.error(e, "property value", false);
            return null;
        }
    }

    private void stylesheet(Stylesheet stylesheet) throws IOException {
        block19: {
            Token t = this.la();
            try {
                block18: {
                    if (t == Token.TK_CHARSET_SYM) {
                        try {
                            t = this.next();
                            this.skip_whitespace();
                            t = this.next();
                            if (t == Token.TK_STRING) {
                                this.skip_whitespace();
                                t = this.next();
                                if (t != Token.TK_SEMICOLON) {
                                    this.push(t);
                                    throw new CSSParseException(t, Token.TK_SEMICOLON, this.getCurrentLine());
                                }
                                break block18;
                            }
                            this.push(t);
                            throw new CSSParseException(t, Token.TK_STRING, this.getCurrentLine());
                        }
                        catch (CSSParseException e) {
                            this.error(e, "@charset rule", true);
                            this.recover(false, false);
                        }
                    }
                }
                this.skip_whitespace_and_cdocdc();
                while ((t = this.la()) == Token.TK_IMPORT_SYM) {
                    this.import_rule(stylesheet);
                    this.skip_whitespace_and_cdocdc();
                }
                while ((t = this.la()) == Token.TK_NAMESPACE_SYM) {
                    this.namespace();
                    this.skip_whitespace_and_cdocdc();
                }
                while ((t = this.la()) != Token.TK_EOF) {
                    switch (t.getType()) {
                        case 18: {
                            this.page(stylesheet);
                            break;
                        }
                        case 19: {
                            this.media(stylesheet);
                            break;
                        }
                        case 22: {
                            this.font_face(stylesheet);
                            break;
                        }
                        case 17: {
                            this.next();
                            this.error(new CSSParseException("@import not allowed here", this.getCurrentLine()), "@import rule", true);
                            this.recover(false, false);
                            break;
                        }
                        case 21: {
                            this.next();
                            this.error(new CSSParseException("@namespace not allowed here", this.getCurrentLine()), "@namespace rule", true);
                            this.recover(false, false);
                            break;
                        }
                        case 23: {
                            this.next();
                            this.error(new CSSParseException("Invalid at-rule", this.getCurrentLine()), "at-rule", true);
                            this.recover(false, false);
                        }
                        default: {
                            this.ruleset(stylesheet);
                        }
                    }
                    this.skip_whitespace_and_cdocdc();
                }
            }
            catch (CSSParseException e) {
                if (e.isCallerNotified()) break block19;
                this.error(e, "stylesheet", false);
            }
        }
    }

    private void import_rule(Stylesheet stylesheet) throws IOException {
        try {
            StylesheetInfo info;
            Token t = this.next();
            if (t == Token.TK_IMPORT_SYM) {
                info = new StylesheetInfo();
                info.setOrigin(stylesheet.getOrigin());
                info.setType("text/css");
                this.skip_whitespace();
                t = this.next();
                switch (t.getType()) {
                    case 13: 
                    case 39: {
                        try {
                            info.setUri(new URL(new URL(stylesheet.getURI()), this.getTokenValue(t)).toString());
                        }
                        catch (MalformedURLException mue) {
                            try {
                                URI parent = new URI(stylesheet.getURI());
                                String tokenValue = this.getTokenValue(t);
                                String resolvedUri = parent.resolve(tokenValue).toString();
                                System.out.println("Token: " + tokenValue + " resolved " + resolvedUri);
                                info.setUri(resolvedUri);
                            }
                            catch (URISyntaxException use) {
                                throw new CSSParseException("Invalid URL, " + use.getMessage(), this.getCurrentLine());
                            }
                        }
                        this.skip_whitespace();
                        t = this.la();
                        if (t == Token.TK_IDENT) {
                            info.addMedium(this.medium());
                            while ((t = this.la()) == Token.TK_COMMA) {
                                this.next();
                                this.skip_whitespace();
                                t = this.la();
                                if (t == Token.TK_IDENT) {
                                    info.addMedium(this.medium());
                                    continue;
                                }
                                throw new CSSParseException(t, Token.TK_IDENT, this.getCurrentLine());
                            }
                        }
                        if ((t = this.next()) == Token.TK_SEMICOLON) {
                            this.skip_whitespace();
                            break;
                        }
                        this.push(t);
                        throw new CSSParseException(t, Token.TK_SEMICOLON, this.getCurrentLine());
                    }
                    default: {
                        this.push(t);
                        throw new CSSParseException(t, new Token[]{Token.TK_STRING, Token.TK_URI}, this.getCurrentLine());
                    }
                }
                if (info.getMedia().size() == 0) {
                    info.addMedium("all");
                }
            } else {
                this.push(t);
                throw new CSSParseException(t, Token.TK_IMPORT_SYM, this.getCurrentLine());
            }
            stylesheet.addImportRule(info);
        }
        catch (CSSParseException e) {
            this.error(e, "@import rule", true);
            this.recover(false, false);
        }
    }

    private void namespace() throws IOException {
        try {
            String url;
            String prefix;
            Token t = this.next();
            if (t == Token.TK_NAMESPACE_SYM) {
                prefix = null;
                url = null;
                this.skip_whitespace();
                t = this.next();
                if (t == Token.TK_IDENT) {
                    prefix = this.getTokenValue(t);
                    this.skip_whitespace();
                    t = this.next();
                }
                if (t != Token.TK_STRING && t != Token.TK_URI) {
                    throw new CSSParseException(t, new Token[]{Token.TK_STRING, Token.TK_URI}, this.getCurrentLine());
                }
                url = this.getTokenValue(t);
                this.skip_whitespace();
                t = this.next();
                if (t != Token.TK_SEMICOLON) {
                    throw new CSSParseException(t, Token.TK_SEMICOLON, this.getCurrentLine());
                }
            } else {
                throw new CSSParseException(t, Token.TK_NAMESPACE_SYM, this.getCurrentLine());
            }
            this.skip_whitespace();
            this._namespaces.put(prefix, url);
        }
        catch (CSSParseException e) {
            this.error(e, "@namespace rule", true);
            this.recover(false, false);
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private void media(Stylesheet stylesheet) throws IOException {
        Token t = this.next();
        try {
            MediaRule mediaRule;
            if (t == Token.TK_MEDIA_SYM) {
                mediaRule = new MediaRule(stylesheet.getOrigin());
                this.skip_whitespace();
                t = this.la();
                if (t != Token.TK_IDENT) throw new CSSParseException(t, Token.TK_IDENT, this.getCurrentLine());
                mediaRule.addMedium(this.medium());
                while ((t = this.la()) == Token.TK_COMMA) {
                    this.next();
                    this.skip_whitespace();
                    t = this.la();
                    if (t != Token.TK_IDENT) throw new CSSParseException(t, Token.TK_IDENT, this.getCurrentLine());
                    mediaRule.addMedium(this.medium());
                }
                t = this.next();
                if (t == Token.TK_LBRACE) {
                    this.skip_whitespace();
                    block6: while ((t = this.la()) != null) {
                        switch (t.getType()) {
                            case 42: {
                                this.next();
                                break block6;
                            }
                            default: {
                                this.ruleset(mediaRule);
                                continue block6;
                            }
                        }
                    }
                } else {
                    this.push(t);
                    throw new CSSParseException(t, Token.TK_LBRACE, this.getCurrentLine());
                }
                this.skip_whitespace();
            } else {
                this.push(t);
                throw new CSSParseException(t, Token.TK_MEDIA_SYM, this.getCurrentLine());
            }
            stylesheet.addContent(mediaRule);
            return;
        }
        catch (CSSParseException e) {
            this.error(e, "@media rule", true);
            this.recover(false, false);
        }
    }

    private String medium() throws IOException {
        String result = null;
        Token t = this.next();
        if (t != Token.TK_IDENT) {
            this.push(t);
            throw new CSSParseException(t, Token.TK_IDENT, this.getCurrentLine());
        }
        result = this.getTokenValue(t);
        this.skip_whitespace();
        return result;
    }

    private void font_face(Stylesheet stylesheet) throws IOException {
        Token t = this.next();
        try {
            Ruleset ruleset;
            FontFaceRule fontFaceRule = new FontFaceRule(stylesheet.getOrigin());
            if (t == Token.TK_FONT_FACE_SYM) {
                this.skip_whitespace();
                ruleset = new Ruleset(stylesheet.getOrigin());
                this.skip_whitespace();
                t = this.next();
                if (t == Token.TK_LBRACE) {
                    int maxLoops = 0x100000;
                    int i = 0;
                    while (true) {
                        if (++i >= maxLoops) {
                            throw new CSSParseException(t, Token.TK_RBRACE, this.getCurrentLine());
                        }
                        this.skip_whitespace();
                        t = this.la();
                        if (t != Token.TK_RBRACE) {
                            this.declaration_list(ruleset, false, true, true);
                            continue;
                        }
                        break;
                    }
                } else {
                    this.push(t);
                    throw new CSSParseException(t, Token.TK_LBRACE, this.getCurrentLine());
                }
                this.next();
                this.skip_whitespace();
            } else {
                this.push(t);
                throw new CSSParseException(t, Token.TK_FONT_FACE_SYM, this.getCurrentLine());
            }
            fontFaceRule.addContent(ruleset);
            stylesheet.addFontFaceRule(fontFaceRule);
        }
        catch (CSSParseException e) {
            this.error(e, "@font-face rule", true);
            this.recover(false, false);
        }
    }

    private void page(Stylesheet stylesheet) throws IOException {
        Token t = this.next();
        try {
            Ruleset ruleset;
            PageRule pageRule = new PageRule(stylesheet.getOrigin());
            if (t == Token.TK_PAGE_SYM) {
                this.skip_whitespace();
                t = this.la();
                if (t == Token.TK_IDENT) {
                    String pageName = this.getTokenValue(t);
                    if (pageName.equals("auto")) {
                        throw new CSSParseException("page name may not be auto", this.getCurrentLine());
                    }
                    this.next();
                    pageRule.setName(pageName);
                    t = this.la();
                }
                if (t == Token.TK_COLON) {
                    pageRule.setPseudoPage(this.pseudo_page());
                }
                ruleset = new Ruleset(stylesheet.getOrigin());
                this.skip_whitespace();
                t = this.next();
                if (t == Token.TK_LBRACE) {
                    while (true) {
                        this.skip_whitespace();
                        t = this.la();
                        if (t != Token.TK_RBRACE) {
                            if (t == Token.TK_AT_RULE) {
                                this.margin(stylesheet, pageRule);
                                continue;
                            }
                            this.declaration_list(ruleset, false, true, false);
                            continue;
                        }
                        break;
                    }
                } else {
                    this.push(t);
                    throw new CSSParseException(t, Token.TK_LBRACE, this.getCurrentLine());
                }
                this.next();
                this.skip_whitespace();
            } else {
                this.push(t);
                throw new CSSParseException(t, Token.TK_PAGE_SYM, this.getCurrentLine());
            }
            pageRule.addContent(ruleset);
            stylesheet.addContent(pageRule);
        }
        catch (CSSParseException e) {
            this.error(e, "@page rule", true);
            this.recover(false, false);
        }
    }

    private void margin(Stylesheet stylesheet, PageRule pageRule) throws IOException {
        Token t = this.next();
        if (t != Token.TK_AT_RULE) {
            this.error(new CSSParseException(t, Token.TK_AT_RULE, this.getCurrentLine()), "at rule", true);
            this.recover(true, false);
            return;
        }
        String name = this.getTokenValue(t);
        MarginBoxName marginBoxName = MarginBoxName.valueOf(name);
        if (marginBoxName == null) {
            this.error(new CSSParseException(name + " is not a valid margin box name", this.getCurrentLine()), "at rule", true);
            this.recover(true, false);
            return;
        }
        this.skip_whitespace();
        try {
            Ruleset ruleset;
            t = this.next();
            if (t == Token.TK_LBRACE) {
                this.skip_whitespace();
                ruleset = new Ruleset(stylesheet.getOrigin());
                this.declaration_list(ruleset, false, false, false);
                t = this.next();
                if (t != Token.TK_RBRACE) {
                    this.push(t);
                    throw new CSSParseException(t, Token.TK_RBRACE, this.getCurrentLine());
                }
            } else {
                this.push(t);
                throw new CSSParseException(t, Token.TK_LBRACE, this.getCurrentLine());
            }
            pageRule.addMarginBoxProperties(marginBoxName, ruleset.getPropertyDeclarations());
        }
        catch (CSSParseException e) {
            this.error(e, "margin box", true);
            this.recover(false, false);
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private String pseudo_page() throws IOException {
        String result = null;
        Token t = this.next();
        if (t == Token.TK_COLON) {
            t = this.next();
            if (t == Token.TK_IDENT) {
                result = this.getTokenValue(t);
                if (result.equals("first") || result.equals("left") || result.equals("right")) return result;
                throw new CSSParseException("Pseudo page must be one of first, left, or right", this.getCurrentLine());
            }
            this.push(t);
            throw new CSSParseException(t, Token.TK_IDENT, this.getCurrentLine());
        }
        this.push(t);
        throw new CSSParseException(t, Token.TK_COLON, this.getCurrentLine());
    }

    private void operator() throws IOException {
        Token t = this.la();
        switch (t.getType()) {
            case 12: 
            case 44: {
                this.next();
                this.skip_whitespace();
            }
        }
    }

    private Token combinator() throws IOException {
        Token t = this.next();
        if (t == Token.TK_PLUS || t == Token.TK_GREATER) {
            this.skip_whitespace();
        } else if (t != Token.TK_S) {
            this.push(t);
            throw new CSSParseException(t, new Token[]{Token.TK_PLUS, Token.TK_GREATER, Token.TK_S}, this.getCurrentLine());
        }
        return t;
    }

    private int unary_operator() throws IOException {
        Token t = this.next();
        if (t != Token.TK_MINUS && t != Token.TK_PLUS) {
            this.push(t);
            throw new CSSParseException(t, new Token[]{Token.TK_MINUS, Token.TK_PLUS}, this.getCurrentLine());
        }
        if (t == Token.TK_MINUS) {
            return -1;
        }
        return 1;
    }

    private String property() throws IOException {
        Token t = this.next();
        if (t != Token.TK_IDENT) {
            this.push(t);
            throw new CSSParseException(t, Token.TK_IDENT, this.getCurrentLine());
        }
        String result = this.getTokenValue(t);
        this.skip_whitespace();
        return result;
    }

    private void declaration_list(Ruleset ruleset, boolean expectEOF, boolean expectAtRule, boolean inFontFace) throws IOException {
        block6: while (true) {
            Token t = this.la();
            switch (t.getType()) {
                case 43: {
                    this.next();
                    this.skip_whitespace();
                    continue block6;
                }
                case 42: {
                    break block6;
                }
                case 23: {
                    if (expectAtRule) break block6;
                    this.declaration(ruleset, inFontFace);
                }
                case 54: {
                    if (expectEOF) break block6;
                }
                default: {
                    this.declaration(ruleset, inFontFace);
                    continue block6;
                }
            }
            break;
        }
    }

    private void ruleset(RulesetContainer container) throws IOException {
        try {
            Token t;
            Ruleset ruleset = new Ruleset(container.getOrigin());
            this.selector(ruleset);
            while ((t = this.la()) == Token.TK_COMMA) {
                this.next();
                this.skip_whitespace();
                this.selector(ruleset);
            }
            t = this.next();
            if (t == Token.TK_LBRACE) {
                this.skip_whitespace();
                this.declaration_list(ruleset, false, false, false);
                t = this.next();
                if (t != Token.TK_RBRACE) {
                    this.push(t);
                    throw new CSSParseException(t, Token.TK_RBRACE, this.getCurrentLine());
                }
            } else {
                this.push(t);
                throw new CSSParseException(t, new Token[]{Token.TK_COMMA, Token.TK_LBRACE}, this.getCurrentLine());
            }
            this.skip_whitespace();
            if (ruleset.getPropertyDeclarations().size() > 0) {
                container.addContent(ruleset);
            }
        }
        catch (CSSParseException e) {
            this.error(e, "ruleset", true);
            this.recover(true, false);
        }
    }

    private void selector(Ruleset ruleset) throws IOException {
        ArrayList<Selector> selectors = new ArrayList<Selector>();
        ArrayList<Token> combinators = new ArrayList<Token>();
        selectors.add(this.simple_selector(ruleset));
        block6: while (true) {
            Token t = this.la();
            switch (t.getType()) {
                case 1: 
                case 10: 
                case 11: {
                    combinators.add(this.combinator());
                    t = this.la();
                    switch (t.getType()) {
                        case 15: 
                        case 16: 
                        case 45: 
                        case 48: 
                        case 50: 
                        case 52: {
                            selectors.add(this.simple_selector(ruleset));
                            continue block6;
                        }
                    }
                    throw new CSSParseException(t, new Token[]{Token.TK_IDENT, Token.TK_ASTERISK, Token.TK_HASH, Token.TK_PERIOD, Token.TK_LBRACKET, Token.TK_COLON}, this.getCurrentLine());
                }
            }
            break;
        }
        ruleset.addFSSelector(this.mergeSimpleSelectors(selectors, combinators));
    }

    private Selector mergeSimpleSelectors(List selectors, List combinators) {
        int count = selectors.size();
        if (count == 1) {
            return (Selector)selectors.get(0);
        }
        int lastDescendantOrChildAxis = 0;
        Selector result = null;
        block0: for (int i = 0; i < count - 1; ++i) {
            Selector first = (Selector)selectors.get(i);
            Selector second = (Selector)selectors.get(i + 1);
            Token combinator = (Token)combinators.get(i);
            if (first.getPseudoElement() != null) {
                throw new CSSParseException("A simple selector with a pseudo element cannot be combined with another simple selector", this.getCurrentLine());
            }
            boolean sibling = false;
            if (combinator == Token.TK_S) {
                second.setAxis(0);
                lastDescendantOrChildAxis = 0;
            } else if (combinator == Token.TK_GREATER) {
                second.setAxis(1);
                lastDescendantOrChildAxis = 1;
            } else if (combinator == Token.TK_PLUS) {
                first.setAxis(2);
                sibling = true;
            }
            second.setSpecificityB(second.getSpecificityB() + first.getSpecificityB());
            second.setSpecificityC(second.getSpecificityC() + first.getSpecificityC());
            second.setSpecificityD(second.getSpecificityD() + first.getSpecificityD());
            if (!sibling) {
                if (result == null) {
                    result = first;
                }
                first.setChainedSelector(second);
                continue;
            }
            second.setSiblingSelector(first);
            if (result == null || result == first) {
                result = second;
            }
            if (i <= 0) continue;
            for (int j = i - 1; j >= 0; --j) {
                Selector selector = (Selector)selectors.get(j);
                if (selector.getChainedSelector() != first) continue;
                selector.setChainedSelector(second);
                second.setAxis(lastDescendantOrChildAxis);
                continue block0;
            }
        }
        return result;
    }

    private Selector simple_selector(Ruleset ruleset) throws IOException {
        Selector selector = new Selector();
        selector.setParent(ruleset);
        Token t = this.la();
        switch (t.getType()) {
            case 15: 
            case 52: 
            case 53: {
                NamespacePair pair = this.typed_value(false);
                selector.setNamespaceURI(pair.getNamespaceURI());
                selector.setName(pair.getName());
                block15: while (true) {
                    t = this.la();
                    switch (t.getType()) {
                        case 16: {
                            t = this.next();
                            selector.addIDCondition(this.getTokenValue(t, true));
                            continue block15;
                        }
                        case 50: {
                            this.class_selector(selector);
                            continue block15;
                        }
                        case 48: {
                            this.attrib(selector);
                            continue block15;
                        }
                        case 45: {
                            this.pseudo(selector);
                            continue block15;
                        }
                    }
                    break;
                }
                break;
            }
            default: {
                boolean found = false;
                block16: while (true) {
                    t = this.la();
                    switch (t.getType()) {
                        case 16: {
                            t = this.next();
                            selector.addIDCondition(this.getTokenValue(t, true));
                            found = true;
                            continue block16;
                        }
                        case 50: {
                            this.class_selector(selector);
                            found = true;
                            continue block16;
                        }
                        case 48: {
                            this.attrib(selector);
                            found = true;
                            continue block16;
                        }
                        case 45: {
                            this.pseudo(selector);
                            found = true;
                            continue block16;
                        }
                    }
                    break;
                }
                if (found) break;
                throw new CSSParseException(t, new Token[]{Token.TK_HASH, Token.TK_PERIOD, Token.TK_LBRACKET, Token.TK_COLON}, this.getCurrentLine());
            }
        }
        return selector;
    }

    private NamespacePair typed_value(boolean matchAttribute) throws IOException {
        String prefix = null;
        String name = null;
        Token t = this.la();
        if (t == Token.TK_ASTERISK || t == Token.TK_IDENT) {
            this.next();
            if (t == Token.TK_IDENT) {
                name = this.getTokenValue(t, true);
            }
            t = this.la();
        } else if (t == Token.TK_VERTICAL_BAR) {
            prefix = "";
        } else {
            throw new CSSParseException(t, new Token[]{Token.TK_ASTERISK, Token.TK_IDENT, Token.TK_VERTICAL_BAR}, this.getCurrentLine());
        }
        if (t == Token.TK_VERTICAL_BAR) {
            this.next();
            t = this.next();
            if (t == Token.TK_ASTERISK || t == Token.TK_IDENT) {
                if (prefix == null) {
                    prefix = name;
                }
                if (t == Token.TK_IDENT) {
                    name = this.getTokenValue(t, true);
                }
            } else {
                throw new CSSParseException(t, new Token[]{Token.TK_ASTERISK, Token.TK_IDENT}, this.getCurrentLine());
            }
        }
        String namespaceURI = null;
        if (prefix != null && prefix != "") {
            namespaceURI = (String)this._namespaces.get(prefix.toLowerCase());
            if (namespaceURI == null) {
                throw new CSSParseException("There is no namespace with prefix " + prefix + " defined", this.getCurrentLine());
            }
        } else if (prefix == null && !matchAttribute) {
            namespaceURI = (String)this._namespaces.get(null);
        }
        if (matchAttribute && name == null) {
            throw new CSSParseException("An attribute name is required", this.getCurrentLine());
        }
        return new NamespacePair(namespaceURI, name);
    }

    private void class_selector(Selector selector) throws IOException {
        Token t = this.next();
        if (t == Token.TK_PERIOD) {
            t = this.next();
            if (t != Token.TK_IDENT) {
                this.push(t);
                throw new CSSParseException(t, Token.TK_IDENT, this.getCurrentLine());
            }
        } else {
            this.push(t);
            throw new CSSParseException(t, Token.TK_PERIOD, this.getCurrentLine());
        }
        selector.addClassCondition(this.getTokenValue(t, true));
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private void attrib(Selector selector) throws IOException {
        Token t = this.next();
        if (t == Token.TK_LBRACKET) {
            this.skip_whitespace();
            t = this.la();
            if (t != Token.TK_IDENT && t != Token.TK_ASTERISK && t != Token.TK_VERTICAL_BAR) throw new CSSParseException(t, new Token[]{Token.TK_IDENT, Token.TK_ASTERISK}, this.getCurrentLine());
            boolean existenceMatch = true;
            NamespacePair pair = this.typed_value(true);
            String attrNamespaceURI = pair.getNamespaceURI();
            String attrName = pair.getName();
            this.skip_whitespace();
            t = this.la();
            switch (t.getType()) {
                case 4: 
                case 5: 
                case 6: 
                case 7: 
                case 8: 
                case 51: {
                    existenceMatch = false;
                    Token selectorType = this.next();
                    this.skip_whitespace();
                    t = this.next();
                    if (t == Token.TK_IDENT || t == Token.TK_STRING) {
                        String value = this.getTokenValue(t, true);
                        switch (selectorType.getType()) {
                            case 51: {
                                selector.addAttributeEqualsCondition(attrNamespaceURI, attrName, value);
                                break;
                            }
                            case 5: {
                                selector.addAttributeMatchesFirstPartCondition(attrNamespaceURI, attrName, value);
                                break;
                            }
                            case 4: {
                                selector.addAttributeMatchesListCondition(attrNamespaceURI, attrName, value);
                                break;
                            }
                            case 6: {
                                selector.addAttributePrefixCondition(attrNamespaceURI, attrName, value);
                                break;
                            }
                            case 7: {
                                selector.addAttributeSuffixCondition(attrNamespaceURI, attrName, value);
                                break;
                            }
                            case 8: {
                                selector.addAttributeSubstringCondition(attrNamespaceURI, attrName, value);
                            }
                        }
                    } else {
                        this.push(t);
                        throw new CSSParseException(t, new Token[]{Token.TK_IDENT, Token.TK_STRING}, this.getCurrentLine());
                    }
                    this.skip_whitespace();
                    this.skip_whitespace();
                    t = this.la();
                }
            }
            if (existenceMatch) {
                selector.addAttributeExistsCondition(attrNamespaceURI, attrName);
            }
            if (t != Token.TK_RBRACKET) {
                throw new CSSParseException(t, new Token[]{Token.TK_EQUALS, Token.TK_INCLUDES, Token.TK_DASHMATCH, Token.TK_PREFIXMATCH, Token.TK_SUFFIXMATCH, Token.TK_SUBSTRINGMATCH, Token.TK_RBRACKET}, this.getCurrentLine());
            }
        } else {
            this.push(t);
            throw new CSSParseException(t, Token.TK_LBRACKET, this.getCurrentLine());
        }
        this.next();
    }

    private void addPseudoClassOrElement(Token t, Selector selector) {
        String value = this.getTokenValue(t);
        if (value.equals("link")) {
            selector.addLinkCondition();
        } else if (value.equals("visited")) {
            selector.setPseudoClass(2);
        } else if (value.equals("hover")) {
            selector.setPseudoClass(4);
        } else if (value.equals("focus")) {
            selector.setPseudoClass(16);
        } else if (value.equals("active")) {
            selector.setPseudoClass(8);
        } else if (value.equals("first-child")) {
            selector.addFirstChildCondition();
        } else if (value.equals("even")) {
            selector.addEvenChildCondition();
        } else if (value.equals("odd")) {
            selector.addOddChildCondition();
        } else if (value.equals("last-child")) {
            selector.addLastChildCondition();
        } else if (CSS21_PSEUDO_ELEMENTS.contains(value)) {
            selector.setPseudoElement(value);
        } else {
            throw new CSSParseException(value + " is not a recognized pseudo-class", this.getCurrentLine());
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private void addPseudoClassOrElementFunction(Token t, Selector selector) throws IOException {
        String f = this.getTokenValue(t);
        if ((f = f.substring(0, f.length() - 1)).equals("lang")) {
            this.skip_whitespace();
            t = this.next();
            if (t != Token.TK_IDENT) {
                this.push(t);
                throw new CSSParseException(t, Token.TK_IDENT, this.getCurrentLine());
            }
            String lang = this.getTokenValue(t);
            selector.addLangCondition(lang);
            this.skip_whitespace();
            t = this.next();
        } else {
            if (!f.equals("nth-child")) {
                this.push(t);
                throw new CSSParseException(f + " is not a valid function in this context", this.getCurrentLine());
            }
            StringBuilder number = new StringBuilder();
            while ((t = this.next()) != null && (t == Token.TK_IDENT || t == Token.TK_S || t == Token.TK_NUMBER || t == Token.TK_DIMENSION || t == Token.TK_PLUS || t == Token.TK_MINUS)) {
                number.append(this.getTokenValue(t));
            }
            try {
                selector.addNthChildCondition(number.toString());
            }
            catch (CSSParseException e) {
                e.setLine(this.getCurrentLine());
                this.push(t);
                throw e;
            }
        }
        if (t != Token.TK_RPAREN) {
            this.push(t);
            throw new CSSParseException(t, Token.TK_RPAREN, this.getCurrentLine());
        }
    }

    private void addPseudoElement(Token t, Selector selector) {
        String value = this.getTokenValue(t);
        if (!SUPPORTED_PSEUDO_ELEMENTS.contains(value)) {
            throw new CSSParseException(value + " is not a recognized psuedo-element", this.getCurrentLine());
        }
        selector.setPseudoElement(value);
    }

    private void pseudo(Selector selector) throws IOException {
        block6: {
            Token t;
            block5: {
                t = this.next();
                if (t != Token.TK_COLON) break block5;
                t = this.next();
                switch (t.getType()) {
                    case 45: {
                        t = this.next();
                        this.addPseudoElement(t, selector);
                        break block6;
                    }
                    case 15: {
                        this.addPseudoClassOrElement(t, selector);
                        break block6;
                    }
                    case 40: {
                        this.addPseudoClassOrElementFunction(t, selector);
                        break block6;
                    }
                    default: {
                        this.push(t);
                        throw new CSSParseException(t, new Token[]{Token.TK_IDENT, Token.TK_FUNCTION}, this.getCurrentLine());
                    }
                }
            }
            this.push(t);
            throw new CSSParseException(t, Token.TK_COLON, this.getCurrentLine());
        }
    }

    private boolean checkCSSName(CSSName cssName, String propertyName) {
        if (cssName == null) {
            this._errorHandler.error(this._URI, propertyName + " is an unrecognized CSS property at line " + this.getCurrentLine() + ". Ignoring declaration.");
            return false;
        }
        if (!CSSName.isImplemented(cssName)) {
            this._errorHandler.error(this._URI, propertyName + " is not implemented at line " + this.getCurrentLine() + ". Ignoring declaration.");
            return false;
        }
        PropertyBuilder builder = CSSName.getPropertyBuilder(cssName);
        if (builder == null) {
            this._errorHandler.error(this._URI, "(bug) No property builder defined for " + propertyName + " at line " + this.getCurrentLine() + ". Ignoring declaration.");
            return false;
        }
        return true;
    }

    private void declaration(Ruleset ruleset, boolean inFontFace) throws IOException {
        block9: {
            try {
                Token t = this.la();
                if (t == Token.TK_IDENT) {
                    String propertyName = this.property();
                    CSSName cssName = CSSName.getByPropertyName(propertyName);
                    boolean valid = this.checkCSSName(cssName, propertyName);
                    t = this.next();
                    if (t == Token.TK_COLON) {
                        this.skip_whitespace();
                        List values = this.expr(cssName == CSSName.FONT_FAMILY || cssName == CSSName.FONT_SHORTHAND || cssName == CSSName.FS_PDF_FONT_ENCODING);
                        boolean important = false;
                        t = this.la();
                        if (t == Token.TK_IMPORTANT_SYM) {
                            this.prio();
                            important = true;
                        }
                        if ((t = this.la()) != Token.TK_SEMICOLON && t != Token.TK_RBRACE && t != Token.TK_EOF) {
                            throw new CSSParseException(t, new Token[]{Token.TK_SEMICOLON, Token.TK_RBRACE}, this.getCurrentLine());
                        }
                        if (valid) {
                            try {
                                PropertyBuilder builder = CSSName.getPropertyBuilder(cssName);
                                ruleset.addAllProperties(builder.buildDeclarations(cssName, values, ruleset.getOrigin(), important, !inFontFace));
                            }
                            catch (CSSParseException e) {
                                e.setLine(this.getCurrentLine());
                                this.error(e, "declaration", true);
                            }
                        }
                        break block9;
                    }
                    this.push(t);
                    throw new CSSParseException(t, Token.TK_COLON, this.getCurrentLine());
                }
                throw new CSSParseException(t, Token.TK_IDENT, this.getCurrentLine());
            }
            catch (CSSParseException e) {
                this.error(e, "declaration", true);
                this.recover(false, true);
            }
        }
    }

    private void prio() throws IOException {
        Token t = this.next();
        if (t != Token.TK_IMPORTANT_SYM) {
            this.push(t);
            throw new CSSParseException(t, Token.TK_IMPORTANT_SYM, this.getCurrentLine());
        }
        this.skip_whitespace();
    }

    private List expr(boolean literal) throws IOException {
        boolean operator;
        Token t;
        ArrayList<PropertyValue> result = new ArrayList<PropertyValue>(10);
        result.add(this.term(literal));
        block6: while (true) {
            t = this.la();
            operator = false;
            Token operatorToken = null;
            switch (t.getType()) {
                case 12: 
                case 44: {
                    operatorToken = t;
                    this.operator();
                    t = this.la();
                    operator = true;
                }
            }
            switch (t.getType()) {
                case 10: 
                case 13: 
                case 15: 
                case 16: 
                case 25: 
                case 26: 
                case 27: 
                case 28: 
                case 29: 
                case 30: 
                case 31: 
                case 32: 
                case 33: 
                case 34: 
                case 35: 
                case 37: 
                case 38: 
                case 39: 
                case 40: 
                case 46: {
                    PropertyValue term = this.term(literal);
                    if (operatorToken != null) {
                        term.setOperator(operatorToken);
                    }
                    result.add(term);
                    continue block6;
                }
            }
            break;
        }
        if (operator) {
            throw new CSSParseException(t, new Token[]{Token.TK_NUMBER, Token.TK_PLUS, Token.TK_MINUS, Token.TK_PERCENTAGE, Token.TK_PX, Token.TK_EMS, Token.TK_EXS, Token.TK_PC, Token.TK_MM, Token.TK_CM, Token.TK_IN, Token.TK_PT, Token.TK_ANGLE, Token.TK_TIME, Token.TK_FREQ, Token.TK_STRING, Token.TK_IDENT, Token.TK_URI, Token.TK_HASH, Token.TK_FUNCTION}, this.getCurrentLine());
        }
        return result;
    }

    private String extractNumber(Token t) {
        char c;
        int i;
        String token = this.getTokenValue(t);
        int offset = 0;
        char[] ch = token.toCharArray();
        for (i = 0; i < ch.length && (c = ch[i]) >= '0' && c <= '9'; ++i) {
            ++offset;
        }
        if (ch[offset] == '.') {
            for (i = ++offset; i < ch.length && (c = ch[i]) >= '0' && c <= '9'; ++i) {
                ++offset;
            }
        }
        return token.substring(0, offset);
    }

    private String extractUnit(Token t) {
        String s = this.extractNumber(t);
        return this.getTokenValue(t).substring(s.length());
    }

    private String sign(float sign) {
        return sign == -1.0f ? "-" : "";
    }

    private PropertyValue term(boolean literal) throws IOException {
        float sign = 1.0f;
        Token t = this.la();
        if (t == Token.TK_PLUS || t == Token.TK_MINUS) {
            sign = this.unary_operator();
            t = this.la();
        }
        PropertyValue result = null;
        switch (t.getType()) {
            case 33: 
            case 34: 
            case 35: 
            case 36: {
                throw new CSSParseException("Unsupported CSS unit " + this.extractUnit(t), this.getCurrentLine());
            }
            case 38: {
                result = new PropertyValue(1, sign * Float.parseFloat(this.getTokenValue(t)), this.sign(sign) + this.getTokenValue(t));
                this.next();
                this.skip_whitespace();
                break;
            }
            case 37: {
                result = new PropertyValue(2, sign * Float.parseFloat(this.extractNumber(t)), this.sign(sign) + this.getTokenValue(t));
                this.next();
                this.skip_whitespace();
                break;
            }
            case 25: {
                result = new PropertyValue(3, sign * Float.parseFloat(this.extractNumber(t)), this.sign(sign) + this.getTokenValue(t));
                this.next();
                this.skip_whitespace();
                break;
            }
            case 26: {
                result = new PropertyValue(4, sign * Float.parseFloat(this.extractNumber(t)), this.sign(sign) + this.getTokenValue(t));
                this.next();
                this.skip_whitespace();
                break;
            }
            case 27: {
                result = new PropertyValue(5, sign * Float.parseFloat(this.extractNumber(t)), this.sign(sign) + this.getTokenValue(t));
                this.next();
                this.skip_whitespace();
                break;
            }
            case 28: {
                result = new PropertyValue(6, sign * Float.parseFloat(this.extractNumber(t)), this.sign(sign) + this.getTokenValue(t));
                this.next();
                this.skip_whitespace();
                break;
            }
            case 29: {
                result = new PropertyValue(7, sign * Float.parseFloat(this.extractNumber(t)), this.sign(sign) + this.getTokenValue(t));
                this.next();
                this.skip_whitespace();
                break;
            }
            case 30: {
                result = new PropertyValue(8, sign * Float.parseFloat(this.extractNumber(t)), this.sign(sign) + this.getTokenValue(t));
                this.next();
                this.skip_whitespace();
                break;
            }
            case 31: {
                result = new PropertyValue(9, sign * Float.parseFloat(this.extractNumber(t)), this.sign(sign) + this.getTokenValue(t));
                this.next();
                this.skip_whitespace();
                break;
            }
            case 32: {
                result = new PropertyValue(10, sign * Float.parseFloat(this.extractNumber(t)), this.sign(sign) + this.getTokenValue(t));
                this.next();
                this.skip_whitespace();
                break;
            }
            case 13: {
                String s = this.getTokenValue(t);
                result = new PropertyValue(19, s, this.getRawTokenValue());
                this.next();
                this.skip_whitespace();
                break;
            }
            case 15: {
                String value = this.getTokenValue(t, literal);
                result = new PropertyValue(21, value, value);
                this.next();
                this.skip_whitespace();
                break;
            }
            case 39: {
                result = new PropertyValue(20, this.getTokenValue(t), this.getRawTokenValue());
                this.next();
                this.skip_whitespace();
                break;
            }
            case 16: {
                result = this.hexcolor();
                break;
            }
            case 40: {
                result = this.function();
                break;
            }
            default: {
                throw new CSSParseException(t, new Token[]{Token.TK_NUMBER, Token.TK_PERCENTAGE, Token.TK_PX, Token.TK_EMS, Token.TK_EXS, Token.TK_PC, Token.TK_MM, Token.TK_CM, Token.TK_IN, Token.TK_PT, Token.TK_ANGLE, Token.TK_TIME, Token.TK_FREQ, Token.TK_STRING, Token.TK_IDENT, Token.TK_URI, Token.TK_HASH, Token.TK_FUNCTION}, this.getCurrentLine());
            }
        }
        return result;
    }

    private PropertyValue function() throws IOException {
        PropertyValue result = null;
        Token t = this.next();
        if (t == Token.TK_FUNCTION) {
            String f = this.getTokenValue(t);
            this.skip_whitespace();
            List params = this.expr(false);
            t = this.next();
            if (t != Token.TK_RPAREN) {
                this.push(t);
                throw new CSSParseException(t, Token.TK_RPAREN, this.getCurrentLine());
            }
            if (f.equals("rgb(")) {
                result = new PropertyValue(this.createRGBColorFromFunction(params));
            } else if (f.equals("cmyk(")) {
                if (!this.isSupportCMYKColors()) {
                    throw new CSSParseException("The current output device does not support CMYK colors", this.getCurrentLine());
                }
                result = new PropertyValue(this.createCMYKColorFromFunction(params));
            } else {
                result = new PropertyValue(new FSFunction(f.substring(0, f.length() - 1), params));
            }
        } else {
            this.push(t);
            throw new CSSParseException(t, Token.TK_FUNCTION, this.getCurrentLine());
        }
        this.skip_whitespace();
        return result;
    }

    private FSCMYKColor createCMYKColorFromFunction(List params) {
        if (params.size() != 4) {
            throw new CSSParseException("The cmyk() function must have exactly four parameters", this.getCurrentLine());
        }
        float[] colorComponents = new float[4];
        for (int i = 0; i < params.size(); ++i) {
            colorComponents[i] = this.parseCMYKColorComponent((PropertyValue)params.get(i), i + 1);
        }
        return new FSCMYKColor(colorComponents[0], colorComponents[1], colorComponents[2], colorComponents[3]);
    }

    private float parseCMYKColorComponent(PropertyValue value, int paramNo) {
        float result;
        short type = value.getPrimitiveType();
        if (type == 1) {
            result = value.getFloatValue();
        } else if (type == 2) {
            result = value.getFloatValue() / 100.0f;
        } else {
            throw new CSSParseException("Parameter " + paramNo + " to the cmyk() function is not a number or a percentage", this.getCurrentLine());
        }
        if (result < 0.0f || result > 1.0f) {
            throw new CSSParseException("Parameter " + paramNo + " to the cmyk() function must be between zero and one", this.getCurrentLine());
        }
        return result;
    }

    private FSRGBColor createRGBColorFromFunction(List params) {
        if (params.size() != 3) {
            throw new CSSParseException("The rgb() function must have exactly three parameters", this.getCurrentLine());
        }
        int red = 0;
        int green = 0;
        int blue = 0;
        block5: for (int i = 0; i < params.size(); ++i) {
            PropertyValue value = (PropertyValue)params.get(i);
            short type = value.getPrimitiveType();
            if (type != 2 && type != 1) {
                throw new CSSParseException("Parameter " + (i + 1) + " to the rgb() function is not a number or percentage", this.getCurrentLine());
            }
            float f = value.getFloatValue();
            if (type == 2) {
                f = f / 100.0f * 255.0f;
            }
            if (f < 0.0f) {
                f = 0.0f;
            } else if (f > 255.0f) {
                f = 255.0f;
            }
            switch (i) {
                case 0: {
                    red = (int)f;
                    continue block5;
                }
                case 1: {
                    green = (int)f;
                    continue block5;
                }
                case 2: {
                    blue = (int)f;
                }
            }
        }
        return new FSRGBColor(red, green, blue);
    }

    private PropertyValue hexcolor() throws IOException {
        String s;
        PropertyValue result = null;
        Token t = this.next();
        if (t == Token.TK_HASH) {
            s = this.getTokenValue(t);
            if (s.length() != 3 && s.length() != 6 || !this.isHexString(s)) {
                this.push(t);
                throw new CSSParseException('#' + s + " is not a valid color definition", this.getCurrentLine());
            }
        } else {
            this.push(t);
            throw new CSSParseException(t, Token.TK_HASH, this.getCurrentLine());
        }
        FSRGBColor color = null;
        color = s.length() == 3 ? new FSRGBColor(this.convertToInteger(s.charAt(0), s.charAt(0)), this.convertToInteger(s.charAt(1), s.charAt(1)), this.convertToInteger(s.charAt(2), s.charAt(2))) : new FSRGBColor(this.convertToInteger(s.charAt(0), s.charAt(1)), this.convertToInteger(s.charAt(2), s.charAt(3)), this.convertToInteger(s.charAt(4), s.charAt(5)));
        result = new PropertyValue(color);
        this.skip_whitespace();
        return result;
    }

    private boolean isHexString(String s) {
        for (int i = 0; i < s.length(); ++i) {
            if (CSSParser.isHexChar(s.charAt(i))) continue;
            return false;
        }
        return true;
    }

    private int convertToInteger(char hexchar1, char hexchar2) {
        int result = this.convertToInteger(hexchar1);
        result <<= 4;
        return result |= this.convertToInteger(hexchar2);
    }

    private int convertToInteger(char hexchar1) {
        if (hexchar1 >= '0' && hexchar1 <= '9') {
            return hexchar1 - 48;
        }
        if (hexchar1 >= 'a' && hexchar1 <= 'f') {
            return hexchar1 - 97 + 10;
        }
        return hexchar1 - 65 + 10;
    }

    private void skip_whitespace() throws IOException {
        Token t;
        while ((t = this.next()) == Token.TK_S) {
        }
        this.push(t);
    }

    private void skip_whitespace_and_cdocdc() throws IOException {
        Token t;
        while ((t = this.next()) == Token.TK_S || t == Token.TK_CDO || t == Token.TK_CDC) {
        }
        this.push(t);
    }

    private Token next() throws IOException {
        if (this._saved != null) {
            Token result = this._saved;
            this._saved = null;
            return result;
        }
        return this._lexer.yylex();
    }

    private void push(Token t) {
        if (this._saved != null) {
            throw new RuntimeException("saved must be null");
        }
        this._saved = t;
    }

    private Token la() throws IOException {
        Token result = this.next();
        this.push(result);
        return result;
    }

    private void error(CSSParseException e, String what, boolean rethrowEOF) {
        if (!e.isCallerNotified()) {
            String message = e.getMessage() + " Skipping " + what + ".";
            this._errorHandler.error(this._URI, message);
        }
        e.setCallerNotified(true);
        if (e.isEOF() && rethrowEOF) {
            throw e;
        }
    }

    /*
     * Enabled aggressive block sorting
     */
    private void recover(boolean needBlock, boolean stopBeforeBlockClose) throws IOException {
        int braces = 0;
        boolean foundBlock = false;
        block5: while (true) {
            Token t;
            if ((t = this.next()) == Token.TK_EOF) {
                return;
            }
            switch (t.getType()) {
                case 9: {
                    foundBlock = true;
                    ++braces;
                    break;
                }
                case 42: {
                    if (braces == 0) {
                        if (!stopBeforeBlockClose) break;
                        this.push(t);
                        break block5;
                    }
                    if (--braces != 0) break;
                    break block5;
                }
                case 43: {
                    if (braces == 0 && (!needBlock || foundBlock)) break block5;
                }
            }
        }
        this.skip_whitespace();
    }

    public void reset(Reader r) {
        this._saved = null;
        this._namespaces.clear();
        this._lexer.yyreset(r);
        this._lexer.setyyline(0);
    }

    public CSSErrorHandler getErrorHandler() {
        return this._errorHandler;
    }

    public void setErrorHandler(CSSErrorHandler errorHandler) {
        this._errorHandler = errorHandler;
    }

    private String getRawTokenValue() {
        return this._lexer.yytext();
    }

    private String getTokenValue(Token t) {
        return this.getTokenValue(t, false);
    }

    private String getTokenValue(Token t, boolean literal) {
        switch (t.getType()) {
            case 13: {
                int count = this._lexer.yylength();
                return CSSParser.processEscapes(this._lexer.yytext().toCharArray(), 1, count - 1);
            }
            case 16: {
                int count = this._lexer.yylength();
                return CSSParser.processEscapes(this._lexer.yytext().toCharArray(), 1, count);
            }
            case 39: {
                int lastSlash;
                String uriResult;
                char[] ch = this._lexer.yytext().toCharArray();
                int start = 4;
                while (ch[start] == '\t' || ch[start] == '\r' || ch[start] == '\n' || ch[start] == '\f') {
                    ++start;
                }
                if (ch[start] == '\'' || ch[start] == '\"') {
                    ++start;
                }
                int end = ch.length - 2;
                while (ch[end] == '\t' || ch[end] == '\r' || ch[end] == '\n' || ch[end] == '\f') {
                    --end;
                }
                if (ch[end] == '\'' || ch[end] == '\"') {
                    --end;
                }
                if (this.isRelativeURI(uriResult = CSSParser.processEscapes(ch, start, end + 1)) && (lastSlash = this._URI.lastIndexOf(47)) != -1) {
                    uriResult = this._URI.substring(0, lastSlash + 1) + uriResult;
                }
                return uriResult;
            }
            case 15: 
            case 23: 
            case 40: {
                int start = 0;
                int count = this._lexer.yylength();
                if (t.getType() == 23) {
                    ++start;
                }
                String result = CSSParser.processEscapes(this._lexer.yytext().toCharArray(), start, count);
                if (!literal) {
                    result = result.toLowerCase();
                }
                return result;
            }
        }
        return this._lexer.yytext();
    }

    private boolean isRelativeURI(String uri) {
        try {
            return uri.length() > 0 && uri.charAt(0) != '/' && !new URI(uri).isAbsolute();
        }
        catch (URISyntaxException e) {
            return false;
        }
    }

    private int getCurrentLine() {
        return this._lexer.yyline();
    }

    private static boolean isHexChar(char c) {
        return c >= '0' && c <= '9' || c >= 'A' && c <= 'F' || c >= 'a' && c <= 'f';
    }

    private static String processEscapes(char[] ch, int start, int end) {
        StringBuffer result = new StringBuffer(ch.length + 10);
        for (int i = start; i < end; ++i) {
            char c = ch[i];
            if (c == '\\') {
                if (i < end - 2 && ch[i + 1] == '\r' && ch[i + 2] == '\n') {
                    i += 2;
                    continue;
                }
                if (i + 1 < ch.length && (ch[i + 1] == '\n' || ch[i + 1] == '\r' || ch[i + 1] == '\f')) {
                    ++i;
                    continue;
                }
                if (i + 1 >= ch.length) {
                    result.append(c);
                    continue;
                }
                if (!CSSParser.isHexChar(ch[i + 1])) continue;
                int current = ++i;
                while (i < end && CSSParser.isHexChar(ch[i]) && i - current < 6) {
                    ++i;
                }
                int cvalue = Integer.parseInt(new String(ch, current, i - current), 16);
                if (cvalue < 65535) {
                    result.append((char)cvalue);
                }
                if (--i < end - 2 && ch[i + 1] == '\r' && ch[i + 2] == '\n') {
                    i += 2;
                    continue;
                }
                if (i >= end - 1 || ch[i + 1] != ' ' && ch[i + 1] != '\t' && ch[i + 1] != '\n' && ch[i + 1] != '\r' && ch[i + 1] != '\f') continue;
                ++i;
                continue;
            }
            result.append(c);
        }
        return result.toString();
    }

    public boolean isSupportCMYKColors() {
        return this._supportCMYKColors;
    }

    public void setSupportCMYKColors(boolean b) {
        this._supportCMYKColors = b;
    }

    static {
        SUPPORTED_PSEUDO_ELEMENTS.add("first-line");
        SUPPORTED_PSEUDO_ELEMENTS.add("first-letter");
        SUPPORTED_PSEUDO_ELEMENTS.add("before");
        SUPPORTED_PSEUDO_ELEMENTS.add("after");
        CSS21_PSEUDO_ELEMENTS = new HashSet();
        CSS21_PSEUDO_ELEMENTS.add("first-line");
        CSS21_PSEUDO_ELEMENTS.add("first-letter");
        CSS21_PSEUDO_ELEMENTS.add("before");
        CSS21_PSEUDO_ELEMENTS.add("after");
    }

    private static class NamespacePair {
        private final String _namespaceURI;
        private final String _name;

        public NamespacePair(String namespaceURI, String name) {
            this._namespaceURI = namespaceURI;
            this._name = name;
        }

        public String getNamespaceURI() {
            return this._namespaceURI;
        }

        public String getName() {
            return this._name;
        }
    }
}

