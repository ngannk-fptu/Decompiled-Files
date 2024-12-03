/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  antlr.ASTPair
 *  antlr.MismatchedTokenException
 *  antlr.RecognitionException
 *  antlr.Token
 *  antlr.TokenStreamException
 *  antlr.collections.AST
 */
package org.hibernate.hql.internal.ast;

import antlr.ASTPair;
import antlr.MismatchedTokenException;
import antlr.RecognitionException;
import antlr.Token;
import antlr.TokenStreamException;
import antlr.collections.AST;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.hibernate.QueryException;
import org.hibernate.hql.internal.antlr.HqlBaseParser;
import org.hibernate.hql.internal.ast.ErrorTracker;
import org.hibernate.hql.internal.ast.HqlASTFactory;
import org.hibernate.hql.internal.ast.HqlLexer;
import org.hibernate.hql.internal.ast.HqlToken;
import org.hibernate.hql.internal.ast.ParseErrorHandler;
import org.hibernate.hql.internal.ast.util.ASTUtil;
import org.hibernate.hql.internal.ast.util.TokenPrinters;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.util.StringHelper;

public final class HqlParser
extends HqlBaseParser {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(HqlParser.class);
    private final ParseErrorHandler parseErrorHandler;
    private int traceDepth;
    private Map<String, Set<String>> treatMap;

    public static HqlParser getInstance(String hql) {
        return new HqlParser(hql);
    }

    private HqlParser(String hql) {
        super(new HqlLexer(new StringReader(hql)));
        this.parseErrorHandler = new ErrorTracker(hql);
        this.setASTFactory(new HqlASTFactory());
    }

    public void traceIn(String ruleName) {
        if (!LOG.isTraceEnabled()) {
            return;
        }
        if (this.inputState.guessing > 0) {
            return;
        }
        String prefix = StringHelper.repeat('-', this.traceDepth++ * 2) + "-> ";
        LOG.trace(prefix + ruleName);
    }

    public void traceOut(String ruleName) {
        if (!LOG.isTraceEnabled()) {
            return;
        }
        if (this.inputState.guessing > 0) {
            return;
        }
        String prefix = "<-" + StringHelper.repeat('-', --this.traceDepth * 2) + " ";
        LOG.trace(prefix + ruleName);
    }

    public void reportError(RecognitionException e) {
        this.parseErrorHandler.reportError(e);
    }

    public void reportError(String s) {
        this.parseErrorHandler.reportError(s);
    }

    public void reportWarning(String s) {
        this.parseErrorHandler.reportWarning(s);
    }

    public ParseErrorHandler getParseErrorHandler() {
        return this.parseErrorHandler;
    }

    @Override
    public AST handleIdentifierError(Token token, RecognitionException ex) throws RecognitionException, TokenStreamException {
        HqlToken hqlToken;
        if (token instanceof HqlToken && (hqlToken = (HqlToken)token).isPossibleID() && ex instanceof MismatchedTokenException) {
            MismatchedTokenException mte = (MismatchedTokenException)ex;
            if (mte.expecting == 111) {
                this.reportWarning("Keyword  '" + token.getText() + "' is being interpreted as an identifier due to: " + mte.getMessage());
                ASTPair currentAST = new ASTPair();
                token.setType(99);
                this.astFactory.addASTChild(currentAST, this.astFactory.create(token));
                this.consume();
                return currentAST.root;
            }
        }
        return super.handleIdentifierError(token, ex);
    }

    @Override
    public AST negateNode(AST x) {
        switch (x.getType()) {
            case 41: {
                x.setType(6);
                x.setText("{and}");
                x.setFirstChild(this.negateNode(x.getFirstChild()));
                x.getFirstChild().setNextSibling(this.negateNode(x.getFirstChild().getNextSibling()));
                return x;
            }
            case 6: {
                x.setType(41);
                x.setText("{or}");
                x.setFirstChild(this.negateNode(x.getFirstChild()));
                x.getFirstChild().setNextSibling(this.negateNode(x.getFirstChild().getNextSibling()));
                return x;
            }
            case 108: {
                x.setType(115);
                x.setText("{not}" + x.getText());
                return x;
            }
            case 115: {
                x.setType(108);
                x.setText("{not}" + x.getText());
                return x;
            }
            case 118: {
                x.setType(119);
                x.setText("{not}" + x.getText());
                return x;
            }
            case 117: {
                x.setType(120);
                x.setText("{not}" + x.getText());
                return x;
            }
            case 120: {
                x.setType(117);
                x.setText("{not}" + x.getText());
                return x;
            }
            case 119: {
                x.setType(118);
                x.setText("{not}" + x.getText());
                return x;
            }
            case 35: {
                x.setType(89);
                x.setText("{not}" + x.getText());
                return x;
            }
            case 89: {
                x.setType(35);
                x.setText("{not}" + x.getText());
                return x;
            }
            case 27: {
                x.setType(88);
                x.setText("{not}" + x.getText());
                return x;
            }
            case 88: {
                x.setType(27);
                x.setText("{not}" + x.getText());
                return x;
            }
            case 85: {
                x.setType(84);
                x.setText("{not}" + x.getText());
                return x;
            }
            case 84: {
                x.setType(85);
                x.setText("{not}" + x.getText());
                return x;
            }
            case 10: {
                x.setType(87);
                x.setText("{not}" + x.getText());
                return x;
            }
            case 87: {
                x.setType(10);
                x.setText("{not}" + x.getText());
                return x;
            }
        }
        AST not = super.negateNode(x);
        if (not != x) {
            not.setNextSibling(x.getNextSibling());
            x.setNextSibling(null);
        }
        return not;
    }

    @Override
    public AST processEqualityExpression(AST x) {
        if (x == null) {
            LOG.processEqualityExpression();
            return null;
        }
        int type = x.getType();
        if (type == 108 || type == 115) {
            boolean negated;
            boolean bl = negated = type == 115;
            if (x.getNumberOfChildren() == 2) {
                AST a = x.getFirstChild();
                AST b = a.getNextSibling();
                if (a.getType() == 40 && b.getType() != 40) {
                    return this.createIsNullParent(b, negated);
                }
                if (b.getType() == 40 && a.getType() != 40) {
                    return this.createIsNullParent(a, negated);
                }
                if (b.getType() == 65) {
                    return this.processIsEmpty(a, negated);
                }
                return x;
            }
            return x;
        }
        return x;
    }

    private AST createIsNullParent(AST node, boolean negated) {
        node.setNextSibling(null);
        int type = negated ? 84 : 85;
        String text = negated ? "is not null" : "is null";
        return ASTUtil.createParent(this.astFactory, type, text, node);
    }

    private AST processIsEmpty(AST node, boolean negated) {
        node.setNextSibling(null);
        AST ast = this.createSubquery(node);
        ast = ASTUtil.createParent(this.astFactory, 19, "exists", ast);
        if (!negated) {
            ast = ASTUtil.createParent(this.astFactory, 39, "not", ast);
        }
        return ast;
    }

    private AST createSubquery(AST node) {
        AST ast = ASTUtil.createParent(this.astFactory, 92, "RANGE", node);
        ast = ASTUtil.createParent(this.astFactory, 23, "from", ast);
        AST alias = ASTUtil.createSibling(this.astFactory, 75, "_", node);
        ASTUtil.insertChild(ASTUtil.createSibling(this.astFactory, 46, "select", ast), this.astFactory.create(111, alias.getText()));
        ast = ASTUtil.createParent(this.astFactory, 94, "SELECT_FROM", ast);
        ast = ASTUtil.createParent(this.astFactory, 91, "QUERY", ast);
        return ast;
    }

    public void showAst(AST ast, PrintStream out) {
        this.showAst(ast, new PrintWriter(out));
    }

    private void showAst(AST ast, PrintWriter pw) {
        TokenPrinters.HQL_TOKEN_PRINTER.showAst(ast, pw);
    }

    @Override
    public void matchOptionalFrom() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST optionalFrom_AST = null;
        if (this.LA(1) == 23 && this.LA(2) != 15) {
            this.match(23);
            this.returnAST = optionalFrom_AST = currentAST.root;
        }
    }

    @Override
    public void firstPathTokenWeakKeywords() throws TokenStreamException {
        int t = this.LA(1);
        switch (t) {
            case 15: {
                this.LT(0).setType(111);
            }
        }
    }

    @Override
    public void handlePrimaryExpressionDotIdent() throws TokenStreamException {
        HqlToken t;
        if (this.LA(2) == 15 && this.LA(3) != 111 && (t = (HqlToken)this.LT(3)).isPossibleID()) {
            t.setType(111);
            if (LOG.isDebugEnabled()) {
                LOG.debugf("handleDotIdent() : new LT(3) token - %s", this.LT(1));
            }
        }
    }

    @Override
    public void weakKeywords() throws TokenStreamException {
        int t = this.LA(1);
        switch (t) {
            case 25: 
            case 42: {
                if (this.LA(2) == 112) break;
                this.LT(1).setType(111);
                if (!LOG.isDebugEnabled()) break;
                LOG.debugf("weakKeywords() : new LT(1) token - %s", this.LT(1));
                break;
            }
            default: {
                HqlToken hqlToken;
                if (this.LA(0) != 23 || t == 111 || this.LA(2) != 15 || !(hqlToken = (HqlToken)this.LT(1)).isPossibleID()) break;
                hqlToken.setType(111);
                if (!LOG.isDebugEnabled()) break;
                LOG.debugf("weakKeywords() : new LT(1) token - %s", this.LT(1));
            }
        }
    }

    @Override
    public void expectNamedParameterName() throws TokenStreamException {
        HqlToken nextToken;
        if (this.LA(1) != 111 && (nextToken = (HqlToken)this.LT(1)).isPossibleID()) {
            LOG.debugf("Converting keyword [%s] following COLON to IDENT as an expected parameter name", nextToken.getText());
            nextToken.setType(111);
        }
    }

    @Override
    public void handleDotIdent() throws TokenStreamException {
        HqlToken t;
        if (this.LA(1) == 15 && this.LA(2) != 111 && (t = (HqlToken)this.LT(2)).isPossibleID()) {
            this.LT(2).setType(111);
            if (LOG.isDebugEnabled()) {
                LOG.debugf("handleDotIdent() : new LT(2) token - %s", this.LT(1));
            }
        }
    }

    @Override
    public void processMemberOf(Token n, AST p, ASTPair currentAST) {
        AST inNode = n == null ? this.astFactory.create(27, "in") : this.astFactory.create(88, "not in");
        this.astFactory.makeASTRoot(currentAST, inNode);
        AST inListNode = this.astFactory.create(82, "inList");
        inNode.addChild(inListNode);
        AST elementsNode = this.astFactory.create(17, "elements");
        inListNode.addChild(elementsNode);
        elementsNode.addChild(p);
    }

    @Override
    protected void registerTreat(AST pathToTreat, AST treatAs) {
        Set<String> subclassNames;
        String path = this.toPathText(pathToTreat);
        String subclassName = this.toPathText(treatAs);
        LOG.debugf("Registering discovered request to treat(%s as %s)", path, subclassName);
        if (this.treatMap == null) {
            this.treatMap = new HashMap<String, Set<String>>();
        }
        if ((subclassNames = this.treatMap.get(path)) == null) {
            subclassNames = new HashSet<String>();
            this.treatMap.put(path, subclassNames);
        }
        subclassNames.add(subclassName);
    }

    private String toPathText(AST node) {
        String text = node.getText();
        if (text.equals(".") && node.getFirstChild() != null && node.getFirstChild().getNextSibling() != null && node.getFirstChild().getNextSibling().getNextSibling() == null) {
            return this.toPathText(node.getFirstChild()) + '.' + this.toPathText(node.getFirstChild().getNextSibling());
        }
        return text;
    }

    public Map<String, Set<String>> getTreatMap() {
        return this.treatMap == null ? Collections.emptyMap() : this.treatMap;
    }

    public static void panic() {
        throw new QueryException("Parser: panic");
    }
}

