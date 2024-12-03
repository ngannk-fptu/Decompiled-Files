/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.antlr.v4.runtime.CommonToken
 *  org.antlr.v4.runtime.ParserRuleContext
 *  org.antlr.v4.runtime.RuleContext
 *  org.antlr.v4.runtime.Token
 *  org.antlr.v4.runtime.tree.ErrorNode
 *  org.antlr.v4.runtime.tree.ParseTree
 *  org.antlr.v4.runtime.tree.RuleNode
 *  org.antlr.v4.runtime.tree.TerminalNode
 *  org.antlr.v4.runtime.tree.TerminalNodeImpl
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.unbescape.css.CssEscape
 */
package cz.vutbr.web.csskit.antlr4;

import cz.vutbr.web.css.CSSFactory;
import cz.vutbr.web.css.CombinedSelector;
import cz.vutbr.web.css.Declaration;
import cz.vutbr.web.css.KeyframeBlock;
import cz.vutbr.web.css.MediaExpression;
import cz.vutbr.web.css.MediaQuery;
import cz.vutbr.web.css.RuleBlock;
import cz.vutbr.web.css.RuleFactory;
import cz.vutbr.web.css.RuleList;
import cz.vutbr.web.css.RuleMargin;
import cz.vutbr.web.css.RuleSet;
import cz.vutbr.web.css.Selector;
import cz.vutbr.web.css.Term;
import cz.vutbr.web.css.TermBracketedIdents;
import cz.vutbr.web.css.TermColor;
import cz.vutbr.web.css.TermFactory;
import cz.vutbr.web.css.TermFunction;
import cz.vutbr.web.css.TermIdent;
import cz.vutbr.web.css.TermPercent;
import cz.vutbr.web.css.TermRect;
import cz.vutbr.web.css.TermString;
import cz.vutbr.web.csskit.RuleArrayList;
import cz.vutbr.web.csskit.antlr4.CSSParser;
import cz.vutbr.web.csskit.antlr4.CSSParserExtractor;
import cz.vutbr.web.csskit.antlr4.CSSParserVisitor;
import cz.vutbr.web.csskit.antlr4.CSSToken;
import cz.vutbr.web.csskit.antlr4.Preparator;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unbescape.css.CssEscape;

public class CSSParserVisitorImpl
implements CSSParserVisitor<Object>,
CSSParserExtractor {
    private RuleFactory rf = CSSFactory.getRuleFactory();
    private TermFactory tf = CSSFactory.getTermFactory();
    private Logger log = LoggerFactory.getLogger(this.getClass());
    private int spacesCounter = 0;
    private Preparator preparator;
    private List<MediaQuery> wrapMedia;
    private List<String> importPaths = new ArrayList<String>();
    private List<List<MediaQuery>> importMedia = new ArrayList<List<MediaQuery>>();
    private RuleList rules = null;
    private List<MediaQuery> mediaQueryList = null;
    private boolean preventImports = false;
    protected Stack<statement_scope> statement_stack = new Stack();
    mediaquery_scope mq;
    protected Stack<declaration_scope> declaration_stack = new Stack();
    protected Stack<terms_scope> terms_stack = new Stack();
    protected Stack<funct_args_scope> funct_args_stack = new Stack();
    protected Stack<combined_selector_scope> combined_selector_stack = new Stack();
    protected Stack<selector_scope> selector_stack = new Stack();

    private void logEnter(String entry) {
        if (this.log.isTraceEnabled()) {
            this.log.trace("Enter: {}{}", (Object)this.generateSpaces(this.spacesCounter), (Object)entry);
        }
    }

    private void logEnter(String entry, RuleContext ctx) {
        if (this.log.isTraceEnabled()) {
            this.log.trace("Enter: {}{}: >{}<", new Object[]{this.generateSpaces(this.spacesCounter), entry, ctx.getText()});
        }
    }

    private void logLeave(String leaving) {
        if (this.log.isTraceEnabled()) {
            this.log.trace("Leave: {}{}", (Object)this.generateSpaces(this.spacesCounter), (Object)leaving);
        }
    }

    private String extractTextUnescaped(String text) {
        return CssEscape.unescapeCss((String)text);
    }

    private Declaration.Source extractSource(CSSToken ct) {
        return new Declaration.Source(ct.getBase(), ct.getLine(), ct.getCharPositionInLine());
    }

    private URL extractBase(TerminalNode node) {
        CSSToken ct = (CSSToken)node.getSymbol();
        return ct.getBase();
    }

    private String extractIdUnescaped(String id) {
        if (!id.isEmpty() && !Character.isDigit(id.charAt(0))) {
            return CssEscape.unescapeCss((String)id);
        }
        return null;
    }

    private String generateSpaces(int count) {
        String spaces = "";
        for (int i = 0; i < count; ++i) {
            spaces = spaces + " ";
        }
        return spaces;
    }

    private List<ParseTree> filterSpaceTokens(List<ParseTree> inputArrayList) {
        ArrayList<ParseTree> ret = new ArrayList<ParseTree>(inputArrayList.size());
        for (ParseTree item : inputArrayList) {
            if (item instanceof TerminalNode && ((TerminalNodeImpl)item).getSymbol().getType() == 78) continue;
            ret.add(item);
        }
        return ret;
    }

    private boolean ctxHasErrorNode(ParserRuleContext ctx) {
        for (int i = 0; i < ctx.children.size(); ++i) {
            if (!(ctx.getChild(i) instanceof ErrorNode)) continue;
            return true;
        }
        return false;
    }

    private Term<?> findSpecificType(Term<?> term) {
        TermColor colorTerm = null;
        TermRect rectTerm = null;
        if (term instanceof TermIdent) {
            colorTerm = this.tf.createColor((TermIdent)term);
        } else if (term instanceof TermFunction && (colorTerm = this.tf.createColor((TermFunction)term)) == null) {
            rectTerm = this.tf.createRect((TermFunction)term);
        }
        if (colorTerm != null) {
            if (this.log.isDebugEnabled()) {
                this.log.debug("term color is OK - creating - " + colorTerm.toString());
            }
            return colorTerm;
        }
        if (rectTerm != null) {
            if (this.log.isDebugEnabled()) {
                this.log.debug("term rect is OK - creating - " + rectTerm.toString());
            }
            return rectTerm;
        }
        return null;
    }

    @Override
    public RuleList getRules() {
        return this.rules;
    }

    @Override
    public List<MediaQuery> getMedia() {
        return this.mediaQueryList;
    }

    @Override
    public List<String> getImportPaths() {
        return this.importPaths;
    }

    @Override
    public List<List<MediaQuery>> getImportMedia() {
        return this.importMedia;
    }

    public CSSParserVisitorImpl(Preparator preparator, List<MediaQuery> wrapMedia) {
        this.preparator = preparator;
        this.wrapMedia = wrapMedia;
    }

    public CSSParserVisitorImpl() {
    }

    @Override
    public RuleList visitInlinestyle(CSSParser.InlinestyleContext ctx) {
        this.logEnter("inlinestyle");
        this.rules = new RuleArrayList();
        if (ctx.declarations() != null) {
            Object decl = this.visitDeclarations(ctx.declarations());
            RuleBlock<?> rb = this.preparator.prepareInlineRuleSet((List<Declaration>)decl, null);
            if (rb != null) {
                this.rules.add(rb);
            }
        } else {
            for (CSSParser.InlinesetContext ctxis : ctx.inlineset()) {
                Object irs = this.visitInlineset(ctxis);
                if (irs == null) continue;
                this.rules.add(irs);
            }
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug("\n***\n{}\n***\n", (Object)this.rules);
        }
        this.logLeave("inlinestyle");
        return this.rules;
    }

    @Override
    public RuleList visitStylesheet(CSSParser.StylesheetContext ctx) {
        this.logEnter("stylesheet: ", (RuleContext)ctx);
        this.rules = new RuleArrayList();
        for (CSSParser.StatementContext stmt : ctx.statement()) {
            Object s = this.visitStatement(stmt);
            if (s == null) continue;
            this.rules.add(s);
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug("\n***\n{}\n***\n", (Object)this.rules);
        }
        this.logLeave("stylesheet");
        return this.rules;
    }

    @Override
    public RuleBlock<?> visitStatement(CSSParser.StatementContext ctx) {
        if (this.ctxHasErrorNode(ctx)) {
            return null;
        }
        this.logEnter("statement: ", (RuleContext)ctx);
        this.statement_stack.push(new statement_scope());
        Object stmt = null;
        if (ctx.ruleset() != null) {
            stmt = this.visitRuleset(ctx.ruleset());
        } else if (ctx.atstatement() != null) {
            stmt = this.visitAtstatement(ctx.atstatement());
        }
        if (this.statement_stack.peek().invalid && this.log.isDebugEnabled()) {
            this.log.debug("Statement is invalid");
        }
        this.statement_stack.pop();
        this.logLeave("statement");
        return stmt;
    }

    @Override
    public RuleBlock<?> visitAtstatement(CSSParser.AtstatementContext ctx) {
        this.logEnter("atstatement: ", (RuleContext)ctx);
        RuleBlock<?> atstmt = null;
        if (ctx.CHARSET() == null) {
            if (ctx.IMPORT() != null) {
                Object im = null;
                if (ctx.media() != null) {
                    im = this.visitMedia(ctx.media());
                }
                ctx.import_uri();
                String iuri = this.visitImport_uri(ctx.import_uri());
                if (!this.preventImports && iuri != null) {
                    if (this.log.isDebugEnabled()) {
                        this.log.debug("Adding import: {}", (Object)iuri);
                    }
                    this.importMedia.add((List<MediaQuery>)im);
                    this.importPaths.add(iuri);
                } else if (this.log.isDebugEnabled()) {
                    this.log.debug("Ignoring import: {}", (Object)iuri);
                }
            } else if (ctx.page() != null) {
                atstmt = this.visitPage(ctx.page());
            } else if (ctx.VIEWPORT() != null) {
                Object declarations = this.visitDeclarations(ctx.declarations());
                atstmt = this.preparator.prepareRuleViewport((List<Declaration>)declarations);
                if (atstmt != null) {
                    this.preventImports = true;
                }
            } else if (ctx.FONTFACE() != null) {
                Object declarations = this.visitDeclarations(ctx.declarations());
                atstmt = this.preparator.prepareRuleFontFace((List<Declaration>)declarations);
                if (atstmt != null) {
                    this.preventImports = true;
                }
            } else if (ctx.MEDIA() != null) {
                Object mediaList = null;
                ArrayList<RuleSet> rules = null;
                if (ctx.media() != null) {
                    mediaList = this.visitMedia(ctx.media());
                }
                if (ctx.media_rule() != null) {
                    rules = new ArrayList<RuleSet>();
                    for (CSSParser.Media_ruleContext mr : ctx.media_rule()) {
                        Object rs = this.visitMedia_rule(mr);
                        if (rs == null) continue;
                        rules.add((RuleSet)rs);
                    }
                }
                if ((atstmt = this.preparator.prepareRuleMedia(rules, (List<MediaQuery>)mediaList)) != null) {
                    this.preventImports = true;
                }
            } else if (ctx.KEYFRAMES() != null) {
                String name = null;
                ArrayList<KeyframeBlock> keyframes = null;
                if (ctx.keyframes_name() != null) {
                    name = this.visitKeyframes_name(ctx.keyframes_name());
                }
                if (ctx.keyframe_block() != null) {
                    keyframes = new ArrayList<KeyframeBlock>();
                    for (CSSParser.Keyframe_blockContext kfctx : ctx.keyframe_block()) {
                        KeyframeBlock block = this.visitKeyframe_block(kfctx);
                        if (block == null) continue;
                        keyframes.add(block);
                    }
                }
                if ((atstmt = this.preparator.prepareRuleKeyframes(keyframes, name)) != null) {
                    this.preventImports = true;
                }
            } else if (this.log.isDebugEnabled()) {
                this.log.debug("Skipping invalid at statement");
            }
        }
        this.logLeave("atstatement");
        return atstmt;
    }

    @Override
    public String visitImport_uri(CSSParser.Import_uriContext ctx) {
        if (ctx != null) {
            return this.extractTextUnescaped(ctx.getText());
        }
        return null;
    }

    @Override
    public RuleBlock<?> visitPage(CSSParser.PageContext ctx) {
        boolean invalid = false;
        String name = null;
        if (ctx.IDENT() != null) {
            name = this.extractTextUnescaped(ctx.IDENT().getText());
        }
        Selector.PseudoPage pseudo = null;
        if (ctx.pseudo() != null) {
            Selector.SelectorPart p = this.visitPseudo(ctx.pseudo());
            if (p != null && p instanceof Selector.PseudoPage) {
                pseudo = (Selector.PseudoPage)p;
            } else {
                if (this.log.isDebugEnabled()) {
                    this.log.debug("skipping RulePage with invalid pseudo-class: " + pseudo);
                }
                invalid = true;
            }
        }
        Object declarations = this.visitDeclarations(ctx.declarations());
        ArrayList<RuleMargin> margins = null;
        if (ctx.margin_rule() != null) {
            margins = new ArrayList<RuleMargin>();
            for (CSSParser.Margin_ruleContext mctx : ctx.margin_rule()) {
                RuleMargin m = this.visitMargin_rule(mctx);
                margins.add(m);
                if (!this.log.isDebugEnabled()) continue;
                this.log.debug("Inserted margin rule #{} into @page", (Object)(margins.size() + 1));
            }
        }
        if (invalid) {
            return null;
        }
        RuleBlock<?> rb = this.preparator.prepareRulePage((List<Declaration>)declarations, margins, name, pseudo);
        if (rb != null) {
            this.preventImports = true;
        }
        return rb;
    }

    @Override
    public RuleMargin visitMargin_rule(CSSParser.Margin_ruleContext ctx) {
        this.logEnter("margin_rule");
        String area = ctx.MARGIN_AREA().getText();
        Object decl = this.visitDeclarations(ctx.declarations());
        RuleMargin m = this.preparator.prepareRuleMargin(this.extractTextUnescaped(area).substring(1), (List<Declaration>)decl);
        this.logLeave("margin_rule");
        return m;
    }

    @Override
    public RuleBlock<?> visitInlineset(CSSParser.InlinesetContext ctx) {
        this.logEnter("inlineset");
        ArrayList<Selector.SelectorPart> pplist = new ArrayList<Selector.SelectorPart>();
        if (ctx.pseudo() != null) {
            for (CSSParser.PseudoContext pctx : ctx.pseudo()) {
                Selector.SelectorPart p = this.visitPseudo(pctx);
                pplist.add(p);
            }
        }
        Object decl = this.visitDeclarations(ctx.declarations());
        RuleBlock<?> is = this.preparator.prepareInlineRuleSet((List<Declaration>)decl, pplist);
        this.logLeave("inlineset");
        return is;
    }

    @Override
    public List<MediaQuery> visitMedia(CSSParser.MediaContext ctx) {
        this.logEnter("media: ", (RuleContext)ctx);
        this.mediaQueryList = new ArrayList<MediaQuery>();
        ArrayList<MediaQuery> queries = this.mediaQueryList;
        for (CSSParser.Media_queryContext mqc : ctx.media_query()) {
            queries.add(this.visitMedia_query(mqc));
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug("Totally returned {} media queries.", (Object)queries.size());
        }
        this.logLeave("media");
        return queries;
    }

    @Override
    public MediaQuery visitMedia_query(CSSParser.Media_queryContext ctx) {
        this.logEnter("mediaquery: ", (RuleContext)ctx);
        this.mq = new mediaquery_scope();
        this.mq.q = this.rf.createMediaQuery();
        this.mq.q.unlock();
        this.mq.state = MediaQueryState.START;
        this.mq.invalid = false;
        this.logLeave("mediaquery");
        for (CSSParser.Media_termContext mtc : ctx.media_term()) {
            this.visitMedia_term(mtc);
        }
        if (this.mq.invalid) {
            this.log.trace("Skipping invalid rule {}", (Object)this.mq.q);
            this.mq.q.setType("all");
            this.mq.q.setNegative(true);
        }
        this.logLeave("mediaquery");
        return this.mq.q;
    }

    @Override
    public Object visitMedia_term(CSSParser.Media_termContext ctx) {
        if (ctx.IDENT() != null) {
            String m = this.extractTextUnescaped(ctx.IDENT().getText());
            MediaQueryState state = this.mq.state;
            if (m.equalsIgnoreCase("ONLY") && state == MediaQueryState.START) {
                this.mq.state = MediaQueryState.TYPEOREXPR;
            } else if (m.equalsIgnoreCase("NOT") && state == MediaQueryState.START) {
                this.mq.q.setNegative(true);
                this.mq.state = MediaQueryState.TYPEOREXPR;
            } else if (m.equalsIgnoreCase("AND") && state == MediaQueryState.AND) {
                this.mq.state = MediaQueryState.EXPR;
            } else if (state == MediaQueryState.START || state == MediaQueryState.TYPE || state == MediaQueryState.TYPEOREXPR) {
                this.mq.q.setType(m);
                this.mq.state = MediaQueryState.AND;
            } else {
                this.log.trace("Invalid media query: found ident: {} state: {}", (Object)m, (Object)state);
                this.mq.invalid = true;
            }
        } else if (ctx.media_expression() != null) {
            MediaExpression e = this.visitMedia_expression(ctx.media_expression());
            if (this.mq.state == MediaQueryState.START || this.mq.state == MediaQueryState.EXPR || this.mq.state == MediaQueryState.TYPEOREXPR) {
                if (e != null && e.getFeature() != null) {
                    this.mq.q.add(e);
                    this.mq.state = MediaQueryState.AND;
                } else {
                    this.log.trace("Invalidating media query for invalud expression");
                    this.mq.invalid = true;
                }
            } else {
                this.log.trace("Invalid media query: found expr, state: {}", (Object)this.mq.state);
                this.mq.invalid = true;
            }
        } else {
            this.mq.invalid = true;
        }
        return null;
    }

    @Override
    public MediaExpression visitMedia_expression(CSSParser.Media_expressionContext ctx) {
        Declaration decl;
        this.logEnter("mediaexpression: ", (RuleContext)ctx);
        if (this.ctxHasErrorNode(ctx)) {
            this.mq.invalid = true;
            return null;
        }
        MediaExpression expr = this.rf.createMediaExpression();
        this.declaration_stack.push(new declaration_scope());
        this.declaration_stack.peek().d = decl = this.rf.createDeclaration();
        this.declaration_stack.peek().invalid = false;
        String property = this.extractTextUnescaped(ctx.IDENT().getText());
        decl.setProperty(property);
        Token token = ctx.IDENT().getSymbol();
        decl.setSource(this.extractSource((CSSToken)token));
        if (ctx.terms() != null) {
            Object t = this.visitTerms(ctx.terms());
            decl.replaceAll(t);
        }
        if (this.declaration_stack.peek().d != null && !this.declaration_stack.peek().invalid) {
            expr.setFeature(decl.getProperty());
            expr.replaceAll(decl);
        }
        this.declaration_stack.pop();
        this.logLeave("mediaexpression");
        return expr;
    }

    @Override
    public RuleBlock<?> visitMedia_rule(CSSParser.Media_ruleContext ctx) {
        this.logEnter("media_rule: ", (RuleContext)ctx);
        Object rules = null;
        if (ctx.ruleset() != null) {
            this.statement_stack.push(new statement_scope());
            rules = this.visitRuleset(ctx.ruleset());
            this.statement_stack.pop();
        } else if (this.log.isDebugEnabled()) {
            this.log.debug("Skiping invalid statement in media");
        }
        this.logLeave("media_rule");
        return rules;
    }

    @Override
    public String visitKeyframes_name(CSSParser.Keyframes_nameContext ctx) {
        if (ctx.IDENT() != null) {
            return this.extractTextUnescaped(ctx.IDENT().getText());
        }
        if (ctx.string() != null) {
            return this.visitString(ctx.string());
        }
        return null;
    }

    @Override
    public KeyframeBlock visitKeyframe_block(CSSParser.Keyframe_blockContext ctx) {
        ArrayList<TermPercent> selectors = null;
        if (ctx.keyframe_selector() != null) {
            selectors = new ArrayList<TermPercent>();
            for (CSSParser.Keyframe_selectorContext selctx : ctx.keyframe_selector()) {
                TermPercent perc = this.visitKeyframe_selector(selctx);
                if (perc == null) continue;
                selectors.add(perc);
            }
        }
        Object declarations = null;
        if (ctx.declarations() != null) {
            this.statement_stack.push(new statement_scope());
            declarations = this.visitDeclarations(ctx.declarations());
            this.statement_stack.pop();
        }
        if (declarations != null && selectors != null && !selectors.isEmpty()) {
            KeyframeBlock block = this.rf.createKeyframeBlock();
            block.setPercentages(selectors);
            block.replaceAll(declarations);
            return block;
        }
        return null;
    }

    @Override
    public TermPercent visitKeyframe_selector(CSSParser.Keyframe_selectorContext ctx) {
        if (ctx.IDENT() != null) {
            String idtext = ctx.IDENT().getText();
            if (idtext != null) {
                if (idtext.equalsIgnoreCase("from")) {
                    return this.tf.createPercent(Float.valueOf(0.0f));
                }
                if (idtext.equalsIgnoreCase("to")) {
                    return this.tf.createPercent(Float.valueOf(100.0f));
                }
                return null;
            }
            return null;
        }
        if (ctx.PERCENTAGE() != null) {
            return this.tf.createPercent(ctx.PERCENTAGE().getText(), 1);
        }
        return null;
    }

    @Override
    public Object visitUnknown_atrule(CSSParser.Unknown_atruleContext ctx) {
        return null;
    }

    @Override
    public Object visitUnknown_atrule_body(CSSParser.Unknown_atrule_bodyContext ctx) {
        return null;
    }

    @Override
    public RuleBlock<?> visitRuleset(CSSParser.RulesetContext ctx) {
        RuleBlock<?> stmnt;
        this.logEnter("ruleset");
        if (this.ctxHasErrorNode(ctx) || ctx.norule() != null) {
            this.log.trace("Leaving ruleset with error {} {}", (Object)this.ctxHasErrorNode(ctx), (Object)(ctx.norule() != null ? 1 : 0));
            return null;
        }
        ArrayList<CombinedSelector> cslist = new ArrayList<CombinedSelector>();
        for (CSSParser.Combined_selectorContext csctx : ctx.combined_selector()) {
            CombinedSelector cs = this.visitCombined_selector(csctx);
            if (cs == null || cs.isEmpty() || this.statement_stack.peek().invalid) continue;
            cslist.add(cs);
            if (!this.log.isDebugEnabled()) continue;
            this.log.debug("Inserted combined selector ({}) into ruleset", (Object)cslist.size());
        }
        Object decl = this.visitDeclarations(ctx.declarations());
        if (this.statement_stack.peek().invalid) {
            stmnt = null;
            if (this.log.isDebugEnabled()) {
                this.log.debug("Ruleset not valid, so not created");
            }
        } else {
            stmnt = this.preparator.prepareRuleSet(cslist, (List<Declaration>)decl, this.wrapMedia != null && !this.wrapMedia.isEmpty(), this.wrapMedia);
            this.preventImports = true;
        }
        this.logLeave("ruleset");
        return stmnt;
    }

    @Override
    public List<Declaration> visitDeclarations(CSSParser.DeclarationsContext ctx) {
        this.logEnter("declarations");
        ArrayList<Declaration> decl = new ArrayList<Declaration>();
        if (ctx != null && ctx.declaration() != null) {
            for (CSSParser.DeclarationContext declctx : ctx.declaration()) {
                Declaration d = this.visitDeclaration(declctx);
                if (d != null) {
                    decl.add(d);
                    if (!this.log.isDebugEnabled()) continue;
                    this.log.debug("Inserted declaration #{} ", (Object)(decl.size() + 1));
                    continue;
                }
                if (!this.log.isDebugEnabled()) continue;
                this.log.debug("Null declaration was omitted");
            }
        }
        this.logLeave("declarations");
        return decl;
    }

    @Override
    public Declaration visitDeclaration(CSSParser.DeclarationContext ctx) {
        Declaration decl;
        this.logEnter("declaration");
        this.declaration_stack.push(new declaration_scope());
        this.declaration_stack.peek().d = decl = this.rf.createDeclaration();
        this.declaration_stack.peek().invalid = false;
        if (ctx.noprop() == null && !this.ctxHasErrorNode(ctx)) {
            if (ctx.important() != null) {
                this.visitImportant(ctx.important());
            }
            this.visitProperty(ctx.property());
            if (ctx.terms() != null) {
                Object t = this.visitTerms(ctx.terms());
                decl.replaceAll(t);
            }
        } else {
            if (this.log.isDebugEnabled()) {
                this.log.debug("invalidating declaration");
            }
            this.declaration_stack.peek().invalid = true;
        }
        if (this.declaration_stack.peek().invalid || this.declaration_stack.isEmpty()) {
            decl = null;
            if (this.log.isDebugEnabled()) {
                this.log.debug("Declaration was invalidated or already invalid");
            }
        } else if (this.log.isDebugEnabled()) {
            this.log.debug("Returning declaration: {}.", (Object)decl);
        }
        this.logLeave("declaration");
        this.declaration_stack.pop();
        return decl;
    }

    @Override
    public Object visitImportant(CSSParser.ImportantContext ctx) {
        if (this.ctxHasErrorNode(ctx)) {
            this.declaration_stack.peek().invalid = true;
        } else {
            this.declaration_stack.peek().d.setImportant(true);
            if (this.log.isDebugEnabled()) {
                this.log.debug("IMPORTANT");
            }
        }
        return null;
    }

    @Override
    public Object visitProperty(CSSParser.PropertyContext ctx) {
        this.logEnter("property");
        String property = this.extractTextUnescaped(ctx.IDENT().getText());
        if (ctx.MINUS() != null) {
            property = ctx.MINUS().getText() + property;
        }
        this.declaration_stack.peek().d.setProperty(property);
        Token token = ctx.IDENT().getSymbol();
        this.declaration_stack.peek().d.setSource(this.extractSource((CSSToken)token));
        if (this.log.isDebugEnabled()) {
            this.log.debug("Setting property: {}", (Object)this.declaration_stack.peek().d.getProperty());
        }
        this.logLeave("property");
        return null;
    }

    @Override
    public List<Term<?>> visitTerms(CSSParser.TermsContext ctx) {
        this.terms_stack.push(new terms_scope());
        this.logEnter("terms");
        ArrayList tlist = new ArrayList();
        this.terms_stack.peek().list = tlist;
        this.terms_stack.peek().term = null;
        this.terms_stack.peek().op = null;
        this.terms_stack.peek().unary = 1;
        this.terms_stack.peek().dash = false;
        if (ctx.term() != null) {
            for (CSSParser.TermContext trmCtx : ctx.term()) {
                if (trmCtx instanceof CSSParser.TermValuePartContext) {
                    this.visitTermValuePart((CSSParser.TermValuePartContext)trmCtx);
                    if (this.declaration_stack.peek().invalid || this.terms_stack.peek().term == null) continue;
                    this.terms_stack.peek().term.setOperator(this.terms_stack.peek().op);
                    this.terms_stack.peek().list.add(this.terms_stack.peek().term);
                    this.terms_stack.peek().op = Term.Operator.SPACE;
                    this.terms_stack.peek().unary = 1;
                    this.terms_stack.peek().dash = false;
                    this.terms_stack.peek().term = null;
                    continue;
                }
                this.visitTermInvalid((CSSParser.TermInvalidContext)trmCtx);
            }
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug("Totally added {} terms", (Object)tlist.size());
        }
        this.logLeave("terms");
        this.terms_stack.pop();
        return tlist;
    }

    @Override
    public Object visitTermValuePart(CSSParser.TermValuePartContext ctx) {
        this.logEnter("term");
        this.visitValuepart(ctx.valuepart());
        return null;
    }

    @Override
    public Object visitTermInvalid(CSSParser.TermInvalidContext ctx) {
        this.logEnter("term");
        this.declaration_stack.peek().invalid = true;
        return null;
    }

    @Override
    public Object visitFunct(CSSParser.FunctContext ctx) {
        if (ctx.EXPRESSION() != null) {
            this.log.warn("Omitting expression " + ctx.getText() + ", expressions are not supported");
            return null;
        }
        Term<List<Term<Object>>> ret = null;
        String fname = this.extractTextUnescaped(ctx.FUNCTION().getText()).toLowerCase();
        if (ctx.funct_args() != null) {
            Term term;
            Object t = this.visitFunct_args(ctx.funct_args());
            ret = fname.equals("url") ? (t == null || t.size() != 1 ? null : ((term = (Term)t.get(0)) instanceof TermString && term.getOperator() == null ? this.tf.createURI((String)((TermString)term).getValue(), this.extractBase(ctx.FUNCTION())) : null)) : (fname.equals("calc") ? (t == null || t.size() == 0 ? null : this.tf.createCalc((List<Term<?>>)t)) : this.tf.createFunction(fname, (List<Term<?>>)t));
        }
        return ret;
    }

    @Override
    public Object visitValuepart(CSSParser.ValuepartContext ctx) {
        this.logEnter("valuepart: ", (RuleContext)ctx);
        if (this.ctxHasErrorNode(ctx)) {
            this.log.error("value part with error");
            this.terms_stack.peek().term = null;
            this.declaration_stack.peek().invalid = true;
            return null;
        }
        if (ctx.MINUS() != null) {
            this.terms_stack.peek().unary = -1;
            this.terms_stack.peek().dash = true;
        }
        if (ctx.COMMA() != null) {
            if (this.log.isDebugEnabled()) {
                this.log.debug("VP - comma");
            }
            this.terms_stack.peek().op = Term.Operator.COMMA;
        } else if (ctx.SLASH() != null) {
            this.terms_stack.peek().op = Term.Operator.SLASH;
        } else if (ctx.string() != null) {
            if (this.log.isDebugEnabled()) {
                this.log.debug("VP - string");
            }
            this.terms_stack.peek().term = this.tf.createString(this.extractTextUnescaped(ctx.string().getText()));
        } else if (ctx.IDENT() != null) {
            if (this.log.isDebugEnabled()) {
                this.log.debug("VP - ident");
            }
            this.terms_stack.peek().term = this.tf.createIdent(this.extractTextUnescaped(ctx.IDENT().getText()), this.terms_stack.peek().dash);
        } else if (ctx.HASH() != null) {
            if (this.log.isDebugEnabled()) {
                this.log.debug("VP - hash");
            }
            this.terms_stack.peek().term = this.tf.createColor(ctx.HASH().getText());
            if (this.terms_stack.peek().term == null) {
                this.declaration_stack.peek().invalid = true;
            }
        } else if (ctx.PERCENTAGE() != null) {
            if (this.log.isDebugEnabled()) {
                this.log.debug("VP - percentage");
            }
            this.terms_stack.peek().term = this.tf.createPercent(ctx.PERCENTAGE().getText(), this.terms_stack.peek().unary);
        } else if (ctx.DIMENSION() != null) {
            if (this.log.isDebugEnabled()) {
                this.log.debug("VP - dimension");
            }
            String dim = ctx.DIMENSION().getText();
            this.terms_stack.peek().term = this.tf.createDimension(dim, this.terms_stack.peek().unary);
            if (this.terms_stack.peek().term == null) {
                this.log.info("Unable to create dimension from {}, unary {}", (Object)dim, (Object)this.terms_stack.peek().unary);
                this.declaration_stack.peek().invalid = true;
            }
        } else if (ctx.NUMBER() != null) {
            if (this.log.isDebugEnabled()) {
                this.log.debug("VP - number");
            }
            this.terms_stack.peek().term = this.tf.createNumeric(ctx.NUMBER().getText(), this.terms_stack.peek().unary);
        } else if (ctx.UNIRANGE() != null) {
            if (this.log.isDebugEnabled()) {
                this.log.debug("VP - unirange");
            }
            this.terms_stack.peek().term = this.tf.createUnicodeRange(ctx.UNIRANGE().getText());
        } else if (ctx.URI() != null) {
            if (this.log.isDebugEnabled()) {
                this.log.debug("VP - uri");
            }
            this.terms_stack.peek().term = this.tf.createURI(this.extractTextUnescaped(ctx.URI().getText()), this.extractBase(ctx.URI()));
        } else if (ctx.UNCLOSED_URI() != null && ((CSSToken)ctx.UNCLOSED_URI().getSymbol()).isValid()) {
            if (this.log.isDebugEnabled()) {
                this.log.debug("VP - unclosed_uri");
            }
            this.terms_stack.peek().term = this.tf.createURI(this.extractTextUnescaped(ctx.UNCLOSED_URI().getText()), this.extractBase(ctx.UNCLOSED_URI()));
        } else if (ctx.funct() != null) {
            this.terms_stack.peek().term = null;
            Term fnterm = (Term)this.visitFunct(ctx.funct());
            if (fnterm != null) {
                if (this.terms_stack.peek().unary == -1) {
                    if (fnterm instanceof TermFunction) {
                        ((TermFunction)fnterm).setFunctionName('-' + ((TermFunction)fnterm).getFunctionName());
                        this.terms_stack.peek().term = fnterm;
                    } else {
                        this.declaration_stack.peek().invalid = true;
                    }
                } else {
                    this.terms_stack.peek().term = fnterm;
                }
            } else {
                this.declaration_stack.peek().invalid = true;
            }
        } else if (ctx.bracketed_idents() != null) {
            if (this.log.isDebugEnabled()) {
                this.log.debug("VP - bracketed_idents");
            }
            this.terms_stack.peek().term = (TermBracketedIdents)this.visitBracketed_idents(ctx.bracketed_idents());
            if (this.terms_stack.peek().term == null) {
                this.declaration_stack.peek().invalid = true;
            }
        } else {
            this.log.error("unhandled valueparts");
            this.terms_stack.peek().term = null;
            this.declaration_stack.peek().invalid = true;
        }
        Term<?> term = this.terms_stack.peek().term;
        if (term != null && (term = this.findSpecificType(term)) != null) {
            this.terms_stack.peek().term = term;
        }
        return null;
    }

    @Override
    public List<Term<?>> visitFunct_args(CSSParser.Funct_argsContext ctx) {
        this.funct_args_stack.push(new funct_args_scope());
        this.funct_args_stack.peek().term = null;
        this.logEnter("funct_args");
        ArrayList tlist = new ArrayList();
        this.funct_args_stack.peek().list = tlist;
        if (ctx.funct_argument() != null) {
            for (CSSParser.Funct_argumentContext argCtx : ctx.funct_argument()) {
                this.visitFunct_argument(argCtx);
                if (this.declaration_stack.peek().invalid || this.funct_args_stack.peek().term == null) continue;
                this.funct_args_stack.peek().list.add(this.funct_args_stack.peek().term);
                this.funct_args_stack.peek().term = null;
            }
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug("Totally added {} args", (Object)tlist.size());
        }
        this.logLeave("funct_args");
        this.funct_args_stack.pop();
        return tlist;
    }

    @Override
    public Object visitFunct_argument(CSSParser.Funct_argumentContext ctx) {
        this.logEnter("funct_argument: ", (RuleContext)ctx);
        if (this.ctxHasErrorNode(ctx)) {
            this.log.error("argument with error");
            this.funct_args_stack.peek().term = null;
            this.declaration_stack.peek().invalid = true;
            return null;
        }
        if (ctx.PLUS() != null) {
            if (this.log.isDebugEnabled()) {
                this.log.debug("FA - plus");
            }
            this.funct_args_stack.peek().term = this.tf.createOperator('+');
        } else if (ctx.MINUS() != null) {
            if (this.log.isDebugEnabled()) {
                this.log.debug("FA - minus");
            }
            this.funct_args_stack.peek().term = this.tf.createOperator('-');
        } else if (ctx.ASTERISK() != null) {
            if (this.log.isDebugEnabled()) {
                this.log.debug("FA - *");
            }
            this.funct_args_stack.peek().term = this.tf.createOperator('*');
        } else if (ctx.SLASH() != null) {
            if (this.log.isDebugEnabled()) {
                this.log.debug("FA - /");
            }
            this.funct_args_stack.peek().term = this.tf.createOperator('/');
        } else if (ctx.LPAREN() != null) {
            if (this.log.isDebugEnabled()) {
                this.log.debug("FA - (");
            }
            this.funct_args_stack.peek().term = this.tf.createOperator('(');
        } else if (ctx.RPAREN() != null) {
            if (this.log.isDebugEnabled()) {
                this.log.debug("FA - )");
            }
            this.funct_args_stack.peek().term = this.tf.createOperator(')');
        } else if (ctx.COMMA() != null) {
            if (this.log.isDebugEnabled()) {
                this.log.debug("FA - comma");
            }
            this.funct_args_stack.peek().term = this.tf.createOperator(',');
        } else if (ctx.string() != null) {
            if (this.log.isDebugEnabled()) {
                this.log.debug("FA - string");
            }
            this.funct_args_stack.peek().term = this.tf.createString(this.extractTextUnescaped(ctx.string().getText()));
        } else if (ctx.IDENT() != null) {
            if (this.log.isDebugEnabled()) {
                this.log.debug("FA - ident");
            }
            this.funct_args_stack.peek().term = this.tf.createIdent(this.extractTextUnescaped(ctx.IDENT().getText()));
        } else if (ctx.PERCENTAGE() != null) {
            if (this.log.isDebugEnabled()) {
                this.log.debug("FA - percentage");
            }
            this.funct_args_stack.peek().term = this.tf.createPercent(ctx.PERCENTAGE().getText(), 1);
        } else if (ctx.DIMENSION() != null) {
            if (this.log.isDebugEnabled()) {
                this.log.debug("FA - dimension");
            }
            String dim = ctx.DIMENSION().getText();
            this.funct_args_stack.peek().term = this.tf.createDimension(dim, 1);
            if (this.funct_args_stack.peek().term == null) {
                this.log.info("Unable to create dimension from {}, unary {}", (Object)dim, (Object)1);
                this.declaration_stack.peek().invalid = true;
            }
        } else if (ctx.HASH() != null) {
            if (this.log.isDebugEnabled()) {
                this.log.debug("FA - hash");
            }
            this.funct_args_stack.peek().term = this.tf.createColor(ctx.HASH().getText());
            if (this.funct_args_stack.peek().term == null) {
                this.declaration_stack.peek().invalid = true;
            }
        } else if (ctx.NUMBER() != null) {
            if (this.log.isDebugEnabled()) {
                this.log.debug("FA - number");
            }
            this.funct_args_stack.peek().term = this.tf.createNumeric(ctx.NUMBER().getText(), 1);
        } else if (ctx.funct() != null) {
            if (this.log.isDebugEnabled()) {
                this.log.debug("FA - funct");
            }
            this.funct_args_stack.peek().term = null;
            Term fnterm = (Term)this.visitFunct(ctx.funct());
            if (fnterm != null) {
                this.funct_args_stack.peek().term = fnterm;
            } else {
                this.declaration_stack.peek().invalid = true;
            }
        } else {
            this.log.error("unhandled funct_args");
            this.funct_args_stack.peek().term = null;
            this.declaration_stack.peek().invalid = true;
        }
        Term<?> term = this.funct_args_stack.peek().term;
        if (term != null && (term = this.findSpecificType(term)) != null) {
            this.funct_args_stack.peek().term = term;
        }
        return null;
    }

    @Override
    public Object visitBracketed_idents(CSSParser.Bracketed_identsContext ctx) {
        if (ctx.INVALID_STATEMENT() != null) {
            if (this.log.isDebugEnabled()) {
                this.log.debug("VP - ident invalid");
            }
            return null;
        }
        TermBracketedIdents ret = this.tf.createBracketedIdents();
        if (ctx.ident_list_item() != null) {
            for (CSSParser.Ident_list_itemContext ictx : ctx.ident_list_item()) {
                TermIdent t = (TermIdent)this.visitIdent_list_item(ictx);
                if (t != null) {
                    ret.add(t);
                    continue;
                }
                return null;
            }
        }
        return ret;
    }

    @Override
    public Object visitIdent_list_item(CSSParser.Ident_list_itemContext ctx) {
        boolean dash = false;
        if (ctx.INVALID_STATEMENT() != null) {
            if (this.log.isDebugEnabled()) {
                this.log.debug("VP - ident invalid");
            }
            return null;
        }
        if (ctx.MINUS() != null) {
            dash = true;
        }
        if (ctx.IDENT() != null) {
            if (this.log.isDebugEnabled()) {
                this.log.debug("VP - ident item");
            }
            return this.tf.createIdent(this.extractTextUnescaped(ctx.IDENT().getText()), dash);
        }
        return null;
    }

    @Override
    public CombinedSelector visitCombined_selector(CSSParser.Combined_selectorContext ctx) {
        this.logEnter("combined_selector");
        this.combined_selector_stack.push(new combined_selector_scope());
        CombinedSelector combinedSelector = (CombinedSelector)this.rf.createCombinedSelector().unlock();
        Selector s = this.visitSelector(ctx.selector(0));
        combinedSelector.add(s);
        for (int i = 1; i < ctx.selector().size(); ++i) {
            Selector.Combinator c = this.visitCombinator(ctx.combinator(i - 1));
            s = this.visitSelector(ctx.selector(i));
            s.setCombinator(c);
            combinedSelector.add(s);
        }
        if (this.statement_stack.peek().invalid || this.combined_selector_stack.peek().invalid) {
            combinedSelector = null;
            if (this.log.isDebugEnabled()) {
                if (this.statement_stack.peek().invalid) {
                    this.log.debug("Ommiting combined selector, whole statement discarded");
                } else {
                    this.log.debug("Combined selector is invalid");
                }
            }
            this.statement_stack.peek().invalid = true;
        } else if (this.log.isDebugEnabled()) {
            this.log.debug("Returing combined selector: {}.", (Object)combinedSelector);
        }
        this.combined_selector_stack.pop();
        this.logLeave("combined_selector");
        return combinedSelector;
    }

    @Override
    public Selector.Combinator visitCombinator(CSSParser.CombinatorContext ctx) {
        this.logEnter("combinator");
        if (ctx.GREATER() != null) {
            return Selector.Combinator.CHILD;
        }
        if (ctx.PLUS() != null) {
            return Selector.Combinator.ADJACENT;
        }
        if (ctx.TILDE() != null) {
            return Selector.Combinator.PRECEDING;
        }
        return Selector.Combinator.DESCENDANT;
    }

    @Override
    public Selector visitSelector(CSSParser.SelectorContext ctx) {
        Selector sel;
        if (this.ctxHasErrorNode(ctx)) {
            this.statement_stack.peek().invalid = true;
            return null;
        }
        this.selector_stack.push(new selector_scope());
        this.logEnter("selector");
        this.selector_stack.peek().s = sel = (Selector)this.rf.createSelector().unlock();
        if (ctx.IDENT() != null || ctx.ASTERISK() != null) {
            Selector.ElementName en = this.rf.createElement("*");
            if (ctx.IDENT() != null) {
                en.setName(this.extractTextUnescaped(ctx.IDENT().getText()));
            }
            this.selector_stack.peek().s.add(en);
        }
        for (CSSParser.SelpartContext selpartctx : ctx.selpart()) {
            this.visitSelpart(selpartctx);
        }
        this.logLeave("selector");
        this.selector_stack.pop();
        return sel;
    }

    @Override
    public Object visitSelpart(CSSParser.SelpartContext ctx) {
        this.logEnter("selpart");
        if (ctx.HASH() != null) {
            String ident = this.extractIdUnescaped(ctx.HASH().getText());
            if (ident != null) {
                this.selector_stack.peek().s.add(this.rf.createID(ident));
            } else {
                this.combined_selector_stack.peek().invalid = true;
            }
        } else if (ctx.CLASSKEYWORD() != null) {
            this.selector_stack.peek().s.add(this.rf.createClass(this.extractTextUnescaped(ctx.CLASSKEYWORD().getText())));
        } else if (ctx.attribute() != null) {
            Selector.ElementAttribute ea = this.visitAttribute(ctx.attribute());
            this.selector_stack.peek().s.add(ea);
        } else if (ctx.pseudo() != null) {
            Selector.SelectorPart p = this.visitPseudo(ctx.pseudo());
            if (p != null) {
                if (p instanceof Selector.PseudoElement && this.selector_stack.peek().s.getPseudoElementType() != null) {
                    this.log.warn("Invalid selector with multiple pseudo-elements");
                    this.combined_selector_stack.peek().invalid = true;
                } else {
                    this.selector_stack.peek().s.add(p);
                }
            } else {
                this.combined_selector_stack.peek().invalid = true;
            }
        } else {
            this.combined_selector_stack.peek().invalid = true;
        }
        this.logLeave("selpart");
        return null;
    }

    @Override
    public Selector.ElementAttribute visitAttribute(CSSParser.AttributeContext ctx) {
        this.logEnter("attribute: ", (RuleContext)ctx);
        String attributeName = this.extractTextUnescaped(((ParseTree)ctx.children.get(0)).getText());
        String value = null;
        boolean isStringValue = false;
        Selector.Operator op = Selector.Operator.NO_OPERATOR;
        List<ParseTree> ctx2 = this.filterSpaceTokens(ctx.children);
        if (ctx2.size() == 3) {
            CommonToken opToken = (CommonToken)((TerminalNodeImpl)ctx2.get((int)1)).symbol;
            isStringValue = ctx2.get(2) instanceof CSSParser.StringContext;
            value = isStringValue ? ctx2.get(2).getText() : ctx2.get(2).getText();
            value = this.extractTextUnescaped(value);
            switch (opToken.getType()) {
                case 58: {
                    op = Selector.Operator.EQUALS;
                    break;
                }
                case 83: {
                    op = Selector.Operator.INCLUDES;
                    break;
                }
                case 84: {
                    op = Selector.Operator.DASHMATCH;
                    break;
                }
                case 87: {
                    op = Selector.Operator.CONTAINS;
                    break;
                }
                case 85: {
                    op = Selector.Operator.STARTSWITH;
                    break;
                }
                case 86: {
                    op = Selector.Operator.ENDSWITH;
                    break;
                }
                default: {
                    op = Selector.Operator.NO_OPERATOR;
                }
            }
        }
        Selector.ElementAttribute elemAttr = null;
        if (attributeName != null) {
            elemAttr = this.rf.createAttribute(value, isStringValue, op, attributeName);
        } else {
            if (this.log.isDebugEnabled()) {
                this.log.debug("Invalid attribute element in selector");
            }
            this.combined_selector_stack.peek().invalid = true;
        }
        this.logLeave("attribute");
        return elemAttr;
    }

    @Override
    public Selector.SelectorPart visitPseudo(CSSParser.PseudoContext ctx) {
        String name;
        this.logEnter("pseudo: ", (RuleContext)ctx);
        boolean isPseudoElem = ctx.COLON().size() > 1;
        Selector.SelectorPart pseudo = null;
        if (ctx.FUNCTION() != null) {
            name = this.extractTextUnescaped(ctx.FUNCTION().getText());
            if (ctx.selector() != null) {
                Selector sel = this.visitSelector(ctx.selector());
                pseudo = isPseudoElem ? this.rf.createPseudoElement(name, sel) : this.rf.createPseudoClass(name, sel);
            } else {
                String value;
                String string = value = ctx.MINUS() == null ? "" : "-";
                if (ctx.IDENT() != null) {
                    value = value + ctx.IDENT().getText();
                } else if (ctx.NUMBER() != null) {
                    value = value + ctx.NUMBER().getText();
                } else if (ctx.INDEX() != null) {
                    value = value + ctx.INDEX().getText();
                } else {
                    throw new UnsupportedOperationException("unknown state");
                }
                pseudo = isPseudoElem ? this.rf.createPseudoElement(name, value) : this.rf.createPseudoClass(name, value);
            }
        } else if (ctx.IDENT() != null) {
            name = this.extractTextUnescaped(ctx.IDENT().getText());
            if (ctx.MINUS() != null) {
                name = ctx.MINUS().getText() + name;
            }
            if (!isPseudoElem && ("after".equalsIgnoreCase(name) || "before".equalsIgnoreCase(name) || "first-line".equalsIgnoreCase(name) || "first-letter".equalsIgnoreCase(name))) {
                isPseudoElem = true;
            }
            pseudo = isPseudoElem ? this.rf.createPseudoElement(name) : (ctx.parent instanceof CSSParser.PageContext ? this.rf.createPseudoPage(name) : this.rf.createPseudoClass(name));
        } else {
            name = "";
        }
        if (pseudo == null || pseudo instanceof Selector.PseudoPage && ((Selector.PseudoPage)pseudo).getType() == null || pseudo instanceof Selector.PseudoClass && ((Selector.PseudoClass)pseudo).getType() == null || pseudo instanceof Selector.PseudoElement && ((Selector.PseudoElement)pseudo).getType() == null) {
            this.log.error("invalid pseudo declaration: " + name);
            pseudo = null;
        }
        this.logLeave("pseudo");
        return pseudo;
    }

    @Override
    public String visitString(CSSParser.StringContext ctx) {
        if (ctx.INVALID_STRING() != null) {
            return null;
        }
        return this.extractTextUnescaped(ctx.getText());
    }

    @Override
    public Object visitAny(CSSParser.AnyContext ctx) {
        return null;
    }

    @Override
    public Object visitNostatement(CSSParser.NostatementContext ctx) {
        return null;
    }

    @Override
    public Object visitNoprop(CSSParser.NopropContext ctx) {
        return null;
    }

    @Override
    public Object visitNorule(CSSParser.NoruleContext ctx) {
        return null;
    }

    @Override
    public Object visitNomediaquery(CSSParser.NomediaqueryContext ctx) {
        return null;
    }

    public Object visit(ParseTree parseTree) {
        this.logEnter("visit");
        if (this.log.isDebugEnabled()) {
            this.log.debug(parseTree.getText());
        }
        this.logLeave("visit");
        return null;
    }

    public Object visitChildren(RuleNode ruleNode) {
        return null;
    }

    public Object visitTerminal(TerminalNode terminalNode) {
        return null;
    }

    public Object visitErrorNode(ErrorNode errorNode) {
        this.log.error("visitErrorNode");
        return null;
    }

    protected static class selector_scope {
        Selector s;

        protected selector_scope() {
        }
    }

    protected static class combined_selector_scope {
        boolean invalid;

        protected combined_selector_scope() {
        }
    }

    protected static class funct_args_scope {
        List<Term<?>> list;
        Term<?> term;

        protected funct_args_scope() {
        }
    }

    protected static class terms_scope {
        List<Term<?>> list;
        Term<?> term;
        Term.Operator op;
        int unary;
        boolean dash;

        protected terms_scope() {
        }
    }

    protected static class declaration_scope {
        Declaration d;
        boolean invalid;

        protected declaration_scope() {
        }
    }

    protected static class mediaquery_scope {
        MediaQuery q;
        MediaQueryState state;
        boolean invalid;

        protected mediaquery_scope() {
        }
    }

    protected static class statement_scope {
        boolean invalid = false;

        protected statement_scope() {
        }
    }

    private static enum MediaQueryState {
        START,
        TYPE,
        AND,
        EXPR,
        TYPEOREXPR;

    }
}

