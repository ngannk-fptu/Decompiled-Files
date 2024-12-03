/*
 * Decompiled with CFR 0.152.
 */
package antlr.debug;

import antlr.CommonToken;
import antlr.LLkParser;
import antlr.MismatchedTokenException;
import antlr.ParseTree;
import antlr.ParseTreeRule;
import antlr.ParseTreeToken;
import antlr.ParserSharedInputState;
import antlr.TokenBuffer;
import antlr.TokenStream;
import antlr.TokenStreamException;
import antlr.collections.impl.BitSet;
import java.util.Stack;

public class ParseTreeDebugParser
extends LLkParser {
    protected Stack currentParseTreeRoot = new Stack();
    protected ParseTreeRule mostRecentParseTreeRoot = null;
    protected int numberOfDerivationSteps = 1;

    public ParseTreeDebugParser(int n) {
        super(n);
    }

    public ParseTreeDebugParser(ParserSharedInputState parserSharedInputState, int n) {
        super(parserSharedInputState, n);
    }

    public ParseTreeDebugParser(TokenBuffer tokenBuffer, int n) {
        super(tokenBuffer, n);
    }

    public ParseTreeDebugParser(TokenStream tokenStream, int n) {
        super(tokenStream, n);
    }

    public ParseTree getParseTree() {
        return this.mostRecentParseTreeRoot;
    }

    public int getNumberOfDerivationSteps() {
        return this.numberOfDerivationSteps;
    }

    public void match(int n) throws MismatchedTokenException, TokenStreamException {
        this.addCurrentTokenToParseTree();
        super.match(n);
    }

    public void match(BitSet bitSet) throws MismatchedTokenException, TokenStreamException {
        this.addCurrentTokenToParseTree();
        super.match(bitSet);
    }

    public void matchNot(int n) throws MismatchedTokenException, TokenStreamException {
        this.addCurrentTokenToParseTree();
        super.matchNot(n);
    }

    protected void addCurrentTokenToParseTree() throws TokenStreamException {
        if (this.inputState.guessing > 0) {
            return;
        }
        ParseTreeRule parseTreeRule = (ParseTreeRule)this.currentParseTreeRoot.peek();
        ParseTreeToken parseTreeToken = null;
        parseTreeToken = this.LA(1) == 1 ? new ParseTreeToken(new CommonToken("EOF")) : new ParseTreeToken(this.LT(1));
        parseTreeRule.addChild(parseTreeToken);
    }

    public void traceIn(String string) throws TokenStreamException {
        if (this.inputState.guessing > 0) {
            return;
        }
        ParseTreeRule parseTreeRule = new ParseTreeRule(string);
        if (this.currentParseTreeRoot.size() > 0) {
            ParseTreeRule parseTreeRule2 = (ParseTreeRule)this.currentParseTreeRoot.peek();
            parseTreeRule2.addChild(parseTreeRule);
        }
        this.currentParseTreeRoot.push(parseTreeRule);
        ++this.numberOfDerivationSteps;
    }

    public void traceOut(String string) throws TokenStreamException {
        if (this.inputState.guessing > 0) {
            return;
        }
        this.mostRecentParseTreeRoot = (ParseTreeRule)this.currentParseTreeRoot.pop();
    }
}

