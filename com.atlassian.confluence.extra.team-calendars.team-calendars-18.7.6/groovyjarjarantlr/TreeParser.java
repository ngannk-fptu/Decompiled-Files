/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr;

import groovyjarjarantlr.ASTFactory;
import groovyjarjarantlr.ASTNULLType;
import groovyjarjarantlr.MismatchedTokenException;
import groovyjarjarantlr.RecognitionException;
import groovyjarjarantlr.TreeParserSharedInputState;
import groovyjarjarantlr.Utils;
import groovyjarjarantlr.collections.AST;
import groovyjarjarantlr.collections.impl.BitSet;

public class TreeParser {
    public static ASTNULLType ASTNULL = new ASTNULLType();
    protected AST _retTree;
    protected TreeParserSharedInputState inputState;
    protected String[] tokenNames;
    protected AST returnAST;
    protected ASTFactory astFactory = new ASTFactory();
    protected int traceDepth = 0;

    public TreeParser() {
        this.inputState = new TreeParserSharedInputState();
    }

    public AST getAST() {
        return this.returnAST;
    }

    public ASTFactory getASTFactory() {
        return this.astFactory;
    }

    public String getTokenName(int n) {
        return this.tokenNames[n];
    }

    public String[] getTokenNames() {
        return this.tokenNames;
    }

    protected void match(AST aST, int n) throws MismatchedTokenException {
        if (aST == null || aST == ASTNULL || aST.getType() != n) {
            throw new MismatchedTokenException(this.getTokenNames(), aST, n, false);
        }
    }

    public void match(AST aST, BitSet bitSet) throws MismatchedTokenException {
        if (aST == null || aST == ASTNULL || !bitSet.member(aST.getType())) {
            throw new MismatchedTokenException(this.getTokenNames(), aST, bitSet, false);
        }
    }

    protected void matchNot(AST aST, int n) throws MismatchedTokenException {
        if (aST == null || aST == ASTNULL || aST.getType() == n) {
            throw new MismatchedTokenException(this.getTokenNames(), aST, n, true);
        }
    }

    public static void panic() {
        System.err.println("TreeWalker: panic");
        Utils.error("");
    }

    public void reportError(RecognitionException recognitionException) {
        System.err.println(recognitionException.toString());
    }

    public void reportError(String string) {
        System.err.println("error: " + string);
    }

    public void reportWarning(String string) {
        System.err.println("warning: " + string);
    }

    public void setASTFactory(ASTFactory aSTFactory) {
        this.astFactory = aSTFactory;
    }

    public void setASTNodeType(String string) {
        this.setASTNodeClass(string);
    }

    public void setASTNodeClass(String string) {
        this.astFactory.setASTNodeType(string);
    }

    public void traceIndent() {
        for (int i = 0; i < this.traceDepth; ++i) {
            System.out.print(" ");
        }
    }

    public void traceIn(String string, AST aST) {
        ++this.traceDepth;
        this.traceIndent();
        System.out.println("> " + string + "(" + (aST != null ? ((Object)aST).toString() : "null") + ")" + (this.inputState.guessing > 0 ? " [guessing]" : ""));
    }

    public void traceOut(String string, AST aST) {
        this.traceIndent();
        System.out.println("< " + string + "(" + (aST != null ? ((Object)aST).toString() : "null") + ")" + (this.inputState.guessing > 0 ? " [guessing]" : ""));
        --this.traceDepth;
    }
}

